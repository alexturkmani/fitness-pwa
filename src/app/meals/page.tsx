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
import Modal from '@/components/ui/Modal';
import Toast from '@/components/ui/Toast';
import { UtensilsCrossed, RefreshCw, Plus, Flame, Beef, Wheat, Droplets, AlertTriangle, X, Trash2, MessageCircle, Sparkles, Send } from 'lucide-react';
import { PieChart, Pie, Cell, ResponsiveContainer } from 'recharts';

const MACRO_COLORS = ['#10b981', '#06b6d4', '#f59e0b'];

const COMMON_ALLERGIES = [
  'Dairy', 'Gluten', 'Nuts', 'Peanuts', 'Eggs', 'Soy', 'Shellfish', 'Fish', 'Wheat', 'Sesame',
];

export default function MealsPage() {
  const { profile } = useUserProfile();
  const { currentPlan, savePlan, deletePlan } = useMealPlan();
  const { addEntry } = useFoodLog();
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [toast, setToast] = useState<string | null>(null);
  const [showAllergyModal, setShowAllergyModal] = useState(false);
  const [allergies, setAllergies] = useState<string[]>([]);
  const [customAllergy, setCustomAllergy] = useState('');
  const [showDeleteConfirm, setShowDeleteConfirm] = useState(false);
  const [showSubModal, setShowSubModal] = useState(false);
  const [subLoading, setSubLoading] = useState(false);
  const [selectedFood, setSelectedFood] = useState<{ mealName: string; foodName: string; macros: any } | null>(null);
  const [subReason, setSubReason] = useState('');
  const [subResults, setSubResults] = useState<any[] | null>(null);

  const targets = profile.onboardingCompleted ? calculateMacroTargets(profile) : null;

  const toggleAllergy = (allergy: string) => {
    setAllergies((prev) =>
      prev.includes(allergy) ? prev.filter((a) => a !== allergy) : [...prev, allergy]
    );
  };

  const addCustomAllergy = () => {
    const trimmed = customAllergy.trim();
    if (trimmed && !allergies.includes(trimmed)) {
      setAllergies((prev) => [...prev, trimmed]);
    }
    setCustomAllergy('');
  };

  const promptAllergySelection = () => {
    setShowAllergyModal(true);
  };

  const handleGenerate = async (selectedAllergies?: string[]) => {
    setShowAllergyModal(false);
    setLoading(true);
    setError(null);
    try {
      const res = await fetch('/api/ai/meal', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ profile, allergies: selectedAllergies || allergies }),
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

  const openSubstitution = (mealName: string, foodName: string, macros: any) => {
    setSelectedFood({ mealName, foodName, macros });
    setSubReason('');
    setSubResults(null);
    setShowSubModal(true);
  };

  const handleSubstitute = async () => {
    if (!selectedFood) return;
    setSubLoading(true);
    try {
      const res = await fetch('/api/ai/meal-substitute', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          mealName: selectedFood.mealName,
          foodName: selectedFood.foodName,
          reason: subReason,
          currentMacros: selectedFood.macros,
        }),
      });
      if (res.ok) {
        const data = await res.json();
        setSubResults(data.substitutions || []);
      }
    } catch (err) {
      console.error('Substitution error:', err);
    } finally {
      setSubLoading(false);
    }
  };

  const handleDeletePlan = () => {
    deletePlan();
    setShowDeleteConfirm(false);
    setToast('Meal plan deleted');
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
          onAction={promptAllergySelection}
        />
        {error && (
          <Card className="border-red-500/30 bg-red-500/5 mt-4">
            <p className="text-sm text-red-400">{error}</p>
          </Card>
        )}

        {/* Allergy Selection Modal */}
        <Modal isOpen={showAllergyModal} onClose={() => setShowAllergyModal(false)} title="Any Allergies?">
          <p className="text-sm text-dark-400 mb-4">Select any food allergies or intolerances so the AI can avoid them in your meal plan.</p>
          <div className="flex flex-wrap gap-2 mb-4">
            {COMMON_ALLERGIES.map((allergy) => (
              <button
                key={allergy}
                onClick={() => toggleAllergy(allergy)}
                className={`px-3 py-1.5 rounded-lg text-sm font-medium transition-all border ${
                  allergies.includes(allergy)
                    ? 'border-primary-500 bg-primary-500/20 text-primary-400'
                    : 'border-dark-700 bg-dark-800/60 text-dark-400 hover:border-dark-600'
                }`}
              >
                {allergy}
              </button>
            ))}
          </div>
          {allergies.filter((a) => !COMMON_ALLERGIES.includes(a)).length > 0 && (
            <div className="flex flex-wrap gap-2 mb-3">
              {allergies.filter((a) => !COMMON_ALLERGIES.includes(a)).map((a) => (
                <span key={a} className="flex items-center gap-1 px-2 py-1 rounded-lg text-xs bg-primary-500/20 text-primary-400 border border-primary-500/30">
                  {a}
                  <button onClick={() => toggleAllergy(a)}><X size={12} /></button>
                </span>
              ))}
            </div>
          )}
          <div className="flex gap-2 mb-4">
            <input
              className="input-field flex-1"
              placeholder="Add custom allergy..."
              value={customAllergy}
              onChange={(e) => setCustomAllergy(e.target.value)}
              onKeyDown={(e) => e.key === 'Enter' && addCustomAllergy()}
            />
            <Button variant="secondary" size="sm" onClick={addCustomAllergy} disabled={!customAllergy.trim()}>
              Add
            </Button>
          </div>
          {allergies.length > 0 && (
            <div className="flex items-start gap-2 mb-4 p-2 rounded-lg bg-yellow-500/10 border border-yellow-500/20">
              <AlertTriangle className="text-yellow-400 flex-shrink-0 mt-0.5" size={14} />
              <p className="text-xs text-yellow-400">The meal plan will exclude: {allergies.join(', ')}</p>
            </div>
          )}
          <Button className="w-full" onClick={() => handleGenerate(allergies)}>
            Generate Meal Plan
          </Button>
          {allergies.length === 0 && (
            <p className="text-xs text-dark-500 text-center mt-2">No allergies? Just tap Generate to proceed.</p>
          )}
        </Modal>
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
        <div className="flex items-center gap-2">
          <button
            onClick={() => setShowDeleteConfirm(true)}
            className="p-2 rounded-xl text-dark-400 hover:text-red-400 bg-dark-800/60 hover:bg-red-500/10 border border-dark-700 hover:border-red-500/50 transition-all"
            title="Delete Meal Plan"
          >
            <Trash2 size={18} />
          </button>
          <Button variant="secondary" size="sm" onClick={promptAllergySelection} loading={loading}>
            <RefreshCw size={16} /> New Plan
          </Button>
        </div>
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
                  <div className="flex-1 min-w-0">
                    <p className="text-sm text-dark-200">{food.name}</p>
                    <p className="text-xs text-dark-500">{food.servingSize}</p>
                  </div>
                  <div className="flex items-center gap-2">
                    <div className="text-right">
                      <p className="text-xs text-dark-400">{food.macros.calories} cal</p>
                      <p className="text-xs text-dark-500">P:{food.macros.protein}g C:{food.macros.carbs}g F:{food.macros.fats}g</p>
                    </div>
                    <button
                      onClick={() => openSubstitution(meal.name, food.name, food.macros)}
                      className="p-1.5 rounded-lg text-dark-500 hover:text-accent-400 hover:bg-accent-500/10 transition-all flex-shrink-0"
                      title="Find substitutions"
                    >
                      <MessageCircle size={14} />
                    </button>
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

      {/* Allergy Selection Modal */}
      <Modal isOpen={showAllergyModal} onClose={() => setShowAllergyModal(false)} title="Any Allergies?">
        <p className="text-sm text-dark-400 mb-4">Select any food allergies or intolerances so the AI can avoid them in your meal plan.</p>
        <div className="flex flex-wrap gap-2 mb-4">
          {COMMON_ALLERGIES.map((allergy) => (
            <button
              key={allergy}
              onClick={() => toggleAllergy(allergy)}
              className={`px-3 py-1.5 rounded-lg text-sm font-medium transition-all border ${
                allergies.includes(allergy)
                  ? 'border-primary-500 bg-primary-500/20 text-primary-400'
                  : 'border-dark-700 bg-dark-800/60 text-dark-400 hover:border-dark-600'
              }`}
            >
              {allergy}
            </button>
          ))}
        </div>
        {/* Custom allergy tags */}
        {allergies.filter((a) => !COMMON_ALLERGIES.includes(a)).length > 0 && (
          <div className="flex flex-wrap gap-2 mb-3">
            {allergies.filter((a) => !COMMON_ALLERGIES.includes(a)).map((a) => (
              <span key={a} className="flex items-center gap-1 px-2 py-1 rounded-lg text-xs bg-primary-500/20 text-primary-400 border border-primary-500/30">
                {a}
                <button onClick={() => toggleAllergy(a)}><X size={12} /></button>
              </span>
            ))}
          </div>
        )}
        <div className="flex gap-2 mb-4">
          <input
            className="input-field flex-1"
            placeholder="Add custom allergy..."
            value={customAllergy}
            onChange={(e) => setCustomAllergy(e.target.value)}
            onKeyDown={(e) => e.key === 'Enter' && addCustomAllergy()}
          />
          <Button variant="secondary" size="sm" onClick={addCustomAllergy} disabled={!customAllergy.trim()}>
            Add
          </Button>
        </div>
        {allergies.length > 0 && (
          <div className="flex items-start gap-2 mb-4 p-2 rounded-lg bg-yellow-500/10 border border-yellow-500/20">
            <AlertTriangle className="text-yellow-400 flex-shrink-0 mt-0.5" size={14} />
            <p className="text-xs text-yellow-400">The meal plan will exclude: {allergies.join(', ')}</p>
          </div>
        )}
        <Button className="w-full" onClick={() => handleGenerate(allergies)}>
          Generate Meal Plan
        </Button>
        {allergies.length === 0 && (
          <p className="text-xs text-dark-500 text-center mt-2">No allergies? Just tap Generate to proceed.</p>
        )}
      </Modal>

      {/* Substitution Modal */}
      <Modal isOpen={showSubModal} onClose={() => setShowSubModal(false)} title="Substitute Ingredient">
        {selectedFood && (
          <div className="space-y-4">
            <div className="p-3 rounded-xl bg-dark-800/60 border border-dark-700">
              <p className="text-sm font-medium text-dark-200">{selectedFood.foodName}</p>
              <p className="text-xs text-dark-500 mt-1">
                from {selectedFood.mealName} &bull; {selectedFood.macros.calories} cal &bull; P:{selectedFood.macros.protein}g C:{selectedFood.macros.carbs}g F:{selectedFood.macros.fats}g
              </p>
            </div>

            <div>
              <label className="text-xs text-dark-400 mb-1.5 block">Why do you want to substitute? (optional)</label>
              <div className="flex gap-2">
                <input
                  className="input-field flex-1"
                  placeholder="e.g. I don't like it, too expensive..."
                  value={subReason}
                  onChange={(e) => setSubReason(e.target.value)}
                  onKeyDown={(e) => e.key === 'Enter' && handleSubstitute()}
                />
                <button
                  onClick={handleSubstitute}
                  disabled={subLoading}
                  className="p-2.5 rounded-xl bg-primary-500 hover:bg-primary-600 text-white transition-all disabled:opacity-50"
                >
                  {subLoading ? <Sparkles size={18} className="animate-spin" /> : <Send size={18} />}
                </button>
              </div>
            </div>

            {subResults && subResults.length > 0 && (
              <div className="space-y-2">
                <p className="text-xs font-medium text-dark-400 uppercase tracking-wider">Suggestions</p>
                {subResults.map((sub: any, i: number) => (
                  <div key={i} className="p-3 rounded-xl bg-dark-800/40 border border-dark-700/50 space-y-1">
                    <div className="flex items-center justify-between">
                      <p className="text-sm font-medium text-dark-200">{sub.name}</p>
                      <span className="text-xs text-primary-400 font-medium">{sub.macros?.calories || '–'} cal</span>
                    </div>
                    <p className="text-xs text-dark-500">{sub.servingSize}</p>
                    {sub.macros && (
                      <p className="text-xs text-dark-500">P:{sub.macros.protein}g C:{sub.macros.carbs}g F:{sub.macros.fats}g</p>
                    )}
                    {sub.reason && <p className="text-xs text-dark-400 italic mt-1">{sub.reason}</p>}
                  </div>
                ))}
              </div>
            )}

            {subResults && subResults.length === 0 && (
              <p className="text-sm text-dark-500 text-center">No substitutions found. Try a different reason.</p>
            )}
          </div>
        )}
      </Modal>

      {/* Delete Confirmation Modal */}
      <Modal isOpen={showDeleteConfirm} onClose={() => setShowDeleteConfirm(false)} title="Delete Meal Plan?">
        <div className="space-y-4">
          <div className="flex items-start gap-3 p-3 rounded-xl bg-red-500/10 border border-red-500/20">
            <AlertTriangle className="text-red-400 flex-shrink-0 mt-0.5" size={18} />
            <p className="text-sm text-red-400">This will permanently delete your current meal plan. You can always generate a new one.</p>
          </div>
          <div className="flex gap-3">
            <Button variant="secondary" className="flex-1" onClick={() => setShowDeleteConfirm(false)}>Cancel</Button>
            <button
              onClick={handleDeletePlan}
              className="flex-1 px-4 py-2.5 rounded-xl bg-red-500 hover:bg-red-600 text-white font-medium transition-all"
            >
              Delete Plan
            </button>
          </div>
        </div>
      </Modal>
    </div>
  );
}
