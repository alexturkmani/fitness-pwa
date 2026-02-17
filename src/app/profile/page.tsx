'use client';
import { useState, useEffect, Suspense } from 'react';
import { useRouter, useSearchParams } from 'next/navigation';
import { signOut, useSession } from 'next-auth/react';
import Link from 'next/link';
import { useUserProfile } from '@/hooks/useUserProfile';
import { useTheme } from '@/components/ThemeProvider';
import { ACTIVITY_LEVELS, FITNESS_GOALS } from '@/lib/constants';
import Card from '@/components/ui/Card';
import Button from '@/components/ui/Button';
import Modal from '@/components/ui/Modal';
import Toast from '@/components/ui/Toast';
import {
  ArrowLeft, User, Save, Check, Pencil,
  TrendingDown, Dumbbell, Zap, Heart, Activity, Sparkles,
  Crown, ChevronRight, LogOut, Sun, Moon, Lock, Mail, Eye, EyeOff
} from 'lucide-react';

const goalIcons: Record<string, any> = {
  TrendingDown, Dumbbell, Zap, Heart, Activity,
};

export default function ProfilePage() {
  return (
    <Suspense fallback={null}>
      <ProfileContent />
    </Suspense>
  );
}

function ProfileContent() {
  const router = useRouter();
  const searchParams = useSearchParams();
  const { data: session } = useSession();
  const { profile, updateProfile } = useUserProfile();
  const { theme, toggleTheme } = useTheme();

  // Email change status from redirect
  const emailChangeStatus = searchParams.get('email-change');

  const [editing, setEditing] = useState(false);
  const [formData, setFormData] = useState({
    name: profile.name || '',
    weight: String(profile.weight || ''),
    height: String(profile.height || ''),
    age: String(profile.age || ''),
    gender: profile.gender || 'male',
    activityLevel: profile.activityLevel || 'moderately_active',
    fitnessGoals: profile.fitnessGoals || ['general_fitness'],
    targetWeight: String(profile.targetWeight || ''),
    intervalWeeks: profile.intervalWeeks || 6,
    gymDaysPerWeek: profile.gymDaysPerWeek || 5,
  });

  const [showGoalModal, setShowGoalModal] = useState(false);
  const [showActivityModal, setShowActivityModal] = useState(false);
  const [saved, setSaved] = useState(false);
  const [toast, setToast] = useState<string | null>(null);

  // Password change state
  const [showPasswordModal, setShowPasswordModal] = useState(false);
  const [currentPassword, setCurrentPassword] = useState('');
  const [newPassword, setNewPassword] = useState('');
  const [confirmNewPassword, setConfirmNewPassword] = useState('');
  const [showPasswords, setShowPasswords] = useState(false);
  const [passwordLoading, setPasswordLoading] = useState(false);
  const [passwordError, setPasswordError] = useState('');

  // Email change state
  const [showEmailModal, setShowEmailModal] = useState(false);
  const [newEmail, setNewEmail] = useState('');
  const [emailLoading, setEmailLoading] = useState(false);
  const [emailError, setEmailError] = useState('');
  const [emailSent, setEmailSent] = useState(false);

  // Check if user signed up with password (not Google-only)
  const isPasswordUser = !!(session?.user as any)?.email;
  // Google users don't have a password field set, so we allow email change for all but password change only for credential users

  useEffect(() => {
    if (emailChangeStatus === 'success') {
      setToast('Email updated successfully! Please sign in again.');
      // Clear the query param
      router.replace('/profile');
      // Sign out so they sign in with new email
      setTimeout(() => signOut({ callbackUrl: '/login' }), 3000);
    } else if (emailChangeStatus === 'expired') {
      setToast('Email change link expired. Please try again.');
      router.replace('/profile');
    } else if (emailChangeStatus === 'taken') {
      setToast('That email is already in use by another account.');
      router.replace('/profile');
    } else if (emailChangeStatus === 'invalid' || emailChangeStatus === 'failed') {
      setToast('Email change failed. Please try again.');
      router.replace('/profile');
    }
  }, [emailChangeStatus, router]);

  const handleChangePassword = async () => {
    setPasswordError('');

    if (newPassword.length < 6) {
      setPasswordError('New password must be at least 6 characters');
      return;
    }
    if (newPassword !== confirmNewPassword) {
      setPasswordError('Passwords do not match');
      return;
    }

    setPasswordLoading(true);
    try {
      const res = await fetch('/api/auth/change-password', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ currentPassword, newPassword }),
      });
      const data = await res.json();
      if (!res.ok) {
        setPasswordError(data.error || 'Failed to change password');
      } else {
        setShowPasswordModal(false);
        setCurrentPassword('');
        setNewPassword('');
        setConfirmNewPassword('');
        setToast('Password updated successfully!');
      }
    } catch {
      setPasswordError('Something went wrong');
    } finally {
      setPasswordLoading(false);
    }
  };

  const handleChangeEmail = async () => {
    setEmailError('');

    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailRegex.test(newEmail)) {
      setEmailError('Please enter a valid email address');
      return;
    }

    setEmailLoading(true);
    try {
      const res = await fetch('/api/auth/change-email', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ newEmail }),
      });
      const data = await res.json();
      if (!res.ok) {
        setEmailError(data.error || 'Failed to send verification email');
      } else {
        setEmailSent(true);
      }
    } catch {
      setEmailError('Something went wrong');
    } finally {
      setEmailLoading(false);
    }
  };

  const updateField = (field: string, value: any) => {
    setFormData((prev) => ({ ...prev, [field]: value }));
  };

  const handleSave = () => {
    updateProfile({
      name: formData.name.trim(),
      weight: parseFloat(formData.weight) || profile.weight,
      height: parseFloat(formData.height) || profile.height,
      age: parseInt(formData.age) || profile.age,
      gender: formData.gender as any,
      activityLevel: formData.activityLevel as any,
      fitnessGoals: formData.fitnessGoals as any,
      targetWeight: parseFloat(formData.targetWeight) || profile.targetWeight,
      intervalWeeks: formData.intervalWeeks as 6 | 8,
      gymDaysPerWeek: formData.gymDaysPerWeek,
    });
    setEditing(false);
    setSaved(true);
    setTimeout(() => setSaved(false), 2000);
  };

  const goalLabels: Record<string, string> = {
    weight_loss: 'Weight Loss',
    muscle_gain: 'Muscle Gain',
    strength: 'Strength',
    endurance: 'Endurance',
    general_fitness: 'General Fitness',
  };

  const activityLabels: Record<string, string> = {
    sedentary: 'Sedentary',
    lightly_active: 'Lightly Active',
    moderately_active: 'Moderately Active',
    very_active: 'Very Active',
    extremely_active: 'Extremely Active',
  };

  return (
    <div className="py-6 space-y-6">
      {toast && <Toast message={toast} onClose={() => setToast(null)} />}

      {/* Header */}
      <div className="flex items-center justify-between">
        <div className="flex items-center gap-3">
          <button onClick={() => router.back()} className="p-2 text-dark-400 hover:text-dark-200 transition-colors">
            <ArrowLeft size={20} />
          </button>
          <h1 className="text-2xl font-bold text-dark-100">My Profile</h1>
        </div>
        {!editing ? (
          <Button size="sm" variant="secondary" onClick={() => setEditing(true)}>
            <Pencil size={16} /> Edit
          </Button>
        ) : (
          <Button size="sm" onClick={handleSave}>
            <Save size={16} /> Save
          </Button>
        )}
      </div>

      {saved && (
        <Card className="border-primary-500/30 bg-primary-500/5">
          <div className="flex items-center gap-2">
            <Check className="text-primary-400" size={18} />
            <p className="text-sm text-primary-400 font-medium">Profile saved successfully!</p>
          </div>
        </Card>
      )}

      {/* Avatar & Name */}
      <Card>
        <div className="flex items-center gap-4">
          <div className="w-16 h-16 rounded-full bg-primary-500/20 flex items-center justify-center">
            <User className="text-primary-400" size={32} />
          </div>
          <div className="flex-1">
            {editing ? (
              <input
                type="text"
                className="input-field text-lg font-bold"
                placeholder="Your name"
                value={formData.name}
                onChange={(e) => updateField('name', e.target.value)}
              />
            ) : (
              <>
                <h2 className="text-xl font-bold text-dark-100">{profile.name || 'No name set'}</h2>
                <p className="text-sm text-dark-400">Member since {new Date(profile.createdAt).toLocaleDateString()}</p>
              </>
            )}
          </div>
        </div>
      </Card>

      {/* Body Stats */}
      <div>
        <h3 className="text-lg font-semibold text-dark-200 mb-3">Body Stats</h3>
        <Card>
          <div className="space-y-4">
            <div className="flex items-center justify-between">
              <span className="text-dark-400">Weight</span>
              {editing ? (
                <input
                  type="number"
                  className="input-field w-24 text-right text-sm py-2 px-3"
                  value={formData.weight}
                  onChange={(e) => updateField('weight', e.target.value)}
                />
              ) : (
                <span className="text-dark-100 font-medium">{profile.weight} kg</span>
              )}
            </div>
            <div className="border-t border-dark-700/50" />
            <div className="flex items-center justify-between">
              <span className="text-dark-400">Height</span>
              {editing ? (
                <input
                  type="number"
                  className="input-field w-24 text-right text-sm py-2 px-3"
                  value={formData.height}
                  onChange={(e) => updateField('height', e.target.value)}
                />
              ) : (
                <span className="text-dark-100 font-medium">{profile.height} cm</span>
              )}
            </div>
            <div className="border-t border-dark-700/50" />
            <div className="flex items-center justify-between">
              <span className="text-dark-400">Age</span>
              {editing ? (
                <input
                  type="number"
                  className="input-field w-24 text-right text-sm py-2 px-3"
                  value={formData.age}
                  onChange={(e) => updateField('age', e.target.value)}
                />
              ) : (
                <span className="text-dark-100 font-medium">{profile.age} years</span>
              )}
            </div>
            <div className="border-t border-dark-700/50" />
            <div className="flex items-center justify-between">
              <span className="text-dark-400">Gender</span>
              {editing ? (
                <div className="flex gap-2">
                  {(['male', 'female', 'other'] as const).map((g) => (
                    <button
                      key={g}
                      onClick={() => updateField('gender', g)}
                      className={`px-3 py-1.5 rounded-lg text-xs font-medium transition-all ${
                        formData.gender === g
                          ? 'bg-primary-500/20 border border-primary-500 text-primary-400'
                          : 'bg-dark-800/60 border border-dark-700 text-dark-400'
                      }`}
                    >
                      {g.charAt(0).toUpperCase() + g.slice(1)}
                    </button>
                  ))}
                </div>
              ) : (
                <span className="text-dark-100 font-medium capitalize">{profile.gender}</span>
              )}
            </div>
            <div className="border-t border-dark-700/50" />
            <div className="flex items-center justify-between">
              <span className="text-dark-400">Target Weight</span>
              {editing ? (
                <input
                  type="number"
                  className="input-field w-24 text-right text-sm py-2 px-3"
                  value={formData.targetWeight}
                  onChange={(e) => updateField('targetWeight', e.target.value)}
                />
              ) : (
                <span className="text-dark-100 font-medium">{profile.targetWeight} kg</span>
              )}
            </div>
          </div>
        </Card>
      </div>

      {/* Activity Level */}
      <div>
        <h3 className="text-lg font-semibold text-dark-200 mb-3">Activity Level</h3>
        <Card hover={editing} onClick={editing ? () => setShowActivityModal(true) : undefined}>
          <div className="flex items-center justify-between">
            <div>
              <p className="font-medium text-dark-100">
                {activityLabels[editing ? formData.activityLevel : profile.activityLevel] || profile.activityLevel}
              </p>
              <p className="text-sm text-dark-400 mt-0.5">
                {ACTIVITY_LEVELS.find(a => a.value === (editing ? formData.activityLevel : profile.activityLevel))?.description}
              </p>
            </div>
            {editing && <Pencil className="text-dark-500" size={16} />}
          </div>
        </Card>
      </div>

      {/* Fitness Goals */}
      <div>
        <h3 className="text-lg font-semibold text-dark-200 mb-3">Fitness Goals</h3>
        <Card hover={editing} onClick={editing ? () => setShowGoalModal(true) : undefined}>
          <div className="flex items-center justify-between">
            <div className="flex flex-wrap gap-2">
              {(editing ? formData.fitnessGoals : (profile.fitnessGoals || ['general_fitness'])).map((g: string) => (
                <span key={g} className="badge badge-green">{goalLabels[g] || g}</span>
              ))}
            </div>
            {editing && <Pencil className="text-dark-500 flex-shrink-0" size={16} />}
          </div>
        </Card>
      </div>

      {/* Training Interval */}
      <div>
        <h3 className="text-lg font-semibold text-dark-200 mb-3">Training Settings</h3>
        <Card>
          <div className="space-y-4">
            <div className="flex items-center justify-between">
              <span className="text-dark-400">Rotation period</span>
              {editing ? (
                <div className="flex gap-2">
                  {([6, 8] as const).map((w) => (
                    <button
                      key={w}
                      onClick={() => updateField('intervalWeeks', w)}
                      className={`px-4 py-1.5 rounded-lg text-sm font-medium transition-all ${
                        formData.intervalWeeks === w
                          ? 'bg-primary-500/20 border border-primary-500 text-primary-400'
                          : 'bg-dark-800/60 border border-dark-700 text-dark-400'
                      }`}
                    >
                      {w} weeks
                    </button>
                  ))}
                </div>
              ) : (
                <span className="text-dark-100 font-medium">{profile.intervalWeeks} weeks</span>
              )}
            </div>
            <div className="border-t border-dark-700/50" />
            <div className="flex items-center justify-between">
              <span className="text-dark-400">Gym days / week</span>
              {editing ? (
                <div className="flex gap-1.5">
                  {[3, 4, 5, 6, 7].map((d) => (
                    <button
                      key={d}
                      onClick={() => updateField('gymDaysPerWeek', d)}
                      className={`w-9 h-9 rounded-lg text-sm font-medium transition-all ${
                        formData.gymDaysPerWeek === d
                          ? 'bg-primary-500/20 border border-primary-500 text-primary-400'
                          : 'bg-dark-800/60 border border-dark-700 text-dark-400'
                      }`}
                    >
                      {d}
                    </button>
                  ))}
                </div>
              ) : (
                <span className="text-dark-100 font-medium">{profile.gymDaysPerWeek || 5} days</span>
              )}
            </div>
          </div>
        </Card>
      </div>

      {/* Activity Level Modal */}
      <Modal isOpen={showActivityModal} onClose={() => setShowActivityModal(false)} title="Activity Level">
        <div className="space-y-2">
          {ACTIVITY_LEVELS.map((level) => (
            <button
              key={level.value}
              onClick={() => { updateField('activityLevel', level.value); setShowActivityModal(false); }}
              className={`w-full text-left p-3 rounded-xl border transition-all ${
                formData.activityLevel === level.value
                  ? 'border-primary-500 bg-primary-500/10'
                  : 'border-dark-700 bg-dark-800/60 hover:border-dark-600'
              }`}
            >
              <p className={`font-medium text-sm ${formData.activityLevel === level.value ? 'text-primary-400' : 'text-dark-200'}`}>{level.label}</p>
              <p className="text-xs text-dark-500 mt-0.5">{level.description}</p>
            </button>
          ))}
        </div>
      </Modal>

      {/* Goals Modal */}
      <Modal isOpen={showGoalModal} onClose={() => setShowGoalModal(false)} title="Fitness Goals">
        <p className="text-sm text-dark-400 mb-3">Select one or more goals</p>
        <div className="space-y-2">
          {FITNESS_GOALS.map((goal) => {
            const Icon = goalIcons[goal.icon] || Sparkles;
            const isSelected = formData.fitnessGoals.includes(goal.value);
            return (
              <button
                key={goal.value}
                onClick={() => {
                  const current = formData.fitnessGoals;
                  if (current.includes(goal.value)) {
                    if (current.length > 1) {
                      updateField('fitnessGoals', current.filter((g: string) => g !== goal.value));
                    }
                  } else {
                    updateField('fitnessGoals', [...current, goal.value]);
                  }
                }}
                className={`w-full text-left p-3 rounded-xl border transition-all flex items-center gap-3 ${
                  isSelected
                    ? 'border-primary-500 bg-primary-500/10'
                    : 'border-dark-700 bg-dark-800/60 hover:border-dark-600'
                }`}
              >
                <Icon className={isSelected ? 'text-primary-400' : 'text-dark-400'} size={18} />
                <div className="flex-1">
                  <p className={`font-medium text-sm ${isSelected ? 'text-primary-400' : 'text-dark-200'}`}>{goal.label}</p>
                </div>
                {isSelected && <Check className="text-primary-400" size={16} />}
              </button>
            );
          })}
        </div>
        <Button className="w-full mt-3" onClick={() => setShowGoalModal(false)}>Done</Button>
      </Modal>

      {/* Appearance */}
      <div>
        <h3 className="text-lg font-semibold text-dark-200 mb-3">Appearance</h3>
        <Card>
          <div className="flex items-center justify-between">
            <div className="flex items-center gap-3">
              {theme === 'dark' ? (
                <Moon className="text-accent-400" size={20} />
              ) : (
                <Sun className="text-yellow-500" size={20} />
              )}
              <div>
                <p className="font-medium text-dark-100">Dark Mode</p>
                <p className="text-sm text-dark-400">{theme === 'dark' ? 'On' : 'Off'}</p>
              </div>
            </div>
            <button
              onClick={toggleTheme}
              className={`relative w-12 h-7 rounded-full transition-colors duration-200 ${
                theme === 'dark' ? 'bg-primary-500' : 'bg-dark-700'
              }`}
            >
              <span
                className={`absolute top-0.5 left-0.5 w-6 h-6 rounded-full bg-white shadow transition-transform duration-200 ${
                  theme === 'dark' ? 'translate-x-5' : 'translate-x-0'
                }`}
              />
            </button>
          </div>
        </Card>
      </div>

      {/* Account Security */}
      <div>
        <h3 className="text-lg font-semibold text-dark-200 mb-3">Account Security</h3>
        <Card>
          <div className="space-y-4">
            {/* Current Email */}
            <div className="flex items-center justify-between">
              <div className="flex items-center gap-3">
                <Mail className="text-dark-500" size={18} />
                <div>
                  <p className="text-sm text-dark-400">Email</p>
                  <p className="text-sm font-medium text-dark-100">{session?.user?.email || '—'}</p>
                </div>
              </div>
              <button
                onClick={() => { setShowEmailModal(true); setNewEmail(''); setEmailError(''); setEmailSent(false); }}
                className="text-sm text-primary-400 font-medium hover:text-primary-300"
              >
                Change
              </button>
            </div>
            <div className="border-t border-dark-700/50" />
            {/* Password */}
            <div className="flex items-center justify-between">
              <div className="flex items-center gap-3">
                <Lock className="text-dark-500" size={18} />
                <div>
                  <p className="text-sm text-dark-400">Password</p>
                  <p className="text-sm font-medium text-dark-100">••••••••</p>
                </div>
              </div>
              <button
                onClick={() => { setShowPasswordModal(true); setCurrentPassword(''); setNewPassword(''); setConfirmNewPassword(''); setPasswordError(''); }}
                className="text-sm text-primary-400 font-medium hover:text-primary-300"
              >
                Change
              </button>
            </div>
          </div>
        </Card>
      </div>

      {/* Change Password Modal */}
      <Modal isOpen={showPasswordModal} onClose={() => setShowPasswordModal(false)} title="Change Password">
        <div className="space-y-4">
          <div className="relative">
            <input
              type={showPasswords ? 'text' : 'password'}
              placeholder="Current password"
              value={currentPassword}
              onChange={(e) => setCurrentPassword(e.target.value)}
              className="input-field pr-12"
            />
            <button
              type="button"
              onClick={() => setShowPasswords(!showPasswords)}
              className="absolute right-4 top-1/2 -translate-y-1/2 text-dark-500 hover:text-dark-300"
            >
              {showPasswords ? <EyeOff size={16} /> : <Eye size={16} />}
            </button>
          </div>
          <input
            type={showPasswords ? 'text' : 'password'}
            placeholder="New password (min. 6 characters)"
            value={newPassword}
            onChange={(e) => setNewPassword(e.target.value)}
            className="input-field"
          />
          <input
            type={showPasswords ? 'text' : 'password'}
            placeholder="Confirm new password"
            value={confirmNewPassword}
            onChange={(e) => setConfirmNewPassword(e.target.value)}
            className="input-field"
          />
          {passwordError && <p className="text-sm text-red-500">{passwordError}</p>}
          <Button
            className="w-full"
            onClick={handleChangePassword}
            loading={passwordLoading}
            disabled={!currentPassword || !newPassword || !confirmNewPassword}
          >
            Update Password
          </Button>
        </div>
      </Modal>

      {/* Change Email Modal */}
      <Modal isOpen={showEmailModal} onClose={() => setShowEmailModal(false)} title="Change Email">
        {emailSent ? (
          <div className="space-y-4 text-center">
            <div className="w-14 h-14 rounded-full bg-primary-500/20 flex items-center justify-center mx-auto">
              <Mail className="text-primary-500" size={24} />
            </div>
            <h3 className="text-lg font-semibold text-dark-100">Check your inbox</h3>
            <p className="text-sm text-dark-400">
              We sent a verification link to <span className="font-medium text-dark-200">{newEmail}</span>. Click the link to confirm your new email address.
            </p>
            <Button variant="secondary" className="w-full" onClick={() => setShowEmailModal(false)}>
              Done
            </Button>
          </div>
        ) : (
          <div className="space-y-4">
            <p className="text-sm text-dark-400">Enter your new email address. You&apos;ll need to verify it before the change takes effect.</p>
            <input
              type="email"
              placeholder="New email address"
              value={newEmail}
              onChange={(e) => setNewEmail(e.target.value)}
              className="input-field"
            />
            {emailError && <p className="text-sm text-red-500">{emailError}</p>}
            <Button
              className="w-full"
              onClick={handleChangeEmail}
              loading={emailLoading}
              disabled={!newEmail}
            >
              Send Verification Email
            </Button>
          </div>
        )}
      </Modal>

      {/* Subscription & Account */}
      <Link href="/subscription">
        <Card className="card-hover">
          <div className="flex items-center justify-between">
            <div className="flex items-center gap-3">
              <div className="w-10 h-10 rounded-xl bg-yellow-500/10 flex items-center justify-center">
                <Crown className="text-yellow-500" size={20} />
              </div>
              <div>
                <p className="font-medium text-dark-100">Subscription & Billing</p>
                <p className="text-sm text-dark-400">Manage your plan, cancel, or upgrade</p>
              </div>
            </div>
            <ChevronRight className="text-dark-500" size={20} />
          </div>
        </Card>
      </Link>

      <button
        onClick={() => signOut({ callbackUrl: '/login' })}
        className="w-full flex items-center justify-center gap-2 p-3.5 rounded-xl border border-dark-700 text-red-400 font-medium hover:bg-red-500/5 hover:border-red-500/30 transition-all"
      >
        <LogOut size={18} />
        Sign Out
      </button>
    </div>
  );
}
