import { serve } from "https://deno.land/std@0.208.0/http/server.ts";
import { createClient } from "https://esm.sh/@supabase/supabase-js@2";

function corsHeaders() {
  return {
    "Access-Control-Allow-Origin": "*",
    "Access-Control-Allow-Headers": "authorization, x-client-info, apikey, content-type",
  };
}

function jsonResponse(data: unknown, status = 200) {
  return new Response(JSON.stringify(data), {
    status,
    headers: { ...corsHeaders(), "Content-Type": "application/json" },
  });
}

function errorResponse(message: string, status = 500) {
  return jsonResponse({ error: message }, status);
}

const SUPABASE_URL = Deno.env.get("SUPABASE_URL")!;
const SUPABASE_SERVICE_ROLE_KEY = Deno.env.get("SUPABASE_SERVICE_ROLE_KEY")!;
const GOOGLE_SERVICE_ACCOUNT_JSON = Deno.env.get("GOOGLE_SERVICE_ACCOUNT_JSON")!;

// Get Google OAuth2 access token from service account
async function getGoogleAccessToken(): Promise<string> {
  // The `!` above is compile-time only. Without this check a missing secret
  // surfaces as `"undefined" is not valid JSON`, which says nothing about the
  // actual cause and sends you looking in the app instead of the config.
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

serve(async (req) => {
  if (req.method === "OPTIONS") return new Response("ok", { headers: corsHeaders() });

  try {
    // Authenticate the user via Supabase JWT
    const authHeader = req.headers.get("authorization");
    if (!authHeader) return errorResponse("Unauthorized", 401);

    const supabaseUser = createClient(SUPABASE_URL, SUPABASE_SERVICE_ROLE_KEY);
    const userClient = createClient(SUPABASE_URL, authHeader.replace("Bearer ", ""), {
      global: { headers: { Authorization: authHeader } },
    });
    const { data: { user }, error: authError } = await createClient(
      SUPABASE_URL,
      Deno.env.get("SUPABASE_ANON_KEY")!,
      { global: { headers: { Authorization: authHeader } } },
    ).auth.getUser();

    if (authError || !user) return errorResponse("Unauthorized", 401);

    const { purchaseToken, productId, packageName } = await req.json();
    if (!purchaseToken || !productId) return errorResponse("Missing purchaseToken or productId", 400);

    // Verify with Google Play Developer API
    const accessToken = await getGoogleAccessToken();
    const pkg = packageName || "com.nexal.app";
    const verifyUrl = `https://androidpublisher.googleapis.com/androidpublisher/v3/applications/${pkg}/purchases/subscriptionsv2/tokens/${purchaseToken}`;

    const verifyResponse = await fetch(verifyUrl, {
      headers: { Authorization: `Bearer ${accessToken}` },
    });

    if (!verifyResponse.ok) {
      const errorText = await verifyResponse.text();
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
      return errorResponse(detail, 400);
    }

    const purchaseData = await verifyResponse.json();
    const lineItems = Array.isArray(purchaseData.lineItems) ? purchaseData.lineItems : [];
    if (!lineItems.some((item: { productId?: string }) => item.productId === productId)) {
      return errorResponse("Purchase does not match this subscription", 400);
    }
    const subscriptionState = purchaseData.subscriptionState as string | undefined;
    const purchasedItem = lineItems.find(
      (item: { productId?: string }) => item.productId === productId,
    );
    const expiryTime = purchasedItem?.expiryTime as string | undefined;
    const autoRenewing = purchasedItem?.autoRenewingPlan != null;
    const expiryMs = expiryTime ? Date.parse(expiryTime) : NaN;
    const notExpired = Number.isFinite(expiryMs) ? expiryMs > Date.now() : false;

    // Active, grace, or canceled-but-still-in-period (user canceled, access until expiry).
    const isActive =
      subscriptionState === "SUBSCRIPTION_STATE_ACTIVE" ||
      subscriptionState === "SUBSCRIPTION_STATE_IN_GRACE_PERIOD" ||
      (subscriptionState === "SUBSCRIPTION_STATE_CANCELED" && notExpired);

    // Upsert subscription in database
    const admin = createClient(SUPABASE_URL, SUPABASE_SERVICE_ROLE_KEY);
    const { error: upsertError } = await admin.from("user_subscriptions").upsert({
      user_id: user.id,
      product_id: productId,
      purchase_token: purchaseToken,
      status: isActive ? "active" : "expired",
      expiry_time: expiryTime || null,
      auto_renewing: autoRenewing,
      updated_at: new Date().toISOString(),
    }, { onConflict: "user_id" });

    if (upsertError) {
      console.error("Upsert error:", upsertError);
      return errorResponse("Failed to update subscription", 500);
    }

    return jsonResponse({
      subscriptionActive: isActive,
      expiryTime,
      autoRenewing,
    });
  } catch (error: any) {
    console.error("Verify purchase error:", error);
    return errorResponse(error.message || "Verification failed");
  }
});
