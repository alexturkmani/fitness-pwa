// Shared Google Play Developer API helpers for verify-purchase and rtdn-webhook.
// Subscription state always comes from Google, never from the caller.

export const PLAY_PACKAGE_NAME = Deno.env.get("PLAY_PACKAGE_NAME") ?? "com.nexal.app";
const GOOGLE_SERVICE_ACCOUNT_JSON = Deno.env.get("GOOGLE_SERVICE_ACCOUNT_JSON");

// Get Google OAuth2 access token from service account
export async function getGoogleAccessToken(): Promise<string> {
  // Without this check a missing secret surfaces as `"undefined" is not valid
  // JSON`, which says nothing about the actual cause and sends you looking in
  // the app instead of the config.
  if (!GOOGLE_SERVICE_ACCOUNT_JSON) {
    throw new Error(
      "GOOGLE_SERVICE_ACCOUNT_JSON is not set. Add it in Supabase " +
        "(Edge Functions -> Secrets) with the Play service account key.",
    );
  }

  let sa: { client_email: string; private_key: string };
  try {
    sa = JSON.parse(GOOGLE_SERVICE_ACCOUNT_JSON);
  } catch {
    throw new Error("GOOGLE_SERVICE_ACCOUNT_JSON is set but is not valid JSON.");
  }
  if (!sa.client_email || !sa.private_key) {
    throw new Error(
      "GOOGLE_SERVICE_ACCOUNT_JSON is missing client_email or private_key.",
    );
  }
  const now = Math.floor(Date.now() / 1000);

  // Create JWT header and claim set
  const header = { alg: "RS256", typ: "JWT" };
  const claimSet = {
    iss: sa.client_email,
    scope: "https://www.googleapis.com/auth/androidpublisher",
    aud: "https://oauth2.googleapis.com/token",
    iat: now,
    exp: now + 3600,
  };

  const encoder = new TextEncoder();
  const toBase64Url = (data: Uint8Array) =>
    btoa(String.fromCharCode(...data)).replace(/\+/g, "-").replace(/\//g, "_").replace(/=+$/, "");

  const headerB64 = toBase64Url(encoder.encode(JSON.stringify(header)));
  const claimB64 = toBase64Url(encoder.encode(JSON.stringify(claimSet)));
  const signatureInput = `${headerB64}.${claimB64}`;

  // Import RSA private key
  const pemContents = sa.private_key
    .replace("-----BEGIN PRIVATE KEY-----", "")
    .replace("-----END PRIVATE KEY-----", "")
    .replace(/\s/g, "");
  const binaryKey = Uint8Array.from(atob(pemContents), (c) => c.charCodeAt(0));
  const cryptoKey = await crypto.subtle.importKey(
    "pkcs8", binaryKey, { name: "RSASSA-PKCS1-v1_5", hash: "SHA-256" }, false, ["sign"],
  );

  const signature = new Uint8Array(
    await crypto.subtle.sign("RSASSA-PKCS1-v1_5", cryptoKey, encoder.encode(signatureInput)),
  );
  const jwt = `${signatureInput}.${toBase64Url(signature)}`;

  const tokenResponse = await fetch("https://oauth2.googleapis.com/token", {
    method: "POST",
    headers: { "Content-Type": "application/x-www-form-urlencoded" },
    body: `grant_type=urn:ietf:params:oauth:grant-type:jwt-bearer&assertion=${jwt}`,
  });

  if (!tokenResponse.ok) {
    throw new Error(`Failed to get Google access token: ${await tokenResponse.text()}`);
  }
  const tokenData = await tokenResponse.json();
  return tokenData.access_token;
}

/**
 * The obfuscated account id the app passes to launchBillingFlow. Must match
 * BillingRepository.obfuscatedAccountId() on Android: SHA-256 hex of the
 * Supabase user id (64 chars, Play's maximum).
 */
export async function obfuscatedAccountId(userId: string): Promise<string> {
  const digest = await crypto.subtle.digest("SHA-256", new TextEncoder().encode(userId));
  return Array.from(new Uint8Array(digest)).map((b) => b.toString(16).padStart(2, "0")).join("");
}

export type PlaySubscription = {
  isActive: boolean;
  expiryTime: string | null;
  autoRenewing: boolean;
  productIds: string[];
  obfuscatedAccountId: string | null;
  linkedPurchaseToken: string | null;
};

export class PlayApiError extends Error {
  constructor(message: string, readonly status: number) {
    super(message);
  }
}

/** Fetch the authoritative subscription state for a purchase token. */
export async function fetchSubscription(purchaseToken: string): Promise<PlaySubscription> {
  const accessToken = await getGoogleAccessToken();
  const url = "https://androidpublisher.googleapis.com/androidpublisher/v3/applications/" +
    `${encodeURIComponent(PLAY_PACKAGE_NAME)}/purchases/subscriptionsv2/tokens/` +
    encodeURIComponent(purchaseToken);

  const response = await fetch(url, { headers: { Authorization: `Bearer ${accessToken}` } });
  if (!response.ok) {
    const errorText = await response.text();
    console.error("Google Play verification failed:", errorText);
    // Surface a short Google error so the app can show something actionable
    // instead of a opaque JSON blob.
    let detail = "Purchase verification failed";
    try {
      const parsed = JSON.parse(errorText);
      detail = parsed?.error?.message || parsed?.message || detail;
    } catch {
      if (errorText && errorText.length < 200) detail = errorText;
    }
    throw new PlayApiError(detail, response.status);
  }

  const data = await response.json();
  const lineItems: Array<{ productId?: string; expiryTime?: string; autoRenewingPlan?: unknown }> =
    Array.isArray(data.lineItems) ? data.lineItems : [];
  // Latest expiry across line items (normally there is exactly one).
  const expiryTime = lineItems
    .map((item) => item.expiryTime)
    .filter((t): t is string => !!t)
    .sort((a, b) => Date.parse(b) - Date.parse(a))[0] ?? null;
  const expiryMs = expiryTime ? Date.parse(expiryTime) : NaN;
  const notExpired = Number.isFinite(expiryMs) ? expiryMs > Date.now() : false;
  const state = data.subscriptionState as string | undefined;

  return {
    // Active, grace, or canceled-but-still-in-period (user canceled, access until expiry).
    isActive: state === "SUBSCRIPTION_STATE_ACTIVE" ||
      state === "SUBSCRIPTION_STATE_IN_GRACE_PERIOD" ||
      (state === "SUBSCRIPTION_STATE_CANCELED" && notExpired),
    expiryTime,
    autoRenewing: lineItems.some((item) => item.autoRenewingPlan != null),
    productIds: lineItems.map((item) => item.productId).filter((p): p is string => !!p),
    obfuscatedAccountId: data.externalAccountIdentifiers?.obfuscatedExternalAccountId ?? null,
    linkedPurchaseToken: data.linkedPurchaseToken ?? null,
  };
}
