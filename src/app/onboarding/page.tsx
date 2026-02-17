'use client';
import { useState } from 'react';
import { useRouter } from 'next/navigation';
import { useUserProfile } from '@/hooks/useUserProfile';
import { useWorkoutPlan } from '@/hooks/useWorkoutPlan';
import { UserProfile } from '@/types';
import { generateId } from '@/lib/utils';
import { ACTIVITY_LEVELS, FITNESS_GOALS, ROTATION_EXPLANATION } from '@/lib/constants';
import Button from '@/components/ui/Button';
import Card from '@/components/ui/Card';
import {
  User, Activity, Target, Calendar, RotateCcw, ChevronRight, ChevronLeft,
  TrendingDown, Dumbbell, Zap, Heart, Sparkles, Check, Users
} from 'lucide-react';

const goalIcons: Record<string, any> = {
  TrendingDown, Dumbbell, Zap, Heart, Activity,
};

export default function OnboardingPage() {
  const router = useRouter();
  const { setProfile } = useUserProfile();
  const { savePlan } = useWorkoutPlan();
  const [step, setStep] = useState(1);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [formData, setFormData] = useState({
    name: '',
    weight: '',
    height: '',
    age: '',
    gender: 'male' as 'male' | 'female' | 'other',
    activityLevel: '' as string,
    fitnessGoals: [] as string[],
    targetWeight: '',
    intervalWeeks: 6 as 6 | 8,
    workoutStyle: 'muscle_group' as 'single_muscle' | 'muscle_group',
  });

  const totalSteps = 6;

  const updateField = (field: string, value: any) => {
    setFormData((prev) => ({ ...prev, [field]: value }));
  };

  const canProceed = () => {
    switch (step) {
      case 1: return formData.name && formData.weight && formData.height && formData.age;
      case 2: return formData.activityLevel;
      case 3: return formData.fitnessGoals.length > 0;
      case 4: return formData.targetWeight;
      case 5: return true;
      case 6: return true;
      default: return false;
    }
  };

  const handleComplete = async () => {
    setLoading(true);
    setError(null);
    try {
      const profile: UserProfile = {
        id: generateId(),
        name: formData.name.trim(),
        weight: parseFloat(formData.weight),
        height: parseFloat(formData.height),
        age: parseInt(formData.age),
        gender: formData.gender,
        activityLevel: formData.activityLevel as UserProfile['activityLevel'],
        fitnessGoals: formData.fitnessGoals as UserProfile['fitnessGoals'],
        targetWeight: parseFloat(formData.targetWeight),
        intervalWeeks: formData.intervalWeeks,
        onboardingCompleted: true,
        createdAt: new Date().toISOString(),
        updatedAt: new Date().toISOString(),
      };

      setProfile(profile);

      let planGenerated = false;
      try {
        const res = await fetch('/api/ai/workout', {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({ profile, currentInterval: 0, workoutStyle: formData.workoutStyle }),
        });

        if (res.ok) {
          const plan = await res.json();
          savePlan(plan);
          planGenerated = true;
        } else {
          const errData = await res.json().catch(() => ({ error: 'Unknown error' }));
          console.error('Workout API error:', errData);
        }
      } catch (e) {
        console.error('Failed to generate workout plan:', e);
      }

      router.push(planGenerated ? '/workouts' : '/');
    } catch (err: any) {
      setError(err.message || 'Something went wrong. Please try again.');
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen flex flex-col py-8">
      {/* Progress Bar */}
      <div className="mb-8">
        <div className="flex items-center justify-between mb-2">
          <span className="text-sm text-dark-400">Step {step} of {totalSteps}</span>
          <span className="text-sm text-primary-400">{Math.round((step / totalSteps) * 100)}%</span>
        </div>
        <div className="h-2 bg-dark-800 rounded-full overflow-hidden">
          <div
            className="h-full bg-gradient-to-r from-primary-500 to-accent-500 rounded-full transition-all duration-500"
            style={{ width: `${(step / totalSteps) * 100}%` }}
          />
        </div>
      </div>

      {/* Step 1: Personal Info */}
      {step === 1 && (
        <div className="flex-1">
          <div className="flex items-center gap-3 mb-6">
            <div className="p-3 bg-primary-500/20 rounded-xl">
              <User className="text-primary-400" size={24} />
            </div>
            <div>
              <h1 className="text-2xl font-bold text-dark-100">Personal Info</h1>
              <p className="text-dark-400">Tell us about yourself</p>
            </div>
          </div>

          <div className="space-y-4">
            <div>
              <label className="block text-sm font-medium text-dark-300 mb-2">Your Name</label>
              <input
                type="text"
                className="input-field"
                placeholder="John"
                value={formData.name}
                onChange={(e) => updateField('name', e.target.value)}
              />
            </div>
            <div>
              <label className="block text-sm font-medium text-dark-300 mb-2">Weight (kg)</label>
              <input
                type="number"
                className="input-field"
                placeholder="75"
                value={formData.weight}
                onChange={(e) => updateField('weight', e.target.value)}
              />
            </div>
            <div>
              <label className="block text-sm font-medium text-dark-300 mb-2">Height (cm)</label>
              <input
                type="number"
                className="input-field"
                placeholder="175"
                value={formData.height}
                onChange={(e) => updateField('height', e.target.value)}
              />
            </div>
            <div>
              <label className="block text-sm font-medium text-dark-300 mb-2">Age</label>
              <input
                type="number"
                className="input-field"
                placeholder="28"
                value={formData.age}
                onChange={(e) => updateField('age', e.target.value)}
              />
            </div>
            <div>
              <label className="block text-sm font-medium text-dark-300 mb-2">Gender</label>
              <div className="grid grid-cols-3 gap-3">
                {(['male', 'female', 'other'] as const).map((g) => (
                  <button
                    key={g}
                    onClick={() => updateField('gender', g)}
                    className={`py-3 rounded-xl text-sm font-medium transition-all ${
                      formData.gender === g
                        ? 'bg-primary-500/20 border border-primary-500 text-primary-400'
                        : 'bg-dark-800/60 border border-dark-700 text-dark-400 hover:border-dark-600'
                    }`}
                  >
                    {g.charAt(0).toUpperCase() + g.slice(1)}
                  </button>
                ))}
              </div>
            </div>
          </div>
        </div>
      )}

      {/* Step 2: Activity Level */}
      {step === 2 && (
        <div className="flex-1">
          <div className="flex items-center gap-3 mb-6">
            <div className="p-3 bg-accent-500/20 rounded-xl">
              <Activity className="text-accent-400" size={24} />
            </div>
            <div>
              <h1 className="text-2xl font-bold text-dark-100">Activity Level</h1>
              <p className="text-dark-400">How active are you?</p>
            </div>
          </div>

          <div className="space-y-3">
            {ACTIVITY_LEVELS.map((level) => (
              <Card
                key={level.value}
                hover
                onClick={() => updateField('activityLevel', level.value)}
                className={`${
                  formData.activityLevel === level.value
                    ? '!border-primary-500 bg-primary-500/10'
                    : ''
                }`}
              >
                <div className="flex items-center justify-between">
                  <div>
                    <h3 className="font-semibold text-dark-100">{level.label}</h3>
                    <p className="text-sm text-dark-400 mt-1">{level.description}</p>
                  </div>
                  {formData.activityLevel === level.value && (
                    <Check className="text-primary-400" size={20} />
                  )}
                </div>
              </Card>
            ))}
          </div>
        </div>
      )}

      {/* Step 3: Fitness Goals */}
      {step === 3 && (
        <div className="flex-1">
          <div className="flex items-center gap-3 mb-6">
            <div className="p-3 bg-primary-500/20 rounded-xl">
              <Target className="text-primary-400" size={24} />
            </div>
            <div>
              <h1 className="text-2xl font-bold text-dark-100">Your Goals</h1>
              <p className="text-dark-400">Select one or more goals</p>
            </div>
          </div>

          <div className="space-y-3">
            {FITNESS_GOALS.map((goal) => {
              const Icon = goalIcons[goal.icon] || Sparkles;
              const isSelected = formData.fitnessGoals.includes(goal.value);
              return (
                <Card
                  key={goal.value}
                  hover
                  onClick={() => {
                    const current = formData.fitnessGoals;
                    if (current.includes(goal.value)) {
                      updateField('fitnessGoals', current.filter((g: string) => g !== goal.value));
                    } else {
                      updateField('fitnessGoals', [...current, goal.value]);
                    }
                  }}
                  className={`${
                    isSelected
                      ? '!border-primary-500 bg-primary-500/10'
                      : ''
                  }`}
                >
                  <div className="flex items-center gap-4">
                    <div className={`p-2.5 rounded-xl ${isSelected ? 'bg-primary-500/20' : 'bg-dark-700/50'}`}>
                      <Icon className={isSelected ? 'text-primary-400' : 'text-dark-400'} size={22} />
                    </div>
                    <div className="flex-1">
                      <h3 className="font-semibold text-dark-100">{goal.label}</h3>
                      <p className="text-sm text-dark-400 mt-0.5">{goal.description}</p>
                    </div>
                    {isSelected && (
                      <Check className="text-primary-400" size={20} />
                    )}
                  </div>
                </Card>
              );
            })}
          </div>
        </div>
      )}

      {/* Step 4: Target Weight & Interval */}
      {step === 4 && (
        <div className="flex-1">
          <div className="flex items-center gap-3 mb-6">
            <div className="p-3 bg-accent-500/20 rounded-xl">
              <Calendar className="text-accent-400" size={24} />
            </div>
            <div>
              <h1 className="text-2xl font-bold text-dark-100">Target & Duration</h1>
              <p className="text-dark-400">Set your target and training interval</p>
            </div>
          </div>

          <div className="space-y-6">
            <div>
              <label className="block text-sm font-medium text-dark-300 mb-2">Target Weight (kg)</label>
              <input
                type="number"
                className="input-field"
                placeholder="70"
                value={formData.targetWeight}
                onChange={(e) => updateField('targetWeight', e.target.value)}
              />
            </div>

            <div>
              <label className="block text-sm font-medium text-dark-300 mb-3">Training Interval</label>
              <div className="grid grid-cols-2 gap-3">
                {([6, 8] as const).map((weeks) => (
                  <button
                    key={weeks}
                    onClick={() => updateField('intervalWeeks', weeks)}
                    className={`p-4 rounded-xl text-center transition-all ${
                      formData.intervalWeeks === weeks
                        ? 'bg-primary-500/20 border-2 border-primary-500'
                        : 'bg-dark-800/60 border-2 border-dark-700 hover:border-dark-600'
                    }`}
                  >
                    <span className={`text-2xl font-bold ${formData.intervalWeeks === weeks ? 'text-primary-400' : 'text-dark-300'}`}>
                      {weeks}
                    </span>
                    <span className={`block text-sm mt-1 ${formData.intervalWeeks === weeks ? 'text-primary-400' : 'text-dark-500'}`}>
                      weeks
                    </span>
                  </button>
                ))}
              </div>
            </div>
          </div>
        </div>
      )}

      {/* Step 5: Rotation Explanation */}
      {step === 5 && (
        <div className="flex-1">
          <div className="flex items-center gap-3 mb-6">
            <div className="p-3 bg-primary-500/20 rounded-xl">
              <RotateCcw className="text-primary-400" size={24} />
            </div>
            <div>
              <h1 className="text-2xl font-bold text-dark-100">{ROTATION_EXPLANATION.title}</h1>
              <p className="text-dark-400">The science of periodization</p>
            </div>
          </div>

          <Card className="mb-6">
            <div className="space-y-4">
              {ROTATION_EXPLANATION.points.map((point, i) => (
                <div key={i} className="flex items-start gap-3">
                  <div className="mt-0.5 w-6 h-6 rounded-full bg-primary-500/20 flex items-center justify-center flex-shrink-0">
                    <span className="text-xs font-bold text-primary-400">{i + 1}</span>
                  </div>
                  <p className="text-dark-300 text-sm leading-relaxed">{point}</p>
                </div>
              ))}
            </div>
          </Card>

          <Card className="border-primary-500/30 bg-primary-500/5">
            <p className="text-sm text-dark-300">
              Your AI coach will design a <span className="text-primary-400 font-semibold">{formData.intervalWeeks}-week program</span> and
              automatically assess your progress at the end of each interval to create an optimized follow-up plan.
            </p>
          </Card>
        </div>
      )}

      {/* Step 6: Workout Style */}
      {step === 6 && (
        <div className="flex-1">
          <div className="flex items-center gap-3 mb-6">
            <div className="p-3 bg-accent-500/20 rounded-xl">
              <Dumbbell className="text-accent-400" size={24} />
            </div>
            <div>
              <h1 className="text-2xl font-bold text-dark-100">Workout Style</h1>
              <p className="text-dark-400">How should your workouts be structured?</p>
            </div>
          </div>

          <div className="space-y-3">
            <button
              onClick={() => updateField('workoutStyle', 'single_muscle')}
              className={`w-full text-left p-4 rounded-xl border transition-all flex items-center gap-3 ${
                formData.workoutStyle === 'single_muscle'
                  ? 'border-primary-500 bg-primary-500/10'
                  : 'border-dark-700 bg-dark-800/60 hover:border-dark-600'
              }`}
            >
              <div className={`p-2.5 rounded-lg ${formData.workoutStyle === 'single_muscle' ? 'bg-primary-500/20' : 'bg-dark-700/50'}`}>
                <User className={formData.workoutStyle === 'single_muscle' ? 'text-primary-400' : 'text-dark-400'} size={20} />
              </div>
              <div className="flex-1">
                <p className={`font-medium ${formData.workoutStyle === 'single_muscle' ? 'text-primary-400' : 'text-dark-200'}`}>Single Muscle</p>
                <p className="text-xs text-dark-500">Each day targets one specific muscle (e.g., Chest Day, Back Day, Arm Day)</p>
              </div>
              {formData.workoutStyle === 'single_muscle' && <Check className="text-primary-400" size={18} />}
            </button>

            <button
              onClick={() => updateField('workoutStyle', 'muscle_group')}
              className={`w-full text-left p-4 rounded-xl border transition-all flex items-center gap-3 ${
                formData.workoutStyle === 'muscle_group'
                  ? 'border-primary-500 bg-primary-500/10'
                  : 'border-dark-700 bg-dark-800/60 hover:border-dark-600'
              }`}
            >
              <div className={`p-2.5 rounded-lg ${formData.workoutStyle === 'muscle_group' ? 'bg-primary-500/20' : 'bg-dark-700/50'}`}>
                <Users className={formData.workoutStyle === 'muscle_group' ? 'text-primary-400' : 'text-dark-400'} size={20} />
              </div>
              <div className="flex-1">
                <p className={`font-medium ${formData.workoutStyle === 'muscle_group' ? 'text-primary-400' : 'text-dark-200'}`}>Muscle Groups</p>
                <p className="text-xs text-dark-500">Combine related muscles per day (e.g., Push/Pull/Legs, Upper/Lower)</p>
              </div>
              {formData.workoutStyle === 'muscle_group' && <Check className="text-primary-400" size={18} />}
            </button>
          </div>
        </div>
      )}

      {/* Error Display */}
      {error && (
        <Card className="border-red-500/30 bg-red-500/5 mt-4">
          <p className="text-sm text-red-400">{error}</p>
        </Card>
      )}

      {/* Navigation Buttons */}
      <div className="flex items-center gap-3 mt-8 pt-4 border-t border-dark-800">
        {step > 1 && (
          <Button variant="secondary" onClick={() => setStep(step - 1)} className="flex-1">
            <ChevronLeft size={18} /> Back
          </Button>
        )}
        {step < totalSteps ? (
          <Button onClick={() => setStep(step + 1)} disabled={!canProceed()} className="flex-1">
            Next <ChevronRight size={18} />
          </Button>
        ) : (
          <Button onClick={handleComplete} loading={loading} className="flex-1">
            {loading ? 'AI is designing your plan...' : 'Generate My Plan'}
          </Button>
        )}
      </div>
    </div>
  );
}
