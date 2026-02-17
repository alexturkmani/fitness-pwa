'use client';
import { useEffect, useState } from 'react';
import { useRouter } from 'next/navigation';
import { useUserProfile } from '@/hooks/useUserProfile';
import { useWorkoutPlan } from '@/hooks/useWorkoutPlan';
import { useWorkoutLogs } from '@/hooks/useWorkoutLogs';
import { useFoodLog } from '@/hooks/useFoodLog';
import { useWeightLog } from '@/hooks/useWeightLog';
import { formatDate } from '@/lib/utils';
import { FITNESS_GOALS } from '@/lib/constants';
import { UserProfile } from '@/types';
import Card from '@/components/ui/Card';
import Button from '@/components/ui/Button';
import Modal from '@/components/ui/Modal';
import {
  Dumbbell, Flame, Target, Scale, ChevronRight, Calendar,
  UtensilsCrossed, BarChart3, ScanLine, Sparkles, Settings,
  TrendingDown, Zap, Heart, Activity, Check
} from 'lucide-react';
import Link from 'next/link';

const goalIcons: Record<string, any> = {
  TrendingDown, Dumbbell, Zap, Heart, Activity,
};

export default function DashboardPage() {
  const router = useRouter();
  const { profile, isOnboarded, updateProfile } = useUserProfile();
  const [showGoalModal, setShowGoalModal] = useState(false);
  const [selectedGoals, setSelectedGoals] = useState<string[]>(profile.fitnessGoals || ['general_fitness']);
  const { currentPlan, getCurrentWeek, getDaysRemaining } = useWorkoutPlan();
  const { getThisWeekCount } = useWorkoutLogs();
  const { getDayTotals } = useFoodLog();
  const { getLatestWeight } = useWeightLog();

  useEffect(() => {
    if (!isOnboarded) {
      router.push('/onboarding');
    }
  }, [isOnboarded, router]);

  if (!isOnboarded) return null;

  const today = formatDate(new Date());
  const todayTotals = getDayTotals(today);
  const todayDayNumber = new Date().getDay() || 7;
  const todayWorkout = currentPlan?.days.find((d) => d.dayNumber === todayDayNumber);

  const goalLabels: Record<string, string> = {
    weight_loss: 'Weight Loss',
    muscle_gain: 'Muscle Gain',
    strength: 'Strength',
    endurance: 'Endurance',
    general_fitness: 'General Fitness',
  };

  const activeGoals = profile.fitnessGoals || ['general_fitness'];
  const goalDisplay = activeGoals.map(g => goalLabels[g] || g).join(', ');

  return (
    <div className="py-6 space-y-6">
      {/* Greeting */}
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold text-dark-100">
            <span className="gradient-text">FitMate</span>
          </h1>
          <p className="text-dark-400 mt-1">
            Goals: <span className="text-primary-400">{goalDisplay}</span>
          </p>
        </div>
        <button
          onClick={() => { setSelectedGoals([...(profile.fitnessGoals || ['general_fitness'])]); setShowGoalModal(true); }}
          className="p-2.5 bg-dark-800/60 border border-dark-700 rounded-xl text-dark-400 hover:text-primary-400 hover:border-primary-500/50 transition-all"
          title="Change Goal"
        >
          <Settings size={20} />
        </button>
      </div>

      {/* Quick Stats */}
      <div className="grid grid-cols-2 gap-3">
        <div className="stat-card">
          <Scale className="text-accent-400 mb-1" size={18} />
          <p className="text-xl font-bold text-dark-100">{getLatestWeight() || profile.weight} kg</p>
          <p className="text-xs text-dark-500">Current Weight</p>
        </div>
        <div className="stat-card">
          <Dumbbell className="text-primary-400 mb-1" size={18} />
          <p className="text-xl font-bold text-dark-100">{getThisWeekCount()}</p>
          <p className="text-xs text-dark-500">Workouts This Week</p>
        </div>
        <div className="stat-card">
          <Flame className="text-primary-400 mb-1" size={18} />
          <p className="text-xl font-bold text-dark-100">{Math.round(todayTotals.calories)}</p>
          <p className="text-xs text-dark-500">Calories Today</p>
        </div>
        <div className="stat-card">
          <Calendar className="text-accent-400 mb-1" size={18} />
          <p className="text-xl font-bold text-dark-100">
            {currentPlan ? `${getDaysRemaining()}d` : '--'}
          </p>
          <p className="text-xs text-dark-500">
            {currentPlan ? `Week ${getCurrentWeek()} of ${currentPlan.weeks}` : 'No Plan'}
          </p>
        </div>
      </div>

      {/* Today's Workout */}
      {todayWorkout && (
        <Card hover onClick={() => router.push(`/workouts/log?day=${todayWorkout.id}&plan=${currentPlan?.id}`)}>
          <div className="flex items-center justify-between mb-3">
            <div className="flex items-center gap-2">
              <Sparkles className="text-primary-400" size={18} />
              <h3 className="font-semibold text-dark-100">Today&apos;s Workout</h3>
            </div>
            <ChevronRight className="text-dark-600" size={18} />
          </div>
          {todayWorkout.isRestDay ? (
            <p className="text-dark-400 text-sm">Rest Day - Recovery & Stretching</p>
          ) : (
            <>
              <p className="text-primary-400 font-medium mb-2">{todayWorkout.dayLabel}</p>
              <div className="flex flex-wrap gap-1.5">
                {todayWorkout.exercises.map((ex) => (
                  <span key={ex.id} className="text-xs px-2 py-1 bg-dark-700/40 rounded-lg text-dark-400">
                    {ex.name}
                  </span>
                ))}
              </div>
            </>
          )}
        </Card>
      )}

      {!currentPlan && (
        <Card className="border-primary-500/30 bg-primary-500/5">
          <div className="flex items-center gap-3">
            <Dumbbell className="text-primary-400" size={24} />
            <div className="flex-1">
              <h3 className="font-semibold text-dark-100">Get Started</h3>
              <p className="text-sm text-dark-400">Generate your first AI workout plan</p>
            </div>
            <Button size="sm" onClick={() => router.push('/workouts')}>Go</Button>
          </div>
        </Card>
      )}

      {/* Quick Links */}
      <div className="space-y-3">
        <h2 className="text-lg font-semibold text-dark-200">Quick Actions</h2>
        <div className="grid grid-cols-2 gap-3">
          <Link href="/meals" className="block">
            <Card hover className="text-center py-4">
              <UtensilsCrossed className="mx-auto text-primary-400 mb-2" size={24} />
              <p className="text-sm font-medium text-dark-200">Meal Plans</p>
            </Card>
          </Link>
          <Link href="/scanner" className="block">
            <Card hover className="text-center py-4">
              <ScanLine className="mx-auto text-accent-400 mb-2" size={24} />
              <p className="text-sm font-medium text-dark-200">Scan Food</p>
            </Card>
          </Link>
          <Link href="/nutrition" className="block">
            <Card hover className="text-center py-4">
              <Flame className="mx-auto text-yellow-400 mb-2" size={24} />
              <p className="text-sm font-medium text-dark-200">Calorie Log</p>
            </Card>
          </Link>
          <Link href="/progress" className="block">
            <Card hover className="text-center py-4">
              <BarChart3 className="mx-auto text-primary-400 mb-2" size={24} />
              <p className="text-sm font-medium text-dark-200">Progress</p>
            </Card>
          </Link>
        </div>
      </div>

      {/* Change Goal Modal */}
      <Modal isOpen={showGoalModal} onClose={() => setShowGoalModal(false)} title="Change Your Goals">
        <p className="text-sm text-dark-400 mb-3">Select one or more fitness goals.</p>
        <div className="space-y-3">
          {FITNESS_GOALS.map((goal) => {
            const Icon = goalIcons[goal.icon] || Sparkles;
            const isSelected = selectedGoals.includes(goal.value);
            return (
              <button
                key={goal.value}
                onClick={() => {
                  if (isSelected) {
                    if (selectedGoals.length > 1) {
                      setSelectedGoals(selectedGoals.filter(g => g !== goal.value));
                    }
                  } else {
                    setSelectedGoals([...selectedGoals, goal.value]);
                  }
                }}
                className={`w-full text-left p-3 rounded-xl border transition-all flex items-center gap-3 ${
                  isSelected
                    ? 'border-primary-500 bg-primary-500/10'
                    : 'border-dark-700 bg-dark-800/60 hover:border-dark-600'
                }`}
              >
                <div className={`p-2 rounded-lg ${isSelected ? 'bg-primary-500/20' : 'bg-dark-700/50'}`}>
                  <Icon className={isSelected ? 'text-primary-400' : 'text-dark-400'} size={18} />
                </div>
                <div className="flex-1">
                  <p className={`font-medium ${isSelected ? 'text-primary-400' : 'text-dark-200'}`}>{goal.label}</p>
                  <p className="text-xs text-dark-500">{goal.description}</p>
                </div>
                {isSelected && <Check className="text-primary-400" size={18} />}
              </button>
            );
          })}
          <Button
            className="w-full mt-2"
            onClick={() => {
              updateProfile({ fitnessGoals: selectedGoals as UserProfile['fitnessGoals'] });
              setShowGoalModal(false);
            }}
            disabled={JSON.stringify(selectedGoals.sort()) === JSON.stringify([...(profile.fitnessGoals || ['general_fitness'])].sort())}
          >
            Update Goals
          </Button>
          <p className="text-xs text-dark-500 text-center mt-1">
            Changing your goals will affect meal plans and macro targets. Generate a new workout plan to match.
          </p>
        </div>
      </Modal>
    </div>
  );
}
