'use client';
import { useState, useEffect } from 'react';
import { useSession } from 'next-auth/react';
import { useRouter } from 'next/navigation';
import { useSubscription } from '@/hooks/useSubscription';
import {
  Dumbbell, Crown, CheckCircle, Sparkles, ArrowRight,
  Zap, Apple, BarChart3, ScanLine, Shield
} from 'lucide-react';

export default function PaywallPage() {
  const router = useRouter();
  const { data: session } = useSession();
  const { hasAccess, trialDaysLeft, isTrialActive } = useSubscription();
  const [purchasing, setPurchasing] = useState(false);
  const [error, setError] = useState('');

  const isNewUser = typeof window !== 'undefined' && !localStorage.getItem('onboarding_complete');

  // Redirect if user has access and is not a new user viewing paywall for first time
  useEffect(() => {
    if (hasAccess && !isNewUser) {
      router.push('/');
    }
  }, [hasAccess, isNewUser, router]);

  const handleSubscribe = async () => {
    setPurchasing(true);
    setError('');

    try {
      const res = await fetch('/api/stripe/checkout', { method: 'POST' });
      const data = await res.json();

      if (!res.ok) {
        setError(data.error || 'Failed to start checkout');
        setPurchasing(false);
        return;
      }

      // Redirect to Stripe Checkout
      window.location.href = data.url;
    } catch (err) {
      setError('Something went wrong. Please try again.');
      setPurchasing(false);
    }
  };

  const features = [
    { icon: Dumbbell, title: 'AI Workout Plans', desc: 'Personalized plans that adapt to your progress' },
    { icon: Apple, title: 'AI Meal Plans', desc: 'Custom nutrition with macro tracking' },
    { icon: ScanLine, title: 'Barcode Scanner', desc: 'Instantly log food with your camera' },
    { icon: BarChart3, title: 'Progress Tracking', desc: 'Charts and insights on your journey' },
    { icon: Zap, title: 'AI Assessments', desc: 'Smart feedback on your performance' },
    { icon: Shield, title: 'Unlimited Access', desc: 'All features, no limits' },
  ];

  return (
    <div className="min-h-screen flex flex-col items-center px-6 py-10 -mt-4">
      {/* Header */}
      <div className="flex items-center gap-2 mb-2">
        <Crown className="text-yellow-500" size={28} />
        <h1 className="text-2xl font-bold text-dark-100">FitMate Premium</h1>
      </div>
      <p className="text-dark-400 text-center mb-8">
        {isNewUser
          ? 'Start with a 7-day free trial, then $4.99/month.'
          : 'Your free trial has ended. Subscribe to keep your AI fitness coach.'}
      </p>

      {/* Features */}
      <div className="w-full max-w-sm space-y-3 mb-8">
        {features.map((feature) => (
          <div key={feature.title} className="flex items-center gap-3 p-3 rounded-xl bg-white border border-dark-700/50">
            <div className="w-10 h-10 rounded-xl bg-primary-500/10 flex items-center justify-center flex-shrink-0">
              <feature.icon className="text-primary-500" size={20} />
            </div>
            <div>
              <p className="font-medium text-dark-100 text-sm">{feature.title}</p>
              <p className="text-xs text-dark-500">{feature.desc}</p>
            </div>
            <CheckCircle className="text-primary-500 flex-shrink-0 ml-auto" size={18} />
          </div>
        ))}
      </div>

      {/* Pricing */}
      <div className="w-full max-w-sm">
        <div className="p-6 rounded-2xl bg-gradient-to-br from-primary-500/5 to-primary-600/10 border-2 border-primary-500/30 text-center mb-4">
          <div className="flex items-baseline justify-center gap-1 mb-1">
            <span className="text-4xl font-bold text-dark-100">$4.99</span>
            <span className="text-dark-400">/month</span>
          </div>
          <p className="text-sm text-dark-400">Cancel anytime from your account</p>
        </div>

        {error && (
          <p className="text-sm text-red-500 text-center mb-3">{error}</p>
        )}

        <button
          onClick={handleSubscribe}
          disabled={purchasing}
          className="w-full flex items-center justify-center gap-2 p-4 rounded-xl bg-gradient-to-r from-primary-500 to-primary-600 text-white font-bold text-lg hover:from-primary-600 hover:to-primary-700 transition-all disabled:opacity-50 shadow-lg shadow-primary-500/20"
        >
          {purchasing ? (
            <div className="w-6 h-6 border-2 border-white/30 border-t-white rounded-full spinner" />
          ) : (
            <>
              <Sparkles size={20} />
              Subscribe Now
              <ArrowRight size={20} />
            </>
          )}
        </button>

        <p className="text-xs text-dark-500 text-center mt-4">
          Secured by Stripe. Your payment info is never stored on our servers.
        </p>

        {isNewUser && (
          <button
            onClick={() => router.push('/onboarding')}
            className="w-full mt-3 p-3.5 rounded-xl border border-dark-700 text-dark-300 font-medium hover:bg-dark-950 transition-all text-center"
          >
            Start 7-Day Free Trial Instead
          </button>
        )}
      </div>
    </div>
  );
}
