'use client';
import { useState } from 'react';
import { signIn } from 'next-auth/react';
import { useRouter } from 'next/navigation';
import Link from 'next/link';
import { Dumbbell, Mail, Lock, User, Eye, EyeOff, ArrowRight, CheckCircle, MailCheck } from 'lucide-react';

export default function RegisterPage() {
  const router = useRouter();
  const [name, setName] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [showPassword, setShowPassword] = useState(false);
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const [googleLoading, setGoogleLoading] = useState(false);
  const [emailSent, setEmailSent] = useState(false);

  const handleRegister = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');

    if (password !== confirmPassword) {
      setError('Passwords do not match');
      return;
    }

    if (password.length < 6) {
      setError('Password must be at least 6 characters');
      return;
    }

    setLoading(true);

    try {
      const res = await fetch('/api/auth/register', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ name, email, password }),
      });

      const data = await res.json();

      if (!res.ok) {
        setError(data.error || 'Registration failed');
        setLoading(false);
        return;
      }

      // Show "check your email" message
      setEmailSent(true);
      setLoading(false);
    } catch (err) {
      setError('Something went wrong. Please try again.');
      setLoading(false);
    }
  };

  const handleGoogleRegister = async () => {
    setGoogleLoading(true);
    await signIn('google', { callbackUrl: '/paywall' });
  };

  return (
    <div className="min-h-screen flex flex-col items-center justify-center px-6 -mt-10">
      {/* Logo */}
      <div className="flex items-center gap-3 mb-6">
        <div className="w-12 h-12 rounded-2xl bg-primary-500 flex items-center justify-center">
          <Dumbbell className="text-white" size={24} />
        </div>
        <h1 className="text-3xl font-bold">
          <span className="gradient-text">FitMate</span>
        </h1>
      </div>

      <div className="w-full max-w-sm space-y-5">
        {emailSent ? (
          /* Email sent confirmation */
          <div className="text-center space-y-5 py-8">
            <div className="w-16 h-16 rounded-full bg-primary-500/10 flex items-center justify-center mx-auto">
              <MailCheck className="text-primary-500" size={32} />
            </div>
            <div>
              <h2 className="text-2xl font-bold text-dark-100">Check your email</h2>
              <p className="text-dark-400 mt-2">
                We sent a verification link to <br />
                <span className="font-medium text-dark-200">{email}</span>
              </p>
            </div>
            <div className="p-4 rounded-xl bg-primary-500/5 border border-primary-500/20">
              <p className="text-sm text-dark-300">
                Click the link in your email to verify your account, then sign in to start your 7-day free trial.
              </p>
            </div>
            <Link
              href="/login"
              className="inline-flex items-center gap-2 text-primary-400 font-medium hover:text-primary-300"
            >
              Go to Sign In
              <ArrowRight size={16} />
            </Link>
          </div>
        ) : (
        <>
        <div className="text-center">
          <h2 className="text-2xl font-bold text-dark-100">Create account</h2>
          <p className="text-dark-400 mt-1">Start your 7-day free trial</p>
        </div>

        {/* Trial benefits */}
        <div className="p-4 rounded-xl bg-primary-500/5 border border-primary-500/20">
          <div className="space-y-2">
            {['AI workout plans', 'AI meal plans', 'Barcode food scanner', 'Progress tracking'].map((benefit) => (
              <div key={benefit} className="flex items-center gap-2">
                <CheckCircle className="text-primary-500 flex-shrink-0" size={16} />
                <span className="text-sm text-dark-200">{benefit}</span>
              </div>
            ))}
          </div>
          <p className="text-xs text-dark-500 mt-3">7 days free, then $4.99/month. Cancel anytime.</p>
        </div>

        {/* Google Sign Up */}
        <button
          onClick={handleGoogleRegister}
          disabled={googleLoading}
          className="w-full flex items-center justify-center gap-3 p-3.5 rounded-xl border border-dark-700 bg-white hover:bg-dark-950 transition-all font-medium text-dark-100 disabled:opacity-50"
        >
          {googleLoading ? (
            <div className="w-5 h-5 border-2 border-dark-700 border-t-primary-500 rounded-full spinner" />
          ) : (
            <svg width="20" height="20" viewBox="0 0 24 24">
              <path d="M22.56 12.25c0-.78-.07-1.53-.2-2.25H12v4.26h5.92a5.06 5.06 0 0 1-2.2 3.32v2.77h3.57c2.08-1.92 3.28-4.74 3.28-8.1z" fill="#4285F4" />
              <path d="M12 23c2.97 0 5.46-.98 7.28-2.66l-3.57-2.77c-.98.66-2.23 1.06-3.71 1.06-2.86 0-5.29-1.93-6.16-4.53H2.18v2.84C3.99 20.53 7.7 23 12 23z" fill="#34A853" />
              <path d="M5.84 14.09c-.22-.66-.35-1.36-.35-2.09s.13-1.43.35-2.09V7.07H2.18C1.43 8.55 1 10.22 1 12s.43 3.45 1.18 4.93l2.85-2.22.81-.62z" fill="#FBBC05" />
              <path d="M12 5.38c1.62 0 3.06.56 4.21 1.64l3.15-3.15C17.45 2.09 14.97 1 12 1 7.7 1 3.99 3.47 2.18 7.07l3.66 2.84c.87-2.6 3.3-4.53 6.16-4.53z" fill="#EA4335" />
            </svg>
          )}
          Continue with Google
        </button>

        {/* Divider */}
        <div className="flex items-center gap-3">
          <div className="flex-1 h-px bg-dark-700" />
          <span className="text-sm text-dark-500">or</span>
          <div className="flex-1 h-px bg-dark-700" />
        </div>

        {/* Email Form */}
        <form onSubmit={handleRegister} className="space-y-3">
          <div className="relative">
            <User className="absolute left-4 top-1/2 -translate-y-1/2 text-dark-500" size={18} />
            <input
              type="text"
              placeholder="Full name"
              value={name}
              onChange={(e) => setName(e.target.value)}
              className="input-field pl-12"
            />
          </div>

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

          <div className="relative">
            <Lock className="absolute left-4 top-1/2 -translate-y-1/2 text-dark-500" size={18} />
            <input
              type={showPassword ? 'text' : 'password'}
              placeholder="Password (min 6 characters)"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              className="input-field pl-12 pr-12"
              required
            />
            <button
              type="button"
              onClick={() => setShowPassword(!showPassword)}
              className="absolute right-4 top-1/2 -translate-y-1/2 text-dark-500 hover:text-dark-300"
            >
              {showPassword ? <EyeOff size={18} /> : <Eye size={18} />}
            </button>
          </div>

          <div className="relative">
            <Lock className="absolute left-4 top-1/2 -translate-y-1/2 text-dark-500" size={18} />
            <input
              type={showPassword ? 'text' : 'password'}
              placeholder="Confirm password"
              value={confirmPassword}
              onChange={(e) => setConfirmPassword(e.target.value)}
              className="input-field pl-12"
              required
            />
          </div>

          {error && (
            <p className="text-sm text-red-500 text-center">{error}</p>
          )}

          <button
            type="submit"
            disabled={loading || !email || !password}
            className="w-full flex items-center justify-center gap-2 p-3.5 rounded-xl bg-gradient-to-r from-primary-500 to-primary-600 text-white font-semibold hover:from-primary-600 hover:to-primary-700 transition-all disabled:opacity-50"
          >
            {loading ? (
              <div className="w-5 h-5 border-2 border-white/30 border-t-white rounded-full spinner" />
            ) : (
              <>
                Start Free Trial
                <ArrowRight size={18} />
              </>
            )}
          </button>
        </form>

        <p className="text-center text-sm text-dark-400">
          Already have an account?{' '}
          <Link href="/login" className="text-primary-400 font-medium hover:text-primary-300">
            Sign in
          </Link>
        </p>
        </>
        )}
      </div>
    </div>
  );
}
