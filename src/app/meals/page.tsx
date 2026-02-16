'use client';
import { useState } from 'react';
import { useUserProfile } from '@/hooks/useUserProfile';
import { useMealPlan } from '@/hooks/useMealPlan';
import { useFoodLog } from '@/hooks/useFoodLog';
import { FoodLogEntry, MacroNutrients } from '@/types';
import { generateId, formatDate, calculateMacroTargets } from '@/lib/utils';
import Card from '@/components/ui/Card';
import Button from '@/components/ui/Button';
import EmptyState from '@/components/ui/EmptyState';
import LoadingSpinner from '@/components/ui/LoadingSpinner';
import Toast from '@/components/ui/Toast';
import { UtensilsCrossed, RefreshCw, Plus, Flame, Beef, Wheat, Droplets } from 'lucide-react';
import { PieChart, Pie, Cell, ResponsiveContainer } from 'recharts';

const MACRO_COLORS = ['#10b981', '#06b6d4', '#f59e0b'];

export default function MealsPage() {
  const { profile } = useUserProfile();
  const { currentPlan, savePlan } = useMealPlan();
  const { addEntry } = useFoodLog();
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [toast, setToast] = useState<string | null>(null);

  const targets = profile.onboardingCompleted ? calculateMacroTargets(profile) : null;

  const handleGenerate = async () => {
    setLoading(true);
    setError(null);
    try {
      const res = await fetch('/api/ai/meal', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ profile }),
      });
      if (res.ok) {
        const plan = await res.json();
        savePlan(plan);
      } else {
        const errData = await res.json().catch(() => ({ error: 'Unknown error' }));
        setError(errData.error || 'Failed to generate meal plan. Please try again.');
      }
    } catch (err: any) {
      console.error('Failed to generate meal plan:', err);
      setError(err.message || 'Failed to generate meal plan. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  const addMealToLog = (mealName: string, macros: MacroNutrients) => {
    const entry: FoodLogEntry = {
      id: generateId(),
      date: formatDate(new Date()),
      foodName: mealName,
      servingSize: '1 serving',
      quantity: 1,
      macros,
      source: 'meal_plan',
      createdAt: new Date().toISOString(),
    };
    addEntry(entry);
    setToast(`${mealName} added to food log`);
  };

  if (loading) {
    return <LoadingSpinner message="AI is creating your meal plan..." />;
  }

  if (!currentPlan) {
    return (
      <div className="py-8">
        <EmptyState
          icon={<UtensilsCrossed size={48} />}
          title="No Meal Plan"
          description="Generate an AI-powered meal plan tailored to your goals and macro targets."
          actionLabel="Generate Meal Plan"
          onAction={handleGenerate}
        />
        {error && (
          <Card className="border-red-500/30 bg-red-500/5 mt-4">
            <p className="text-sm text-red-400">{error}</p>
          </Card>
        )}
      </div>
    );
  }

  const macroData = [
    { name: 'Protein', value: currentPlan.dailyTotals.protein, unit: 'g' },
    { name: 'Carbs', value: currentPlan.dailyTotals.carbs, unit: 'g' },
    { name: 'Fats', value: currentPlan.dailyTotals.fats, unit: 'g' },
  ];

  return (
    <div className="py-6 space-y-6">
      {toast && <Toast message={toast} onClose={() => setToast(null)} />}

      {/* Header */}
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold text-dark-100">Meal Plan</h1>
          <p className="text-dark-400 mt-1">AI-designed for your goals</p>
        </div>
        <Button variant="secondary" size="sm" onClick={handleGenerate} loading={loading}>
          <RefreshCw size={16} /> New Plan
        </Button>
      </div>

      {/* Macro Summary */}
      <Card>
        <div className="flex items-center gap-4">
          <div className="w-24 h-24">
            <ResponsiveContainer width="100%" height="100%">
              <PieChart>
                <Pie data={macroData} cx="50%" cy="50%" innerRadius={28} outerRadius={42} dataKey="value" strokeWidth={0}>
                  {macroData.map((_, i) => (
                    <Cell key={i} fill={MACRO_COLORS[i]} />
                  ))}
                </Pie>
              </PieChart>
            </ResponsiveContainer>
          </div>
          <div className="flex-1 space-y-2">
            <div className="flex items-center justify-between">
              <div className="flex items-center gap-2">
                <Flame className="text-primary-400" size={16} />
                <span className="text-sm text-dark-300">Calories</span>
              </div>
              <span className="font-bold text-dark-100">{currentPlan.dailyTotals.calories}</span>
            </div>
            <div className="flex items-center justify-between">
              <div className="flex items-center gap-2">
                <Beef className="text-primary-400" size={16} />
                <span className="text-sm text-dark-300">Protein</span>
              </div>
              <span className="font-semibold text-dark-200">{currentPlan.dailyTotals.protein}g</span>
            </div>
            <div className="flex items-center justify-between">
              <div className="flex items-center gap-2">
                <Wheat className="text-accent-400" size={16} />
                <span className="text-sm text-dark-300">Carbs</span>
              </div>
              <span className="font-semibold text-dark-200">{currentPlan.dailyTotals.carbs}g</span>
            </div>
            <div className="flex items-center justify-between">
              <div className="flex items-center gap-2">
                <Droplets className="text-yellow-400" size={16} />
                <span className="text-sm text-dark-300">Fats</span>
              </div>
              <span className="font-semibold text-dark-200">{currentPlan.dailyTotals.fats}g</span>
            </div>
          </div>
        </div>
      </Card>

      {/* AI Notes */}
      {currentPlan.aiNotes && (
        <Card className="bg-dark-800/30">
          <p className="text-sm text-dark-400 italic">{currentPlan.aiNotes}</p>
        </Card>
      )}

      {/* Meals */}
      <div className="space-y-4">
        {currentPlan.meals.map((meal) => (
          <Card key={meal.id}>
            <div className="flex items-center justify-between mb-3">
              <h3 className="font-semibold text-dark-100">{meal.name}</h3>
              <div className="flex items-center gap-2">
                <span className="text-sm text-primary-400 font-medium">{meal.totalMacros.calories} cal</span>
                <button
                  onClick={() => addMealToLog(meal.name, meal.totalMacros)}
                  className="p-1.5 bg-primary-500/20 rounded-lg hover:bg-primary-500/30 transition-colors"
                  title="Add to food log"
                >
                  <Plus className="text-primary-400" size={14} />
                </button>
              </div>
            </div>
            <div className="space-y-2">
              {meal.foods.map((food) => (
                <div key={food.id} className="flex items-center justify-between py-1.5 border-b border-dark-700/30 last:border-0">
                  <div>
                    <p className="text-sm text-dark-200">{food.name}</p>
                    <p className="text-xs text-dark-500">{food.servingSize}</p>
                  </div>
                  <div className="text-right">
                    <p className="text-xs text-dark-400">{food.macros.calories} cal</p>
                    <p className="text-xs text-dark-500">P:{food.macros.protein}g C:{food.macros.carbs}g F:{food.macros.fats}g</p>
                  </div>
                </div>
              ))}
            </div>
            <div className="mt-3 pt-2 border-t border-dark-700/30 flex justify-between text-xs text-dark-500">
              <span>P: {meal.totalMacros.protein}g</span>
              <span>C: {meal.totalMacros.carbs}g</span>
              <span>F: {meal.totalMacros.fats}g</span>
            </div>
          </Card>
        ))}
      </div>
    </div>
  );
}
