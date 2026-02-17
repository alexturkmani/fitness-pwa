'use client';
import { useSession } from 'next-auth/react';
import { useState, useEffect, useCallback } from 'react';

interface SubscriptionState {
  isTrialActive: boolean;
  isSubscribed: boolean;
  hasAccess: boolean;
  trialDaysLeft: number;
  loading: boolean;
}

export function useSubscription(): SubscriptionState {
  const { data: session, status } = useSession();
  const [state, setState] = useState<SubscriptionState>({
    isTrialActive: false,
    isSubscribed: false,
    hasAccess: false,
    trialDaysLeft: 0,
    loading: true,
  });

  useEffect(() => {
    if (status === 'loading') return;

    if (!session?.user) {
      setState({ isTrialActive: false, isSubscribed: false, hasAccess: false, trialDaysLeft: 0, loading: false });
      return;
    }

    const user = session.user as any;
    const trialEndsAt = user.trialEndsAt ? new Date(user.trialEndsAt) : null;
    const now = new Date();

    const isTrialActive = trialEndsAt ? trialEndsAt > now : false;
    const trialDaysLeft = trialEndsAt ? Math.max(0, Math.ceil((trialEndsAt.getTime() - now.getTime()) / (1000 * 60 * 60 * 24))) : 0;
    const isSubscribed = user.subscriptionActive || false;
    const hasAccess = isTrialActive || isSubscribed;

    setState({ isTrialActive, isSubscribed, hasAccess, trialDaysLeft, loading: false });
  }, [session, status]);

  return state;
}
