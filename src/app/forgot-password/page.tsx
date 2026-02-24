'use client';
import { useState } from 'react';
import Link from 'next/link';
import { Dumbbell, Mail, ArrowRight, ArrowLeft, CheckCircle } from 'lucide-react';

export default function ForgotPasswordPage() {
  const [email, setEmail] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [sent, setSent] = useState(false);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    setError('');

    try {
      const res = await fetch('/api/auth/forgot-password', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ email }),
      });

      const data = await res.json();

      if (!res.ok) {
        setError(data.error || 'Something went wrong');
      } else {
        setSent(true);
      }
    } catch {
      setError('Something went wrong. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  if (sent) {
    return (
      <div className="min-h-screen flex flex-col items-center justify-center px-6 -mt-20">
        <div className="w-full max-w-sm text-center space-y-4">
          <div className="w-16 h-16 rounded-full bg-primary-500/20 flex items-center justify-center mx-auto">
            <CheckCircle className="text-primary-500" size={32} />
          </div>
          <h2 className="text-2xl font-bold text-dark-100">Check your email</h2>
          <p className="text-dark-400">
            If an account exists with <span className="font-medium text-dark-200">{email}</span>, we&apos;ve sent a password reset link. Check your inbox and spam folder.
          </p>
          <Link href="/login" className="inline-flex items-center gap-2 text-primary-400 font-medium hover:text-primary-300">
            <ArrowLeft size={16} />
            Back to Login
          </Link>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen flex flex-col items-center justify-center px-6 -mt-20">
      <div className="flex items-center gap-3 mb-8">
        <div className="w-12 h-12 rounded-2xl bg-primary-500 flex items-center justify-center">
          <Dumbbell className="text-white" size={24} />
        </div>
        <h1 className="text-3xl font-bold">
          <span className="gradient-text">Nexal</span>
        </h1>
      </div>

      <div className="w-full max-w-sm space-y-6">
        <div className="text-center">
          <h2 className="text-2xl font-bold text-dark-100">Forgot Password</h2>
          <p className="text-dark-400 mt-1">Enter your email to receive a reset link</p>
        </div>

        <form onSubmit={handleSubmit} className="space-y-4">
          <div className="relative">
            <Mail className="absolute left-4 top-1/2 -translate-y-1/2 text-dark-500" size={18} />
            <input
              type="email"
              placeholder="Email address"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              className="input-field pl-12"
              required
            />
          </div>

          {error && (
            <p className="text-sm text-red-500 text-center">{error}</p>
          )}

          <button
            type="submit"
            disabled={loading || !email}
            className="w-full flex items-center justify-center gap-2 p-3.5 rounded-xl bg-gradient-to-r from-primary-500 to-primary-600 text-white font-semibold hover:from-primary-600 hover:to-primary-700 transition-all disabled:opacity-50"
          >
            {loading ? (
              <div className="w-5 h-5 border-2 border-white/30 border-t-white rounded-full spinner" />
            ) : (
              <>
                Send Reset Link
                <ArrowRight size={18} />
              </>
            )}
          </button>
        </form>

        <p className="text-center text-sm text-dark-400">
          <Link href="/login" className="inline-flex items-center gap-1 text-primary-400 font-medium hover:text-primary-300">
            <ArrowLeft size={14} />
            Back to Login
          </Link>
        </p>
      </div>
    </div>
  );
}
