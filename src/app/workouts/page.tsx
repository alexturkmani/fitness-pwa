'use client';
import { useState } from 'react';
import { useRouter } from 'next/navigation';
import { useWorkoutPlan } from '@/hooks/useWorkoutPlan';
import { useWorkoutLogs } from '@/hooks/useWorkoutLogs';
import { useUserProfile } from '@/hooks/useUserProfile';
import Card from '@/components/ui/Card';
import Button from '@/components/ui/Button';
import EmptyState from '@/components/ui/EmptyState';
import LoadingSpinner from '@/components/ui/LoadingSpinner';
import Modal from '@/components/ui/Modal';
import { Dumbbell, Calendar, Clock, ChevronRight, Trophy, AlertCircle, Check, User, Users } from 'lucide-react';
import { formatDate } from '@/lib/utils';

type WorkoutStyle = 'single_muscle' | 'muscle_group';

export default function WorkoutsPage() {
  const router = useRouter();
  const { profile } = useUserProfile();
  const { currentPlan, savePlan, isIntervalComplete, getCurrentWeek, getDaysRemaining } = useWorkoutPlan();
  const { logs, getLogsByDate, getLogsByPlan } = useWorkoutLogs();
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [assessmentResult, setAssessmentResult] = useState<any>(null);
  const [showStyleModal, setShowStyleModal] = useState(false);
  const [workoutStyle, setWorkoutStyle] = useState<WorkoutStyle>('muscle_group');

  const today = formatDate(new Date());
  const todayDayNumber = new Date().getDay() || 7; // 1=Mon, 7=Sun

  const todayLog = getLogsByDate(today);
  const todayWorkout = currentPlan?.days.find((d) => d.dayNumber === todayDayNumber);

  const promptStyleSelection = () => {
    setShowStyleModal(true);
  };

  const handleGenerateNewPlan = async (style?: WorkoutStyle) => {
    setShowStyleModal(false);
    setLoading(true);
    setError(null);
    const chosenStyle = style || workoutStyle;
    try {
      let assessment: string | undefined;

      if (currentPlan && isIntervalComplete()) {
        const planLogs = getLogsByPlan(currentPlan.id);
        if (planLogs.length > 0) {
          const assessRes = await fetch('/api/ai/assess', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ type: 'workout', logs: planLogs }),
          });
          if (assessRes.ok) {
            const result = await assessRes.json();
            setAssessmentResult(result);
            assessment = result.summary;
          }
        }
      }

      const res = await fetch('/api/ai/workout', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          profile,
          previousLogs: currentPlan ? getLogsByPlan(currentPlan.id) : undefined,
          assessment,
          currentInterval: currentPlan?.intervalNumber || 0,
          workoutStyle: chosenStyle,
        }),
      });

      if (res.ok) {
        const plan = await res.json();
        savePlan(plan);
        setAssessmentResult(null);
      } else {
        const errData = await res.json().catch(() => ({ error: 'Unknown error' }));
        setError(errData.error || 'Failed to generate workout plan. Please try again.');
      }
    } catch (err: any) {
      console.error('Failed to generate plan:', err);
      setError(err.message || 'Failed to generate workout plan. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return <LoadingSpinner message="AI is designing your workout plan..." />;
  }

  if (!currentPlan) {
    return (
      <div className="py-8">
        <EmptyState
          icon={<Dumbbell size={48} />}
          title="No Workout Plan"
          description="Generate your AI-powered workout plan to get started."
          actionLabel="Generate Plan"
          onAction={promptStyleSelection}
        />
        {error && (
          <Card className="border-red-500/30 bg-red-500/5 mt-4">
            <p className="text-sm text-red-400">{error}</p>
          </Card>
        )}

        {/* Workout Style Selection Modal */}
        <Modal isOpen={showStyleModal} onClose={() => setShowStyleModal(false)} title="Workout Style">
          <p className="text-sm text-dark-400 mb-4">How would you like your workouts structured?</p>
          <div className="space-y-3">
            <button
              onClick={() => setWorkoutStyle('single_muscle')}
              className={`w-full text-left p-4 rounded-xl border transition-all flex items-center gap-3 ${
                workoutStyle === 'single_muscle'
                  ? 'border-primary-500 bg-primary-500/10'
                  : 'border-dark-700 bg-dark-800/60 hover:border-dark-600'
              }`}
            >
              <div className={`p-2.5 rounded-lg ${workoutStyle === 'single_muscle' ? 'bg-primary-500/20' : 'bg-dark-700/50'}`}>
                <User className={workoutStyle === 'single_muscle' ? 'text-primary-400' : 'text-dark-400'} size={20} />
              </div>
              <div className="flex-1">
                <p className={`font-medium ${workoutStyle === 'single_muscle' ? 'text-primary-400' : 'text-dark-200'}`}>Single Muscle</p>
                <p className="text-xs text-dark-500">Each day targets one specific muscle (e.g., Chest Day, Back Day)</p>
              </div>
              {workoutStyle === 'single_muscle' && <Check className="text-primary-400" size={18} />}
            </button>

            <button
              onClick={() => setWorkoutStyle('muscle_group')}
              className={`w-full text-left p-4 rounded-xl border transition-all flex items-center gap-3 ${
                workoutStyle === 'muscle_group'
                  ? 'border-primary-500 bg-primary-500/10'
                  : 'border-dark-700 bg-dark-800/60 hover:border-dark-600'
              }`}
            >
              <div className={`p-2.5 rounded-lg ${workoutStyle === 'muscle_group' ? 'bg-primary-500/20' : 'bg-dark-700/50'}`}>
                <Users className={workoutStyle === 'muscle_group' ? 'text-primary-400' : 'text-dark-400'} size={20} />
              </div>
              <div className="flex-1">
                <p className={`font-medium ${workoutStyle === 'muscle_group' ? 'text-primary-400' : 'text-dark-200'}`}>Muscle Groups</p>
                <p className="text-xs text-dark-500">Combine related muscles per day (e.g., Push/Pull/Legs, Upper/Lower)</p>
              </div>
              {workoutStyle === 'muscle_group' && <Check className="text-primary-400" size={18} />}
            </button>

            <Button className="w-full mt-2" onClick={() => handleGenerateNewPlan(workoutStyle)}>
              Generate Plan
            </Button>
          </div>
        </Modal>
      </div>
    );
  }

  return (
    <div className="py-6 space-y-6">
      {/* Header */}
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold text-dark-100">Your Workout Plan</h1>
          <p className="text-dark-400 mt-1">Interval #{currentPlan.intervalNumber}</p>
        </div>
        <Button size="sm" variant="secondary" onClick={promptStyleSelection}>
          Regenerate
        </Button>
      </div>

      {/* Progress Stats */}
      <div className="grid grid-cols-3 gap-3">
        <div className="stat-card text-center">
          <Calendar className="mx-auto text-primary-400 mb-1" size={20} />
          <p className="text-lg font-bold text-dark-100">Week {getCurrentWeek()}</p>
          <p className="text-xs text-dark-500">of {currentPlan.weeks}</p>
        </div>
        <div className="stat-card text-center">
          <Clock className="mx-auto text-accent-400 mb-1" size={20} />
          <p className="text-lg font-bold text-dark-100">{getDaysRemaining()}</p>
          <p className="text-xs text-dark-500">days left</p>
        </div>
        <div className="stat-card text-center">
          <Trophy className="mx-auto text-yellow-400 mb-1" size={20} />
          <p className="text-lg font-bold text-dark-100">{getLogsByPlan(currentPlan.id).length}</p>
          <p className="text-xs text-dark-500">logged</p>
        </div>
      </div>

      {/* Interval Complete Banner */}
      {isIntervalComplete() && (
        <Card className="border-yellow-500/30 bg-yellow-500/5">
          <div className="flex items-start gap-3">
            <AlertCircle className="text-yellow-400 flex-shrink-0 mt-0.5" size={20} />
            <div className="flex-1">
              <h3 className="font-semibold text-yellow-400">Interval Complete</h3>
              <p className="text-sm text-dark-400 mt-1">
                Time to rotate your exercises. The AI will assess your progress and design your next plan.
              </p>
              <Button onClick={promptStyleSelection} loading={loading} size="sm" className="mt-3">
                Generate Next Plan
              </Button>
            </div>
          </div>
        </Card>
      )}

      {/* Assessment Result */}
      {assessmentResult && (
        <Card className="border-accent-500/30 bg-accent-500/5">
          <h3 className="font-semibold text-accent-400 mb-2">AI Assessment</h3>
          <p className="text-sm text-dark-300">{assessmentResult.summary}</p>
          {assessmentResult.weakPoints && (
            <div className="mt-3">
              <p className="text-xs text-dark-500 mb-1">Areas to improve:</p>
              <div className="flex flex-wrap gap-1">
                {assessmentResult.weakPoints.map((wp: string, i: number) => (
                  <span key={i} className="badge badge-yellow">{wp}</span>
                ))}
              </div>
            </div>
          )}
        </Card>
      )}

      {/* AI Notes */}
      {currentPlan.aiNotes && (
        <Card className="bg-dark-800/30">
          <p className="text-sm text-dark-400 italic">{currentPlan.aiNotes}</p>
        </Card>
      )}

      {/* Weekly Schedule */}
      <div className="space-y-3">
        <h2 className="text-lg font-semibold text-dark-200">Weekly Schedule</h2>
        {currentPlan.days.map((day) => {
          const isToday = day.dayNumber === todayDayNumber;
          const dayNames = ['', 'Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday', 'Sunday'];
          const hasLog = logs.some((l) => l.dayId === day.id && l.date === today);

          return (
            <Card
              key={day.id}
              hover={!day.isRestDay}
              onClick={() => !day.isRestDay && router.push(`/workouts/log?day=${day.id}&plan=${currentPlan.id}`)}
              className={`${isToday ? '!border-primary-500/50' : ''} ${day.isRestDay ? 'opacity-60' : ''}`}
            >
              <div className="flex items-center justify-between">
                <div className="flex items-center gap-3">
                  <div className={`w-10 h-10 rounded-xl flex items-center justify-center text-sm font-bold ${
                    isToday ? 'bg-primary-500/20 text-primary-400' : 'bg-dark-700/50 text-dark-400'
                  }`}>
                    {dayNames[day.dayNumber]?.slice(0, 2)}
                  </div>
                  <div>
                    <div className="flex items-center gap-2">
                      <h3 className="font-semibold text-dark-100">{day.dayLabel}</h3>
                      {isToday && <span className="badge badge-green">Today</span>}
                      {hasLog && <Check className="text-primary-400" size={16} />}
                    </div>
                    <p className="text-xs text-dark-500 mt-0.5">
                      {day.isRestDay ? 'Rest & Recovery' : `${day.exercises.length} exercises`}
                    </p>
                  </div>
                </div>
                {!day.isRestDay && (
                  <ChevronRight className="text-dark-600" size={18} />
                )}
              </div>

              {!day.isRestDay && (
                <div className="mt-3 flex flex-wrap gap-1.5">
                  {day.exercises.map((ex) => (
                    <span key={ex.id} className="text-xs px-2 py-1 bg-dark-700/40 rounded-lg text-dark-400">
                      {ex.name}
                    </span>
                  ))}
                </div>
              )}
            </Card>
          );
        })}
      </div>

      {/* Workout Style Selection Modal */}
      <Modal isOpen={showStyleModal} onClose={() => setShowStyleModal(false)} title="Workout Style">
        <p className="text-sm text-dark-400 mb-4">How would you like your workouts structured?</p>
        <div className="space-y-3">
          <button
            onClick={() => setWorkoutStyle('single_muscle')}
            className={`w-full text-left p-4 rounded-xl border transition-all flex items-center gap-3 ${
              workoutStyle === 'single_muscle'
                ? 'border-primary-500 bg-primary-500/10'
                : 'border-dark-700 bg-dark-800/60 hover:border-dark-600'
            }`}
          >
            <div className={`p-2.5 rounded-lg ${workoutStyle === 'single_muscle' ? 'bg-primary-500/20' : 'bg-dark-700/50'}`}>
              <User className={workoutStyle === 'single_muscle' ? 'text-primary-400' : 'text-dark-400'} size={20} />
            </div>
            <div className="flex-1">
              <p className={`font-medium ${workoutStyle === 'single_muscle' ? 'text-primary-400' : 'text-dark-200'}`}>Single Muscle</p>
              <p className="text-xs text-dark-500">Each day targets one specific muscle (e.g., Chest Day, Back Day)</p>
            </div>
            {workoutStyle === 'single_muscle' && <Check className="text-primary-400" size={18} />}
          </button>

          <button
            onClick={() => setWorkoutStyle('muscle_group')}
            className={`w-full text-left p-4 rounded-xl border transition-all flex items-center gap-3 ${
              workoutStyle === 'muscle_group'
                ? 'border-primary-500 bg-primary-500/10'
                : 'border-dark-700 bg-dark-800/60 hover:border-dark-600'
            }`}
          >
            <div className={`p-2.5 rounded-lg ${workoutStyle === 'muscle_group' ? 'bg-primary-500/20' : 'bg-dark-700/50'}`}>
              <Users className={workoutStyle === 'muscle_group' ? 'text-primary-400' : 'text-dark-400'} size={20} />
            </div>
            <div className="flex-1">
              <p className={`font-medium ${workoutStyle === 'muscle_group' ? 'text-primary-400' : 'text-dark-200'}`}>Muscle Groups</p>
              <p className="text-xs text-dark-500">Combine related muscles per day (e.g., Push/Pull/Legs, Upper/Lower)</p>
            </div>
            {workoutStyle === 'muscle_group' && <Check className="text-primary-400" size={18} />}
          </button>

          <Button className="w-full mt-2" onClick={() => handleGenerateNewPlan(workoutStyle)}>
            Generate Plan
          </Button>
        </div>
      </Modal>
    </div>
  );
}
