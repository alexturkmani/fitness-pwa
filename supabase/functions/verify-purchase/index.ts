import { serve } from "https://deno.land/std@0.208.0/http/server.ts";
import { createClient } from "https://esm.sh/@supabase/supabase-js@2";
import { fetchSubscription, obfuscatedAccountId, PlayApiError } from "../_shared/googlePlay.ts";

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

const OTHER_ACCOUNT_MESSAGE =
  "This Google Play subscription is linked to a different Nexal account. " +
  "Sign in with that account, or contact support to move it.";

serve(async (req) => {
  if (req.method === "OPTIONS") return new Response("ok", { headers: corsHeaders() });

  try {
    // Authenticate the user via Supabase JWT
    const authHeader = req.headers.get("authorization");
    if (!authHeader) return errorResponse("Unauthorized", 401);

    const { data: { user }, error: authError } = await createClient(
      SUPABASE_URL,
      Deno.env.get("SUPABASE_ANON_KEY")!,
      { global: { headers: { Authorization: authHeader } } },
    ).auth.getUser();

    if (authError || !user) return errorResponse("Unauthorized", 401);

    // packageName from older app builds is ignored: the package is fixed server-side.
    const { purchaseToken, productId } = await req.json();
    if (!purchaseToken || !productId) return errorResponse("Missing purchaseToken or productId", 400);

    // Verify with Google Play Developer API
    let sub;
    try {
      sub = await fetchSubscription(purchaseToken);
    } catch (e) {
      if (e instanceof PlayApiError) return errorResponse(e.message, 400);
      throw e;
    }
    if (!sub.productIds.includes(productId)) {
      return errorResponse("Purchase does not match this subscription", 400);
    }

    // Purchases made by builds that set obfuscatedAccountId are bound to one
    // account by Google itself.
    if (sub.obfuscatedAccountId && sub.obfuscatedAccountId !== await obfuscatedAccountId(user.id)) {
      return errorResponse(OTHER_ACCOUNT_MESSAGE, 409);
    }

    // Older purchases carry no account id, so the first account to verify a
    // token owns it. Check the token and the one it replaced (resubscribe/upgrade).
    const admin = createClient(SUPABASE_URL, SUPABASE_SERVICE_ROLE_KEY);
    const tokens = [purchaseToken, sub.linkedPurchaseToken].filter((t): t is string => !!t);
    const { data: owners, error: ownerError } = await admin
      .from("user_subscriptions")
      .select("user_id")
      .in("purchase_token", tokens)
      .neq("user_id", user.id)
      .limit(1);
    if (ownerError) {
      console.error("Owner lookup error:", ownerError);
      return errorResponse("Failed to verify subscription", 500);
    }
    if (owners && owners.length > 0) {
      console.warn(`Purchase token already owned by another user; rejected for ${user.id}`);
      return errorResponse(OTHER_ACCOUNT_MESSAGE, 409);
    }

    // Upsert subscription in database
    const { error: upsertError } = await admin.from("user_subscriptions").upsert({
      user_id: user.id,
      product_id: productId,
      purchase_token: purchaseToken,
      status: sub.isActive ? "active" : "expired",
      expiry_time: sub.expiryTime,
      auto_renewing: sub.autoRenewing,
      updated_at: new Date().toISOString(),
    }, { onConflict: "user_id" });

    if (upsertError) {
      // 23505: user_subscriptions_purchase_token_key — another account won a concurrent verify.
      if (upsertError.code === "23505") return errorResponse(OTHER_ACCOUNT_MESSAGE, 409);
      console.error("Upsert error:", upsertError);
      return errorResponse("Failed to update subscription", 500);
    }

    return jsonResponse({
      subscriptionActive: sub.isActive,
      expiryTime: sub.expiryTime,
      autoRenewing: sub.autoRenewing,
    });
  } catch (error: any) {
    console.error("Verify purchase error:", error);
    return errorResponse(error.message || "Verification failed");
  }
});
