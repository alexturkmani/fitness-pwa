'use client';
import { useState, useEffect, useCallback, Suspense } from 'react';
import { useRouter, useSearchParams } from 'next/navigation';
import { useWorkoutPlan } from '@/hooks/useWorkoutPlan';
import { useWorkoutLogs } from '@/hooks/useWorkoutLogs';
import { WorkoutLog, ExerciseLog, SetLog } from '@/types';
import { generateId, formatDate } from '@/lib/utils';
import Card from '@/components/ui/Card';
import Button from '@/components/ui/Button';
import { Save, Timer, Check, ArrowLeft, Plus, Minus } from 'lucide-react';

function RestTimer({ seconds, onComplete }: { seconds: number; onComplete: () => void }) {
  const [remaining, setRemaining] = useState(seconds);

  useEffect(() => {
    if (remaining <= 0) {
      onComplete();
      if (typeof navigator !== 'undefined' && navigator.vibrate) {
        navigator.vibrate([200, 100, 200]);
      }
      return;
    }
    const timer = setInterval(() => setRemaining((r) => r - 1), 1000);
    return () => clearInterval(timer);
  }, [remaining, onComplete]);

  const circumference = 2 * Math.PI * 45;
  const progress = ((seconds - remaining) / seconds) * circumference;

  return (
    <div className="flex flex-col items-center py-4">
      <svg width="100" height="100" className="rest-timer-ring">
        <circle cx="50" cy="50" r="45" fill="none" stroke="#1e293b" strokeWidth="6" />
        <circle
          cx="50" cy="50" r="45" fill="none" stroke="#10b981" strokeWidth="6"
          strokeLinecap="round"
          strokeDasharray={circumference}
          strokeDashoffset={circumference - progress}
        />
      </svg>
      <p className="text-2xl font-bold text-primary-400 -mt-16 mb-12">{remaining}s</p>
      <p className="text-xs text-dark-500">Rest Timer</p>
    </div>
  );
}

