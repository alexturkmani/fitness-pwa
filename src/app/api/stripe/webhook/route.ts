import { NextRequest, NextResponse } from 'next/server';
import { getStripe } from '@/lib/stripe';
import prisma from '@/lib/prisma';
import Stripe from 'stripe';

export async function POST(request: NextRequest) {
  const body = await request.text();
  const signature = request.headers.get('stripe-signature');

  if (!signature || !process.env.STRIPE_WEBHOOK_SECRET) {
    return NextResponse.json({ error: 'Missing signature or webhook secret' }, { status: 400 });
  }

  let event: Stripe.Event;

  try {
    const stripe = getStripe();
    event = stripe.webhooks.constructEvent(body, signature, process.env.STRIPE_WEBHOOK_SECRET);
  } catch (err: any) {
    console.error('Webhook signature verification failed:', err.message);
    return NextResponse.json({ error: 'Invalid signature' }, { status: 400 });
  }

  try {
    switch (event.type) {
      case 'checkout.session.completed': {
        const session = event.data.object as Stripe.Checkout.Session;
        const userId = session.metadata?.userId;
        const subscriptionId = session.subscription as string;

        if (userId && subscriptionId) {
          await prisma.user.update({
            where: { id: userId },
            data: {
              subscriptionActive: true,
              stripeSubscriptionId: subscriptionId,
              stripeCustomerId: session.customer as string,
            },
          });
        }
        break;
      }

      case 'invoice.paid': {
        // Recurring payment succeeded
        const invoice = event.data.object as any;
        const subscriptionId = invoice.subscription as string;
        if (subscriptionId) {
          const user = await prisma.user.findFirst({
            where: { stripeSubscriptionId: subscriptionId },
          });
          if (user) {
            await prisma.user.update({
              where: { id: user.id },
              data: { subscriptionActive: true },
            });
          }
        }
        break;
      }

      case 'invoice.payment_failed': {
        // Payment failed
        const invoice = event.data.object as any;
        const subscriptionId = invoice.subscription as string;
        if (subscriptionId) {
          const user = await prisma.user.findFirst({
            where: { stripeSubscriptionId: subscriptionId },
          });
          if (user) {
            await prisma.user.update({
              where: { id: user.id },
              data: { subscriptionActive: false },
            });
          }
        }
        break;
      }

      case 'customer.subscription.deleted': {
        // Subscription cancelled/expired
        const subscription = event.data.object as Stripe.Subscription;
        const user = await prisma.user.findFirst({
          where: { stripeSubscriptionId: subscription.id },
        });
        if (user) {
          await prisma.user.update({
            where: { id: user.id },
            data: {
              subscriptionActive: false,
              stripeSubscriptionId: null,
            },
          });
        }
        break;
      }

      case 'customer.subscription.updated': {
        // Subscription changed (upgrade, downgrade, cancel at period end)
        const subscription = event.data.object as Stripe.Subscription;
        const user = await prisma.user.findFirst({
          where: { stripeSubscriptionId: subscription.id },
        });
        if (user) {
          const isActive = subscription.status === 'active' || subscription.status === 'trialing';
          await prisma.user.update({
            where: { id: user.id },
            data: { subscriptionActive: isActive },
          });
        }
        break;
      }
    }

    return NextResponse.json({ received: true });
  } catch (error) {
    console.error('Webhook processing error:', error);
    return NextResponse.json({ error: 'Webhook processing failed' }, { status: 500 });
  }
}
