import { serve } from "https://deno.land/std@0.208.0/http/server.ts";
import { createClient } from "https://esm.sh/@supabase/supabase-js@2";
import { jsonResponse, errorResponse } from "../_shared/helpers.ts";

const SUPABASE_URL = Deno.env.get("SUPABASE_URL")!;
const SUPABASE_SERVICE_ROLE_KEY = Deno.env.get("SUPABASE_SERVICE_ROLE_KEY")!;

/**
 * RTDN (Real-Time Developer Notifications) Webhook
 * Receives Google Cloud Pub/Sub push messages for Play Store subscription events.
 *
 * Subscription notification types:
 * 1 = RECOVERED, 2 = RENEWED, 3 = CANCELED, 4 = PURCHASED,
 * 5 = ON_HOLD, 6 = IN_GRACE_PERIOD, 7 = RESTARTED,
 * 12 = REVOKED, 13 = EXPIRED
 */

serve(async (req) => {
  if (req.method !== "POST") return errorResponse("Method not allowed", 405);

  try {
    const body = await req.json();

    // Pub/Sub wraps the message in { message: { data: "base64..." } }
    const messageData = body.message?.data;
    if (!messageData) {
      return jsonResponse({ received: true, skipped: "no message data" });
    }

    const decoded = JSON.parse(atob(messageData));
    const notification = decoded.subscriptionNotification;

    if (!notification) {
      // Could be a test notification or one-time product notification
      console.log("Non-subscription notification received:", JSON.stringify(decoded));
      return jsonResponse({ received: true });
    }

    const { notificationType, purchaseToken, subscriptionId } = notification;
    console.log(`RTDN: type=${notificationType}, subscription=${subscriptionId}`);

    // Active subscription states
    const activeTypes = [1, 2, 4, 6, 7]; // RECOVERED, RENEWED, PURCHASED, IN_GRACE_PERIOD, RESTARTED
    // Inactive subscription states
    const inactiveTypes = [3, 5, 12, 13]; // CANCELED, ON_HOLD, REVOKED, EXPIRED

    let newStatus: string | null = null;
    if (activeTypes.includes(notificationType)) {
      newStatus = "active";
    } else if (inactiveTypes.includes(notificationType)) {
      newStatus = "expired";
    }

    if (newStatus && purchaseToken) {
      const admin = createClient(SUPABASE_URL, SUPABASE_SERVICE_ROLE_KEY);

      // Find the subscription by purchase token
      const { data: sub, error: findError } = await admin
        .from("user_subscriptions")
        .select("user_id")
        .eq("purchase_token", purchaseToken)
        .maybeSingle();

      if (findError) {
        console.error("Error finding subscription:", findError);
        return errorResponse("Database error", 500);
      }

      if (sub) {
        const { error: updateError } = await admin
          .from("user_subscriptions")
          .update({
            status: newStatus,
            updated_at: new Date().toISOString(),
          })
          .eq("purchase_token", purchaseToken);

        if (updateError) {
          console.error("Error updating subscription:", updateError);
          return errorResponse("Failed to update subscription", 500);
        }
        console.log(`Updated subscription for user ${sub.user_id}: ${newStatus}`);
      } else {
        console.log(`No subscription found for purchase token (may be new). Token prefix: ${purchaseToken.slice(0, 20)}...`);
      }
    }

    // Always return 200 to acknowledge the Pub/Sub message
    return jsonResponse({ received: true });
  } catch (error: any) {
    console.error("RTDN webhook error:", error);
    // Return 200 even on error to prevent Pub/Sub retry loops
    return jsonResponse({ received: true, error: error.message });
  }
});