function WorkoutLogContent() {
  const router = useRouter();
  const searchParams = useSearchParams();
  const dayId = searchParams.get('day');
  const planId = searchParams.get('plan');

  const { currentPlan } = useWorkoutPlan();
  const { addLog } = useWorkoutLogs();

  const plan = currentPlan;
  const workoutDay = plan?.days.find((d) => d.id === dayId);

  const [exerciseLogs, setExerciseLogs] = useState<ExerciseLog[]>([]);
  const [activeTimer, setActiveTimer] = useState<{ exerciseIdx: number; seconds: number } | null>(null);

  useEffect(() => {
    if (workoutDay) {
      setExerciseLogs(
        workoutDay.exercises.map((ex) => ({
          exerciseId: ex.id,
          exerciseName: ex.name,
          sets: Array.from({ length: ex.sets }, (_, i) => ({
            setNumber: i + 1,
            weight: 0,
            reps: parseInt(ex.reps) || 10,
            completed: false,
          })),
        }))
      );
    }
  }, [workoutDay]);

  const updateSet = (exIdx: number, setIdx: number, field: keyof SetLog, value: any) => {
    setExerciseLogs((prev) => {
      const updated = [...prev];
      const sets = [...updated[exIdx].sets];
      sets[setIdx] = { ...sets[setIdx], [field]: value };
      updated[exIdx] = { ...updated[exIdx], sets };
      return updated;
    });
  };

  const completeSet = (exIdx: number, setIdx: number) => {
    updateSet(exIdx, setIdx, 'completed', true);
    const exercise = workoutDay?.exercises[exIdx];
    if (exercise) {
      setActiveTimer({ exerciseIdx: exIdx, seconds: exercise.restSeconds });
    }
  };

  const handleTimerComplete = useCallback(() => {
    setActiveTimer(null);
  }, []);

  const handleSave = () => {
    if (!plan || !workoutDay) return;
    const log: WorkoutLog = {
      id: generateId(),
      date: formatDate(new Date()),
      planId: plan.id,
      dayId: workoutDay.id,
      exercises: exerciseLogs,
      createdAt: new Date().toISOString(),
    };
    addLog(log);
    router.push('/workouts');
  };

  if (!workoutDay) {
    return (
      <div className="py-8 text-center">
        <p className="text-dark-400">Workout not found.</p>
        <Button onClick={() => router.push('/workouts')} variant="secondary" className="mt-4">
          Back to Workouts
        </Button>
      </div>
    );
  }

  return (
    <div className="py-6 space-y-6">
      {/* Header */}
      <div className="flex items-center gap-3">
        <button onClick={() => router.push('/workouts')} className="p-2 text-dark-400 hover:text-dark-200">
          <ArrowLeft size={20} />
        </button>
        <div>
          <h1 className="text-xl font-bold text-dark-100">{workoutDay.dayLabel}</h1>
          <p className="text-sm text-dark-400">{workoutDay.exercises.length} exercises</p>
        </div>
      </div>

      {/* Active Timer */}
      {activeTimer && (
        <Card className="border-primary-500/30">
          <RestTimer seconds={activeTimer.seconds} onComplete={handleTimerComplete} />
        </Card>
      )}

      {/* Exercises */}
      {exerciseLogs.map((exLog, exIdx) => {
        const exercise = workoutDay.exercises[exIdx];
        return (
          <Card key={exLog.exerciseId}>
            <div className="flex items-center justify-between mb-1">
              <h3 className="font-semibold text-dark-100">{exercise.name}</h3>
              <span className="badge badge-blue">{exercise.muscleGroup}</span>
            </div>
            <p className="text-xs text-dark-500 mb-3">
              {exercise.sets} sets x {exercise.reps} reps | {exercise.restSeconds}s rest
            </p>

            <div className="space-y-2">
              <div className="grid grid-cols-12 gap-2 text-xs text-dark-500 px-1">
                <span className="col-span-2">Set</span>
                <span className="col-span-4">Weight (kg)</span>
                <span className="col-span-3">Reps</span>
                <span className="col-span-3 text-right">Done</span>
              </div>

              {exLog.sets.map((set, setIdx) => (
                <div key={set.setNumber} className={`grid grid-cols-12 gap-2 items-center p-2 rounded-lg ${set.completed ? 'bg-primary-500/10' : 'bg-dark-800/40'}`}>
                  <span className="col-span-2 text-sm font-medium text-dark-400">{set.setNumber}</span>
                  <div className="col-span-4 flex items-center gap-1">
                    <button onClick={() => updateSet(exIdx, setIdx, 'weight', Math.max(0, set.weight - 2.5))} className="p-1 text-dark-500 hover:text-dark-300">
                      <Minus size={14} />
                    </button>
                    <input
                      type="number"
                      value={set.weight || ''}
                      onChange={(e) => updateSet(exIdx, setIdx, 'weight', parseFloat(e.target.value) || 0)}
                      className="w-full text-center bg-dark-900/60 border border-dark-700 rounded-lg py-1.5 text-sm text-dark-200 focus:border-primary-500 outline-none"
                      placeholder="0"
                    />
                    <button onClick={() => updateSet(exIdx, setIdx, 'weight', set.weight + 2.5)} className="p-1 text-dark-500 hover:text-dark-300">
                      <Plus size={14} />
                    </button>
                  </div>
                  <div className="col-span-3 flex items-center gap-1">
                    <button onClick={() => updateSet(exIdx, setIdx, 'reps', Math.max(0, set.reps - 1))} className="p-1 text-dark-500 hover:text-dark-300">
                      <Minus size={14} />
                    </button>
                    <input
                      type="number"
                      value={set.reps || ''}
                      onChange={(e) => updateSet(exIdx, setIdx, 'reps', parseInt(e.target.value) || 0)}
                      className="w-full text-center bg-dark-900/60 border border-dark-700 rounded-lg py-1.5 text-sm text-dark-200 focus:border-primary-500 outline-none"
                    />
                    <button onClick={() => updateSet(exIdx, setIdx, 'reps', set.reps + 1)} className="p-1 text-dark-500 hover:text-dark-300">
                      <Plus size={14} />
                    </button>
                  </div>
                  <div className="col-span-3 flex justify-end">
                    {set.completed ? (
                      <div className="w-8 h-8 rounded-full bg-primary-500/20 flex items-center justify-center">
                        <Check className="text-primary-400" size={16} />
                      </div>
                    ) : (
                      <button
                        onClick={() => completeSet(exIdx, setIdx)}
                        disabled={!set.weight || !set.reps}
                        className="w-8 h-8 rounded-full border-2 border-dark-600 hover:border-primary-500 transition-colors disabled:opacity-30"
                      />
                    )}
                  </div>
                </div>
              ))}
            </div>
          </Card>
        );
      })}

      {/* Save Button */}
      <Button onClick={handleSave} className="w-full" size="lg">
        <Save size={20} /> Save Workout
      </Button>
    </div>
  );
}

export default function WorkoutLogPage() {
  return (
    <Suspense fallback={<div className="py-8 text-center text-dark-400">Loading...</div>}>
      <WorkoutLogContent />
    </Suspense>
  );
}
