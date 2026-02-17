import { withAuth } from 'next-auth/middleware';
import { NextResponse } from 'next/server';

export default withAuth(
  function middleware(req) {
    const token = req.nextauth.token;
    const { pathname } = req.nextUrl;

    // Check trial/subscription status for protected routes
    const trialEndsAt = token?.trialEndsAt as string | null | undefined;
    const subscriptionActive = token?.subscriptionActive as boolean | undefined;
    const isFreeAccount = token?.isFreeAccount as boolean | undefined;
    const isTrialActive = trialEndsAt ? new Date(trialEndsAt) > new Date() : false;
    const hasAccess = isFreeAccount || isTrialActive || subscriptionActive;

    // If subscription data hasn't loaded yet (both undefined), allow through
    // This prevents lockout when DB is temporarily unavailable
    const hasSubscriptionData = trialEndsAt !== undefined || subscriptionActive !== undefined;

    // Only redirect to paywall if we KNOW they don't have access
    if (hasSubscriptionData && !hasAccess && pathname !== '/paywall') {
      return NextResponse.redirect(new URL('/paywall', req.url));
    }

    return NextResponse.next();
  },
  {
    callbacks: {
      authorized: ({ token }) => !!token,
    },
    pages: {
      signIn: '/login',
    },
  }
);

export const config = {
  matcher: [
    /*
     * Protect app routes only — landing page (/) is public.
     * Match /dashboard and all app routes except public ones.
     */
    '/dashboard/:path*',
    '/workouts/:path*',
    '/meals/:path*',
    '/nutrition/:path*',
    '/scanner/:path*',
    '/progress/:path*',
    '/profile/:path*',
  ],
};
