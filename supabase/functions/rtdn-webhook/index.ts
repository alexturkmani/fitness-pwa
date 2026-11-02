import { serve } from "https://deno.land/std@0.208.0/http/server.ts";
import { createClient } from "https://esm.sh/@supabase/supabase-js@2";
import { jsonResponse, errorResponse } from "../_shared/helpers.ts";
import { fetchSubscription, PlayApiError } from "../_shared/googlePlay.ts";

const SUPABASE_URL = Deno.env.get("SUPABASE_URL")!;
const SUPABASE_SERVICE_ROLE_KEY = Deno.env.get("SUPABASE_SERVICE_ROLE_KEY")!;
// Put the same value in the Pub/Sub push endpoint: .../rtdn-webhook?secret=<value>
const RTDN_SHARED_SECRET = Deno.env.get("RTDN_SHARED_SECRET") ?? "";

/**
 * RTDN (Real-Time Developer Notifications) Webhook
 * Receives Google Cloud Pub/Sub push messages for Play Store subscription events.
 *
 * The notification is only a hint that something changed: the new state is
 * always fetched from the Play Developer API, so a forged message can at most
 * trigger a re-sync to the true state.
 */

function timingSafeEqual(a: string, b: string): boolean {
  if (a.length !== b.length) return false;
  let diff = 0;
  for (let i = 0; i < a.length; i++) diff |= a.charCodeAt(i) ^ b.charCodeAt(i);
  return diff === 0;
}

serve(async (req) => {
  if (req.method !== "POST") return errorResponse("Method not allowed", 405);

  if (RTDN_SHARED_SECRET) {
    const secret = new URL(req.url).searchParams.get("secret") ?? "";
    if (!timingSafeEqual(secret, RTDN_SHARED_SECRET)) return errorResponse("Unauthorized", 401);
  } else {
    console.warn("RTDN_SHARED_SECRET is not set; accepting unauthenticated RTDN push.");
  }

  let body;
  let decoded;
  try {
    body = await req.json();
    // Pub/Sub wraps the message in { message: { data: "base64..." } }
    const messageData = body.message?.data;
    if (!messageData) {
      return jsonResponse({ received: true, skipped: "no message data" });
    }
    decoded = JSON.parse(atob(messageData));
  } catch {
    // Malformed: retrying won't help, so acknowledge it.
    return jsonResponse({ received: true, skipped: "malformed message" });
  }

  const notification = decoded.subscriptionNotification;
  if (!notification) {
    // Could be a test notification or one-time product notification
    console.log("Non-subscription notification received:", JSON.stringify(decoded));
    return jsonResponse({ received: true });
  }

  const { notificationType, purchaseToken, subscriptionId } = notification;
  console.log(`RTDN: type=${notificationType}, subscription=${subscriptionId}`);
  if (!purchaseToken) return jsonResponse({ received: true, skipped: "no purchase token" });

  try {
    let sub;
    try {
      sub = await fetchSubscription(purchaseToken);
    } catch (e) {
      // 4xx: Google doesn't know this token (or it isn't ours) — nothing to sync.
      if (e instanceof PlayApiError && e.status >= 400 && e.status < 500) {
        console.warn(`RTDN token rejected by Play (${e.status}): ${e.message}`);
        return jsonResponse({ received: true, skipped: "unknown token" });
      }
      throw e;
    }

    const admin = createClient(SUPABASE_URL, SUPABASE_SERVICE_ROLE_KEY);

    // A resubscribe/upgrade issues a new token that links to the old one, so
    // the row may still hold the linked token.
    const tokens = [purchaseToken, sub.linkedPurchaseToken].filter((t): t is string => !!t);
    const { data: rows, error: findError } = await admin
      .from("user_subscriptions")
      .select("user_id")
      .in("purchase_token", tokens);

    if (findError) {
      console.error("Error finding subscription:", findError);
      return errorResponse("Database error", 500);
    }

    if (!rows || rows.length === 0) {
      console.log(`No subscription found for purchase token (may be new). Token prefix: ${purchaseToken.slice(0, 20)}...`);
      return jsonResponse({ received: true });
    }

    const { error: updateError } = await admin
      .from("user_subscriptions")
      .update({
        purchase_token: purchaseToken,
        status: sub.isActive ? "active" : "expired",
        expiry_time: sub.expiryTime,
        auto_renewing: sub.autoRenewing,
        updated_at: new Date().toISOString(),
      })
      .eq("user_id", rows[0].user_id);

    if (updateError) {
      console.error("Error updating subscription:", updateError);
      return errorResponse("Failed to update subscription", 500);
    }
    console.log(`Updated subscription for user ${rows[0].user_id}: ${sub.isActive ? "active" : "expired"}`);

    return jsonResponse({ received: true });
  } catch (error: any) {
    // Transient (Google API / network): a non-2xx makes Pub/Sub retry with backoff.
    console.error("RTDN webhook error:", error);
    return errorResponse(error.message || "RTDN processing failed", 500);
  }
});
