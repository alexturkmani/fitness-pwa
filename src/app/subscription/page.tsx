'use client';
import { useState } from 'react';
import { useSession, signOut } from 'next-auth/react';
import { useRouter } from 'next/navigation';
import { useSubscription } from '@/hooks/useSubscription';
import Card from '@/components/ui/Card';
import Button from '@/components/ui/Button';
import {
  ArrowLeft, Crown, CreditCard, Calendar, Shield,
  AlertTriangle, LogOut, CheckCircle, Clock, Sparkles
} from 'lucide-react';

export default function SubscriptionPage() {
  const router = useRouter();
  const { data: session } = useSession();
  const { isTrialActive, isSubscribed, trialDaysLeft, hasAccess } = useSubscription();
  const [cancelling, setCancelling] = useState(false);
  const [showCancelConfirm, setShowCancelConfirm] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  const user = session?.user as any;
  const email = user?.email || '';
  const trialEndsAt = user?.trialEndsAt ? new Date(user.trialEndsAt) : null;

  const [loadingPortal, setLoadingPortal] = useState(false);

  const handleManageBilling = async () => {
    setLoadingPortal(true);
    setError('');
    try {
      const res = await fetch('/api/stripe/portal', { method: 'POST' });
      const data = await res.json();
      if (data.url) {
        window.location.href = data.url;
      } else {
        setError(data.error || 'Failed to open billing portal.');
      }
    } catch (e) {
      setError('Something went wrong. Please try again.');
    } finally {
      setLoadingPortal(false);
    }
  };

  const handleCancel = async () => {
    setCancelling(true);
    setError('');
    try {
      // Open Stripe portal where user can cancel directly
      const res = await fetch('/api/stripe/portal', { method: 'POST' });
      const data = await res.json();
      if (data.url) {
        window.location.href = data.url;
      } else {
        setError('Failed to open billing portal.');
      }
    } catch (e) {
      setError('Something went wrong. Please try again.');
    } finally {
      setCancelling(false);
    }
  };

  return (
    <div className="py-6 space-y-6">
      {/* Header */}
      <div className="flex items-center gap-3">
        <button onClick={() => router.back()} className="p-2 text-dark-400 hover:text-dark-200 transition-colors">
          <ArrowLeft size={20} />
        </button>
        <h1 className="text-2xl font-bold text-dark-100">Subscription</h1>
      </div>

      {/* Status Card */}
      <Card className={`border ${hasAccess ? 'border-primary-500/30 bg-primary-500/5' : 'border-red-500/30 bg-red-500/5'}`}>
        <div className="flex items-center gap-3 mb-3">
          {hasAccess ? (
            <div className="w-10 h-10 rounded-xl bg-primary-500/10 flex items-center justify-center">
              <CheckCircle className="text-primary-500" size={22} />
            </div>
          ) : (
            <div className="w-10 h-10 rounded-xl bg-red-500/10 flex items-center justify-center">
              <AlertTriangle className="text-red-500" size={22} />
            </div>
          )}
          <div>
            <p className="font-semibold text-dark-100">
              {isSubscribed ? 'Premium Active' : isTrialActive ? 'Free Trial' : 'No Active Plan'}
            </p>
            <p className="text-sm text-dark-400">
              {isSubscribed
                ? '$4.99/month — Active subscription'
                : isTrialActive
                ? `${trialDaysLeft} day${trialDaysLeft !== 1 ? 's' : ''} remaining`
                : 'Your trial has ended'}
            </p>
          </div>
        </div>
      </Card>

      {/* Account Details */}
      <Card>
        <h2 className="font-semibold text-dark-100 mb-4 flex items-center gap-2">
          <CreditCard size={18} className="text-dark-400" />
          Account Details
        </h2>
        <div className="space-y-3">
          <div className="flex justify-between items-center py-2 border-b border-dark-700/50">
            <span className="text-sm text-dark-400">Email</span>
            <span className="text-sm font-medium text-dark-200">{email}</span>
          </div>
          <div className="flex justify-between items-center py-2 border-b border-dark-700/50">
            <span className="text-sm text-dark-400">Plan</span>
            <span className="text-sm font-medium text-dark-200">
              {isSubscribed ? 'Premium ($4.99/mo)' : isTrialActive ? 'Free Trial' : 'None'}
            </span>
          </div>
          {trialEndsAt && (
            <div className="flex justify-between items-center py-2 border-b border-dark-700/50">
              <span className="text-sm text-dark-400">
                {isTrialActive ? 'Trial ends' : 'Trial ended'}
              </span>
              <span className="text-sm font-medium text-dark-200">
                {trialEndsAt.toLocaleDateString('en-US', { month: 'short', day: 'numeric', year: 'numeric' })}
              </span>
            </div>
          )}
          <div className="flex justify-between items-center py-2">
            <span className="text-sm text-dark-400">Status</span>
            <span className={`text-sm font-medium px-2.5 py-0.5 rounded-full ${
              hasAccess ? 'bg-primary-500/10 text-primary-500' : 'bg-red-500/10 text-red-500'
            }`}>
              {hasAccess ? 'Active' : 'Inactive'}
            </span>
          </div>
        </div>
      </Card>

      {/* Actions */}
      <div className="space-y-3">
        {!isSubscribed && (
          <Button className="w-full" onClick={() => router.push('/paywall')}>
            <Crown size={18} />
            {isTrialActive ? 'Upgrade to Premium' : 'Subscribe — $4.99/month'}
          </Button>
        )}

        {isSubscribed && (
          <button
            onClick={handleManageBilling}
            disabled={loadingPortal}
            className="w-full flex items-center justify-center gap-2 p-3.5 rounded-xl bg-primary-500 text-white font-medium hover:bg-primary-600 transition-all disabled:opacity-50"
          >
            <CreditCard size={18} />
            {loadingPortal ? 'Opening...' : 'Manage Billing'}
          </button>
        )}

        {isSubscribed && !showCancelConfirm && (
          <button
            onClick={() => setShowCancelConfirm(true)}
            className="w-full p-3.5 rounded-xl border border-dark-700 text-dark-400 text-sm font-medium hover:border-red-500/50 hover:text-red-400 transition-all"
          >
            Cancel Subscription
          </button>
        )}

        {showCancelConfirm && (
          <Card className="border-red-500/30 bg-red-500/5">
            <div className="flex items-start gap-3 mb-4">
              <AlertTriangle className="text-red-500 flex-shrink-0 mt-0.5" size={20} />
              <div>
                <p className="font-medium text-dark-100">Cancel subscription?</p>
                <p className="text-sm text-dark-400 mt-1">
                  You'll lose access to AI workout plans, meal plans, barcode scanner, and all premium features at the end of your billing period.
                </p>
              </div>
            </div>
            <div className="flex gap-2">
              <button
                onClick={() => setShowCancelConfirm(false)}
                className="flex-1 p-2.5 rounded-xl border border-dark-700 text-dark-300 text-sm font-medium hover:bg-dark-950 transition-all"
              >
                Keep Plan
              </button>
              <button
                onClick={handleCancel}
                disabled={cancelling}
                className="flex-1 p-2.5 rounded-xl bg-red-500 text-white text-sm font-medium hover:bg-red-600 transition-all disabled:opacity-50"
              >
                {cancelling ? 'Cancelling...' : 'Yes, Cancel'}
              </button>
            </div>
          </Card>
        )}

        {success && (
          <Card className="border-primary-500/30 bg-primary-500/5">
            <div className="flex items-center gap-2">
              <CheckCircle className="text-primary-500 flex-shrink-0" size={18} />
              <p className="text-sm text-primary-600">{success}</p>
            </div>
          </Card>
        )}

        {error && (
          <Card className="border-red-500/30 bg-red-500/5">
            <p className="text-sm text-red-500">{error}</p>
          </Card>
        )}
      </div>

      {/* Sign Out */}
      <button
        onClick={() => signOut({ callbackUrl: '/login' })}
        className="w-full flex items-center justify-center gap-2 p-3.5 rounded-xl border border-dark-700 text-red-400 font-medium hover:bg-red-500/5 hover:border-red-500/30 transition-all"
      >
        <LogOut size={18} />
        Sign Out
      </button>

      {/* Footer info */}
      <p className="text-xs text-dark-500 text-center">
        Need help? Contact us at support@Nexal.app
      </p>
    </div>
  );
}
