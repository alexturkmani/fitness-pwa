import { NextRequest, NextResponse } from 'next/server';
import prisma from '@/lib/prisma';

/**
 * RevenueCat Webhook Handler
 * 
 * SETUP:
 * 1. In RevenueCat Dashboard → Project Settings → Webhooks
 * 2. Add webhook URL: https://your-domain.vercel.app/api/webhooks/revenuecat
 * 3. Set Authorization header to match REVENUECAT_WEBHOOK_SECRET in .env.local
 * 
 * This webhook updates the user's subscription status in the database
 * when RevenueCat detects a purchase, renewal, cancellation, or expiration.
 */

export async function POST(request: NextRequest) {
  // Verify webhook authenticity
  const authHeader = request.headers.get('authorization');
  const webhookSecret = process.env.REVENUECAT_WEBHOOK_SECRET;

  if (webhookSecret && authHeader !== `Bearer ${webhookSecret}`) {
    return NextResponse.json({ error: 'Unauthorized' }, { status: 401 });
  }

  try {
    const body = await request.json();
    const event = body.event;

    if (!event) {
      return NextResponse.json({ error: 'No event data' }, { status: 400 });
    }

    const appUserId = event.app_user_id;
    if (!appUserId) {
      return NextResponse.json({ error: 'No user ID' }, { status: 400 });
    }

    // Events that grant access
    const activeEvents = [
      'INITIAL_PURCHASE',
      'RENEWAL',
      'PRODUCT_CHANGE',
      'UNCANCELLATION',
    ];

    // Events that revoke access
    const inactiveEvents = [
      'CANCELLATION',
      'EXPIRATION',
      'BILLING_ISSUE',
      'SUBSCRIPTION_PAUSED',
    ];

    let subscriptionActive: boolean | undefined;

    if (activeEvents.includes(event.type)) {
      subscriptionActive = true;
    } else if (inactiveEvents.includes(event.type)) {
      subscriptionActive = false;
    }

    if (subscriptionActive !== undefined) {
      await prisma.user.update({
        where: { id: appUserId },
        data: {
          subscriptionActive,
          rcCustomerId: event.subscriber?.original_app_user_id || undefined,
        },
      });
    }

    return NextResponse.json({ received: true });
  } catch (error: any) {
    console.error('Webhook error:', error);
    return NextResponse.json({ error: 'Webhook processing failed' }, { status: 500 });
  }
}
