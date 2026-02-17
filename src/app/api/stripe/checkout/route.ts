import { NextRequest, NextResponse } from 'next/server';
import { getServerSession } from 'next-auth';
import { getAuthOptions } from '@/lib/auth';
import { getStripe } from '@/lib/stripe';
import prisma from '@/lib/prisma';

export async function POST(request: NextRequest) {
  try {
    const session = await getServerSession(getAuthOptions());
    if (!session?.user?.email) {
      return NextResponse.json({ error: 'Not authenticated' }, { status: 401 });
    }

    const user = await prisma.user.findUnique({
      where: { email: session.user.email },
    });

    if (!user) {
      return NextResponse.json({ error: 'User not found' }, { status: 404 });
    }

    const stripe = getStripe();

    // Create or retrieve Stripe customer
    let customerId = user.stripeCustomerId;
    if (!customerId) {
      const customer = await stripe.customers.create({
        email: user.email,
        name: user.name || undefined,
        metadata: { userId: user.id },
      });
      customerId = customer.id;
      await prisma.user.update({
        where: { id: user.id },
        data: { stripeCustomerId: customerId },
      });
    }

    // Create checkout session
    const checkoutSession = await stripe.checkout.sessions.create({
      customer: customerId,
      mode: 'subscription',
      payment_method_types: ['card'],
      line_items: [
        {
          price: process.env.STRIPE_PRICE_ID!,
          quantity: 1,
        },
      ],
      success_url: `${process.env.NEXTAUTH_URL || 'http://localhost:3000'}/dashboard?subscribed=true`,
      cancel_url: `${process.env.NEXTAUTH_URL || 'http://localhost:3000'}/paywall`,
      subscription_data: {
        metadata: { userId: user.id },
      },
      metadata: { userId: user.id },
    });

    return NextResponse.json({ url: checkoutSession.url });
  } catch (error: any) {
    console.error('Checkout error:', error);
    return NextResponse.json({ error: 'Failed to create checkout session' }, { status: 500 });
  }
}
