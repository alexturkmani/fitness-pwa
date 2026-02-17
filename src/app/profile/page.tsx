'use client';
import { useState } from 'react';
import { useRouter } from 'next/navigation';
import { useUserProfile } from '@/hooks/useUserProfile';
import { ACTIVITY_LEVELS, FITNESS_GOALS } from '@/lib/constants';
import Card from '@/components/ui/Card';
import Button from '@/components/ui/Button';
import Modal from '@/components/ui/Modal';
import {
  ArrowLeft, User, Save, Check, Pencil,
  TrendingDown, Dumbbell, Zap, Heart, Activity, Sparkles
} from 'lucide-react';

const goalIcons: Record<string, any> = {
  TrendingDown, Dumbbell, Zap, Heart, Activity,
};

export default function ProfilePage() {
  const router = useRouter();
  const { profile, updateProfile } = useUserProfile();

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
    </div>
  );
}
