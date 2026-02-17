import { withAuth } from 'next-auth/middleware';
import { NextResponse } from 'next/server';

export default withAuth(
  function middleware(req) {
    const token = req.nextauth.token;
    const { pathname } = req.nextUrl;

    // Check trial/subscription status for protected routes
    const trialEndsAt = token?.trialEndsAt as string | null;
    const subscriptionActive = token?.subscriptionActive as boolean | undefined;
    const isTrialActive = trialEndsAt ? new Date(trialEndsAt) > new Date() : false;
    const hasAccess = isTrialActive || subscriptionActive;

    // If no access and not already on paywall, redirect to paywall
    if (!hasAccess && pathname !== '/paywall') {
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
     * Match all routes except:
     * - /api (API routes)
     * - /login, /register (auth pages)
     * - /paywall (subscription page)
     * - /_next (Next.js internals)
     * - Static files (favicon, manifest, icons, sw.js, etc.)
     */
    '/((?!api|login|register|paywall|onboarding|_next/static|_next/image|favicon\\.ico|manifest\\.json|icons|sw\\.js|robots\\.txt|sitemap\\.xml|\\.well-known).*)',
  ],
};
