'use client';
import { useState } from 'react';
import { useRouter } from 'next/navigation';
import { useCustomWorkouts } from '@/hooks/useCustomWorkouts';
import { useUserProfile } from '@/hooks/useUserProfile';
import { CustomExerciseLog, CustomWorkoutLog, ExerciseSuggestion } from '@/types';
import { generateId, formatDate } from '@/lib/utils';
import Card from '@/components/ui/Card';
import Button from '@/components/ui/Button';
import Modal from '@/components/ui/Modal';
import LoadingSpinner from '@/components/ui/LoadingSpinner';
import {
  Plus, Trash2, Save, ArrowLeft, Sparkles, Dumbbell,
  ChevronDown, ChevronUp, Clock, Calendar, Lightbulb,
  RefreshCw, X, Target
} from 'lucide-react';

const MUSCLE_GROUPS = [
  'Chest', 'Back', 'Shoulders', 'Biceps', 'Triceps',
  'Quadriceps', 'Hamstrings', 'Glutes', 'Calves', 'Abs', 'Core', 'Full Body',
];

interface ExerciseForm {
  name: string;
  muscleGroup: string;
  sets: { weight: string; reps: string }[];
}

export default function CustomWorkoutsPage() {
  const router = useRouter();
  const { profile } = useUserProfile();
  const { logs, addLog, deleteLog, getRecentLogs } = useCustomWorkouts();

  // Form state
  const [view, setView] = useState<'list' | 'new'>('list');
  const [workoutName, setWorkoutName] = useState('');
  const [exercises, setExercises] = useState<ExerciseForm[]>([]);
  const [expandedExercise, setExpandedExercise] = useState<number | null>(null);

  // AI state
  const [aiLoading, setAiLoading] = useState(false);
  const [suggestions, setSuggestions] = useState<ExerciseSuggestion[] | null>(null);
  const [showSuggestions, setShowSuggestions] = useState(false);
  const [selectedLogForAI, setSelectedLogForAI] = useState<CustomWorkoutLog | null>(null);

  // Delete state
  const [confirmDelete, setConfirmDelete] = useState<string | null>(null);

  const recentLogs = getRecentLogs(20);

  const addExercise = () => {
    setExercises((prev) => [
      ...prev,
      { name: '', muscleGroup: 'Chest', sets: [{ weight: '', reps: '' }] },
    ]);
    setExpandedExercise(exercises.length);
  };

  const removeExercise = (idx: number) => {
    setExercises((prev) => prev.filter((_, i) => i !== idx));
    if (expandedExercise === idx) setExpandedExercise(null);
  };

  const updateExercise = (idx: number, field: keyof ExerciseForm, value: string) => {
    setExercises((prev) =>
      prev.map((ex, i) => (i === idx ? { ...ex, [field]: value } : ex))
    );
  };

  const addSet = (exIdx: number) => {
    setExercises((prev) =>
      prev.map((ex, i) =>
        i === exIdx ? { ...ex, sets: [...ex.sets, { weight: '', reps: '' }] } : ex
      )
    );
  };

  const removeSet = (exIdx: number, setIdx: number) => {
    setExercises((prev) =>
      prev.map((ex, i) =>
        i === exIdx ? { ...ex, sets: ex.sets.filter((_, si) => si !== setIdx) } : ex
      )
    );
  };

  const updateSet = (exIdx: number, setIdx: number, field: 'weight' | 'reps', value: string) => {
    setExercises((prev) =>
      prev.map((ex, i) =>
        i === exIdx
          ? {
              ...ex,
              sets: ex.sets.map((s, si) =>
                si === setIdx ? { ...s, [field]: value } : s
              ),
            }
          : ex
      )
    );
  };

  const canSave = workoutName.trim() && exercises.length > 0 && exercises.every(
    (ex) => ex.name.trim() && ex.sets.length > 0 && ex.sets.every((s) => s.weight && s.reps)
  );

  const handleSave = () => {
    if (!canSave) return;

    const log: CustomWorkoutLog = {
      id: generateId(),
      date: formatDate(new Date()),
      name: workoutName.trim(),
      exercises: exercises.map((ex) => ({
        id: generateId(),
        name: ex.name.trim(),
        muscleGroup: ex.muscleGroup,
        sets: ex.sets.map((s) => ({
          weight: parseFloat(s.weight) || 0,
          reps: parseInt(s.reps) || 0,
        })),
      })),
      createdAt: new Date().toISOString(),
    };

    addLog(log);
    resetForm();
    setView('list');
  };

  const resetForm = () => {
    setWorkoutName('');
    setExercises([]);
    setExpandedExercise(null);
  };

  const handleGetSuggestions = async (log: CustomWorkoutLog) => {
    setAiLoading(true);
    setSuggestions(null);
    setSelectedLogForAI(log);
    setShowSuggestions(true);

    try {
      const res = await fetch('/api/ai/exercise-suggestions', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          exercises: log.exercises,
          goals: profile?.fitnessGoals || ['general_fitness'],
        }),
      });

      if (res.ok) {
        const data = await res.json();
        setSuggestions(data.suggestions);
      } else {
        const errData = await res.json().catch(() => ({ error: 'Unknown error' }));
        alert(errData.error || 'Failed to get suggestions');
        setShowSuggestions(false);
      }
    } catch (err: any) {
      console.error('AI suggestion error:', err);
      alert('Failed to get AI suggestions. Please try again.');
      setShowSuggestions(false);
    } finally {
      setAiLoading(false);
    }
  };

  const handleDelete = (logId: string) => {
    deleteLog(logId);
    setConfirmDelete(null);
  };

  // New workout form view
  if (view === 'new') {
    return (
      <div className="py-6 space-y-6">
        <div className="flex items-center gap-3">
          <button onClick={() => { resetForm(); setView('list'); }} className="text-dark-400 hover:text-dark-200 transition-colors">
            <ArrowLeft size={24} />
          </button>
          <h1 className="text-2xl font-bold text-dark-100">Log Custom Workout</h1>
        </div>

        {/* Workout Name */}
        <Card>
          <label className="block text-sm font-medium text-dark-300 mb-2">Workout Name</label>
          <input
            type="text"
            value={workoutName}
            onChange={(e) => setWorkoutName(e.target.value)}
            placeholder="e.g., Upper Body, Leg Day, Push Day..."
            className="input w-full"
          />
        </Card>

        {/* Exercises */}
        <div className="space-y-3">
          <div className="flex items-center justify-between">
            <h2 className="text-lg font-semibold text-dark-200">Exercises</h2>
            <span className="text-sm text-dark-500">{exercises.length} added</span>
          </div>

          {exercises.map((ex, exIdx) => (
            <Card key={exIdx} className="!p-0 overflow-hidden">
              {/* Exercise Header */}
              <button
                onClick={() => setExpandedExercise(expandedExercise === exIdx ? null : exIdx)}
                className="w-full flex items-center justify-between p-4"
              >
                <div className="flex items-center gap-3">
                  <div className="w-8 h-8 rounded-lg bg-primary-500/20 flex items-center justify-center text-sm font-bold text-primary-400">
                    {exIdx + 1}
                  </div>
                  <div className="text-left">
                    <p className="font-medium text-dark-100">{ex.name || 'New Exercise'}</p>
                    <p className="text-xs text-dark-500">{ex.muscleGroup} · {ex.sets.length} set{ex.sets.length !== 1 ? 's' : ''}</p>
                  </div>
                </div>
                <div className="flex items-center gap-2">
                  <button
                    onClick={(e) => { e.stopPropagation(); removeExercise(exIdx); }}
                    className="p-1.5 text-red-400/60 hover:text-red-400 transition-colors"
                  >
                    <Trash2 size={16} />
                  </button>
                  {expandedExercise === exIdx ? <ChevronUp className="text-dark-400" size={18} /> : <ChevronDown className="text-dark-400" size={18} />}
                </div>
              </button>

              {/* Exercise Details (expanded) */}
              {expandedExercise === exIdx && (
                <div className="px-4 pb-4 space-y-4 border-t border-dark-700/50">
                  <div className="pt-4 grid grid-cols-2 gap-3">
                    <div>
                      <label className="block text-xs text-dark-500 mb-1">Exercise Name</label>
                      <input
                        type="text"
                        value={ex.name}
                        onChange={(e) => updateExercise(exIdx, 'name', e.target.value)}
                        placeholder="e.g., Bench Press"
                        className="input w-full text-sm"
                      />
                    </div>
                    <div>
                      <label className="block text-xs text-dark-500 mb-1">Muscle Group</label>
                      <select
                        value={ex.muscleGroup}
                        onChange={(e) => updateExercise(exIdx, 'muscleGroup', e.target.value)}
                        className="input w-full text-sm"
                      >
                        {MUSCLE_GROUPS.map((mg) => (
                          <option key={mg} value={mg}>{mg}</option>
                        ))}
                      </select>
                    </div>
                  </div>

                  {/* Sets */}
                  <div>
                    <div className="flex items-center justify-between mb-2">
                      <label className="text-xs text-dark-500">Sets</label>
                      <button
                        onClick={() => addSet(exIdx)}
                        className="flex items-center gap-1 text-xs text-primary-400 hover:text-primary-300 transition-colors"
                      >
                        <Plus size={14} /> Add Set
                      </button>
                    </div>
                    <div className="space-y-2">
                      {ex.sets.map((set, setIdx) => (
                        <div key={setIdx} className="flex items-center gap-2">
                          <span className="text-xs text-dark-600 w-6 text-center">{setIdx + 1}</span>
                          <div className="flex-1">
                            <input
                              type="number"
                              value={set.weight}
                              onChange={(e) => updateSet(exIdx, setIdx, 'weight', e.target.value)}
                              placeholder="kg"
                              className="input w-full text-sm text-center"
                            />
                          </div>
                          <span className="text-xs text-dark-600">×</span>
                          <div className="flex-1">
                            <input
                              type="number"
                              value={set.reps}
                              onChange={(e) => updateSet(exIdx, setIdx, 'reps', e.target.value)}
                              placeholder="reps"
                              className="input w-full text-sm text-center"
                            />
                          </div>
                          {ex.sets.length > 1 && (
                            <button
                              onClick={() => removeSet(exIdx, setIdx)}
                              className="p-1 text-dark-600 hover:text-red-400 transition-colors"
                            >
                              <X size={14} />
                            </button>
                          )}
                        </div>
                      ))}
                    </div>
                  </div>
                </div>
              )}
            </Card>
          ))}

          {/* Add Exercise Button */}
          <button
            onClick={addExercise}
            className="w-full p-4 border-2 border-dashed border-dark-700 rounded-2xl text-dark-400 hover:text-primary-400 hover:border-primary-500/50 transition-all flex items-center justify-center gap-2"
          >
            <Plus size={20} />
            <span className="font-medium">Add Exercise</span>
          </button>
        </div>

        {/* Save Button */}
        <Button onClick={handleSave} disabled={!canSave} className="w-full">
          <Save size={18} className="mr-2" />
          Save Workout
        </Button>
      </div>
    );
  }

  // List view
  return (
    <div className="py-6 space-y-6">
      {/* Header */}
      <div className="flex items-center gap-3">
        <button onClick={() => router.push('/workouts')} className="text-dark-400 hover:text-dark-200 transition-colors">
          <ArrowLeft size={24} />
        </button>
        <div className="flex-1">
          <h1 className="text-2xl font-bold text-dark-100">My Workouts</h1>
          <p className="text-dark-400 mt-1">Track your own custom workouts</p>
        </div>
      </div>

      {/* New Workout Button */}
      <Button onClick={() => setView('new')} className="w-full">
        <Plus size={18} className="mr-2" />
        Log New Workout
      </Button>

      {/* Workout History */}
      {recentLogs.length === 0 ? (
        <Card className="text-center py-10">
          <Dumbbell className="mx-auto text-dark-600 mb-3" size={40} />
          <p className="text-dark-400 font-medium">No custom workouts yet</p>
          <p className="text-sm text-dark-500 mt-1">Log your first workout to get started</p>
        </Card>
      ) : (
        <div className="space-y-3">
          <h2 className="text-lg font-semibold text-dark-200">Workout History</h2>
          {recentLogs.map((log) => (
            <Card key={log.id} className="!p-0 overflow-hidden">
              <div className="p-4">
                <div className="flex items-center justify-between mb-2">
                  <div className="flex items-center gap-3">
                    <div className="w-10 h-10 rounded-xl bg-accent-500/20 flex items-center justify-center">
                      <Dumbbell className="text-accent-400" size={20} />
                    </div>
                    <div>
                      <h3 className="font-semibold text-dark-100">{log.name}</h3>
                      <div className="flex items-center gap-3 text-xs text-dark-500 mt-0.5">
                        <span className="flex items-center gap-1"><Calendar size={12} /> {log.date}</span>
                        <span className="flex items-center gap-1"><Target size={12} /> {log.exercises.length} exercise{log.exercises.length !== 1 ? 's' : ''}</span>
                      </div>
                    </div>
                  </div>
                  <div className="flex items-center gap-1">
                    <button
                      onClick={() => handleGetSuggestions(log)}
                      className="p-2 text-yellow-400/70 hover:text-yellow-400 bg-yellow-500/10 hover:bg-yellow-500/20 rounded-xl transition-all"
                      title="Get AI suggestions"
                    >
                      <Sparkles size={18} />
                    </button>
                    <button
                      onClick={() => setConfirmDelete(log.id)}
                      className="p-2 text-dark-600 hover:text-red-400 transition-colors rounded-xl"
                      title="Delete workout"
                    >
                      <Trash2 size={16} />
                    </button>
                  </div>
                </div>

                {/* Exercise summary */}
                <div className="flex flex-wrap gap-1.5 mt-3">
                  {log.exercises.map((ex) => (
                    <span key={ex.id} className="text-xs px-2.5 py-1 bg-dark-700/40 rounded-lg text-dark-400">
                      {ex.name} · {ex.sets.length}×{ex.sets[0]?.weight}kg
                    </span>
                  ))}
                </div>
              </div>

              {/* Delete confirmation */}
              {confirmDelete === log.id && (
                <div className="px-4 pb-4 flex gap-2">
                  <Button size="sm" variant="secondary" onClick={() => setConfirmDelete(null)} className="flex-1">
                    Cancel
                  </Button>
                  <Button size="sm" onClick={() => handleDelete(log.id)} className="flex-1 !bg-red-500/20 !text-red-400 hover:!bg-red-500/30">
                    Delete
                  </Button>
                </div>
              )}
            </Card>
          ))}
        </div>
      )}

      {/* AI Suggestions Modal */}
      <Modal isOpen={showSuggestions} onClose={() => { setShowSuggestions(false); setSuggestions(null); }} title="AI Exercise Suggestions">
        {aiLoading ? (
          <div className="flex flex-col items-center py-8">
            <div className="w-10 h-10 border-3 border-primary-500/30 border-t-primary-500 rounded-full animate-spin mb-4" />
            <p className="text-sm text-dark-400">Analyzing your exercises...</p>
          </div>
        ) : suggestions ? (
          <div className="space-y-5 max-h-[65vh] overflow-y-auto">
            {selectedLogForAI && (
              <p className="text-xs text-dark-500 -mt-2 mb-2">
                Suggestions for: <span className="text-dark-300 font-medium">{selectedLogForAI.name}</span>
              </p>
            )}
            {suggestions.map((s, idx) => (
              <div key={idx} className="space-y-3">
                {/* Exercise Name */}
                <div className="flex items-center gap-2">
                  <div className="w-7 h-7 rounded-lg bg-primary-500/20 flex items-center justify-center text-xs font-bold text-primary-400">
                    {idx + 1}
                  </div>
                  <h4 className="font-semibold text-dark-100">{s.exerciseName}</h4>
                </div>

                {/* Assessment */}
                <p className="text-sm text-dark-300 pl-9">{s.assessment}</p>

                {/* Improvement Tips */}
                <div className="pl-9">
                  <p className="text-xs font-semibold text-yellow-400 flex items-center gap-1.5 mb-1.5">
                    <Lightbulb size={14} /> Improvement Tips
                  </p>
                  <ul className="space-y-1">
                    {s.improvementTips.map((tip, ti) => (
                      <li key={ti} className="text-xs text-dark-400 flex items-start gap-2">
                        <span className="text-primary-500 mt-0.5">•</span>
                        <span>{tip}</span>
                      </li>
                    ))}
                  </ul>
                </div>

                {/* Alternatives */}
                <div className="pl-9">
                  <p className="text-xs font-semibold text-accent-400 flex items-center gap-1.5 mb-1.5">
                    <RefreshCw size={14} /> Alternative Exercises
                  </p>
                  <div className="space-y-2">
                    {s.alternatives.map((alt, ai) => (
                      <div key={ai} className="bg-dark-800/60 rounded-xl p-3">
                        <p className="text-sm font-medium text-dark-200">{alt.name}</p>
                        <p className="text-xs text-dark-500 mt-0.5">{alt.reason}</p>
                      </div>
                    ))}
                  </div>
                </div>

                {idx < suggestions.length - 1 && <hr className="border-dark-700/50" />}
              </div>
            ))}
          </div>
        ) : null}
      </Modal>
    </div>
  );
}
