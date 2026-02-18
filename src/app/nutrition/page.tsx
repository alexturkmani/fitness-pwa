'use client';
import { useState, useCallback, useRef } from 'react';
import { useFoodLog } from '@/hooks/useFoodLog';
import { useUserProfile } from '@/hooks/useUserProfile';
import { FoodLogEntry } from '@/types';
import { generateId, formatDate, getDayName, calculateMacroTargets } from '@/lib/utils';
import Card from '@/components/ui/Card';
import Button from '@/components/ui/Button';
import Modal from '@/components/ui/Modal';
import Toast from '@/components/ui/Toast';
import { Apple, Plus, Trash2, ScanLine, Flame, Beef, Wheat, Droplets, Loader2 } from 'lucide-react';
import { useRouter } from 'next/navigation';

export default function NutritionPage() {
  const router = useRouter();
  const { profile } = useUserProfile();
  const { addEntry, removeEntry, getDayEntries, getDayTotals, getWeekEntries } = useFoodLog();
  const [selectedDate, setSelectedDate] = useState(formatDate(new Date()));
  const [showAddModal, setShowAddModal] = useState(false);
  const [toast, setToast] = useState<string | null>(null);
  const [newFood, setNewFood] = useState({
    name: '', servingSize: '', calories: '', protein: '', carbs: '', fats: '',
  });
  const [lookupLoading, setLookupLoading] = useState(false);
  const [autoFilled, setAutoFilled] = useState(false);
  const debounceRef = useRef<NodeJS.Timeout | null>(null);

  const fetchNutrition = useCallback(async (foodName: string, servingSize: string) => {
    if (!foodName || foodName.length < 2) return;
    setLookupLoading(true);
    try {
      const res = await fetch('/api/ai/food-lookup', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ foodName, servingSize: servingSize || 'standard serving' }),
      });
      if (res.ok) {
        const data = await res.json();
        setNewFood((prev) => ({
          ...prev,
          calories: String(data.calories || ''),
          protein: String(data.protein || ''),
          carbs: String(data.carbs || ''),
          fats: String(data.fats || ''),
        }));
        setAutoFilled(true);
      }
    } catch {
      // silently fail - user can still enter manually
    } finally {
      setLookupLoading(false);
    }
  }, []);

  const handleFoodFieldChange = (field: 'name' | 'servingSize', value: string) => {
    const updated = { ...newFood, [field]: value };
    setNewFood(updated);
    setAutoFilled(false);

    // Debounce the lookup - trigger when food name is entered and serving size changes, or vice versa
    if (debounceRef.current) clearTimeout(debounceRef.current);
    const foodName = field === 'name' ? value : updated.name;
    const servingSize = field === 'servingSize' ? value : updated.servingSize;
    if (foodName.trim().length >= 2) {
      debounceRef.current = setTimeout(() => {
        fetchNutrition(foodName.trim(), servingSize.trim() || 'standard serving');
      }, 800);
    }
  };

  const targets = profile.onboardingCompleted ? calculateMacroTargets(profile) : { calories: 2000, protein: 150, carbs: 250, fats: 65 };
  const weekData = getWeekEntries();
  const dayEntries = getDayEntries(selectedDate);
  const dayTotals = getDayTotals(selectedDate);

  const handleAddFood = () => {
    if (!newFood.name || !newFood.calories) return;
    const entry: FoodLogEntry = {
      id: generateId(),
      date: selectedDate,
      foodName: newFood.name,
      servingSize: newFood.servingSize || '1 serving',
      quantity: 1,
      macros: {
        calories: parseInt(newFood.calories) || 0,
        protein: parseInt(newFood.protein) || 0,
        carbs: parseInt(newFood.carbs) || 0,
        fats: parseInt(newFood.fats) || 0,
      },
      source: 'manual',
      createdAt: new Date().toISOString(),
    };
    addEntry(entry);
    setNewFood({ name: '', servingSize: '', calories: '', protein: '', carbs: '', fats: '' });
    setShowAddModal(false);
    setAutoFilled(false);
    setToast(`${entry.foodName} added`);
  };

  const handleRemove = (id: string) => {
    removeEntry(id);
    setToast('Entry removed');
  };

  const macroProgress = [
    { label: 'Calories', current: Math.round(dayTotals.calories), target: targets.calories, color: '#10b981', icon: Flame },
    { label: 'Protein', current: Math.round(dayTotals.protein), target: targets.protein, unit: 'g', color: '#10b981', icon: Beef },
    { label: 'Carbs', current: Math.round(dayTotals.carbs), target: targets.carbs, unit: 'g', color: '#06b6d4', icon: Wheat },
    { label: 'Fats', current: Math.round(dayTotals.fats), target: targets.fats, unit: 'g', color: '#f59e0b', icon: Droplets },
  ];

  return (
    <div className="py-6 space-y-6">
      {toast && <Toast message={toast} onClose={() => setToast(null)} />}

      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold text-dark-100">Nutrition</h1>
          <p className="text-dark-400 mt-1">Track your daily intake</p>
        </div>
        <Button size="sm" onClick={() => router.push('/scanner')}>
          <ScanLine size={16} /> Scan
        </Button>
      </div>

      {/* 7-Day Calendar */}
      <div className="flex gap-2 overflow-x-auto pb-2">
        {weekData.map((day) => {
          const date = new Date(day.date + 'T00:00:00');
          const isSelected = day.date === selectedDate;
          const isToday = day.date === formatDate(new Date());
          const calPercent = targets.calories > 0 ? Math.min(100, (day.totals.calories / targets.calories) * 100) : 0;

          return (
            <button
              key={day.date}
              onClick={() => setSelectedDate(day.date)}
              className={`calendar-cell flex flex-col items-center min-w-[52px] p-2 rounded-xl border ${
                isSelected ? 'active border-primary-500' : 'border-dark-700/50'
              }`}
            >
              <span className={`text-xs ${isToday ? 'text-primary-400 font-bold' : 'text-dark-500'}`}>
                {getDayName(date)}
              </span>
              <span className={`text-sm font-semibold mt-0.5 ${isSelected ? 'text-dark-100' : 'text-dark-300'}`}>
                {date.getDate()}
              </span>
              {/* Mini calorie ring */}
              <svg width="24" height="24" className="mt-1">
                <circle cx="12" cy="12" r="10" fill="none" stroke="#1e293b" strokeWidth="2" />
                <circle
                  cx="12" cy="12" r="10" fill="none"
                  stroke={calPercent > 100 ? '#ef4444' : calPercent > 0 ? '#10b981' : '#334155'}
                  strokeWidth="2" strokeLinecap="round"
                  strokeDasharray={`${(calPercent / 100) * 62.8} 62.8`}
                  transform="rotate(-90 12 12)"
                />
              </svg>
            </button>
          );
        })}
      </div>

      {/* Macro Progress Bars */}
      <Card>
        <div className="space-y-3">
          {macroProgress.map((macro) => {
            const percent = macro.target > 0 ? Math.min(100, (macro.current / macro.target) * 100) : 0;
            const Icon = macro.icon;
            return (
              <div key={macro.label}>
                <div className="flex items-center justify-between mb-1">
                  <div className="flex items-center gap-2">
                    <Icon size={14} style={{ color: macro.color }} />
                    <span className="text-sm text-dark-300">{macro.label}</span>
                  </div>
                  <span className="text-sm text-dark-200">
                    {macro.current}{macro.unit || ''} / {macro.target}{macro.unit || ''}
                  </span>
                </div>
                <div className="macro-bar">
                  <div
                    className="macro-bar-fill"
                    style={{ width: `${percent}%`, backgroundColor: percent > 100 ? '#ef4444' : macro.color }}
                  />
                </div>
              </div>
            );
          })}
        </div>
      </Card>

      {/* Food Log Entries */}
      <div>
        <div className="flex items-center justify-between mb-3">
          <h2 className="text-lg font-semibold text-dark-200">Food Log</h2>
          <span className="text-sm text-dark-500">{dayEntries.length} entries</span>
        </div>

        {dayEntries.length === 0 ? (
          <Card className="text-center py-6">
            <Apple className="mx-auto text-dark-600 mb-2" size={32} />
            <p className="text-dark-500 text-sm">No entries for this day</p>
          </Card>
        ) : (
          <div className="space-y-2">
            {dayEntries.map((entry) => (
              <Card key={entry.id} className="!p-3">
                <div className="flex items-center justify-between">
                  <div className="flex-1">
                    <div className="flex items-center gap-2">
                      <p className="text-sm font-medium text-dark-200">{entry.foodName}</p>
                      <span className={`badge ${entry.source === 'scanner' ? 'badge-blue' : entry.source === 'meal_plan' ? 'badge-green' : 'badge-yellow'}`}>
                        {entry.source === 'scanner' ? 'Scanned' : entry.source === 'meal_plan' ? 'Meal Plan' : 'Manual'}
                      </span>
                    </div>
                    <p className="text-xs text-dark-500 mt-0.5">
                      {entry.macros.calories} cal | P:{entry.macros.protein}g C:{entry.macros.carbs}g F:{entry.macros.fats}g
                    </p>
                  </div>
                  <button onClick={() => handleRemove(entry.id)} className="p-1.5 text-dark-600 hover:text-red-400 transition-colors">
                    <Trash2 size={16} />
                  </button>
                </div>
              </Card>
            ))}
          </div>
        )}
      </div>

      {/* Floating Add Button */}
      <button
        onClick={() => setShowAddModal(true)}
        className="fixed bottom-24 right-4 w-14 h-14 bg-gradient-to-r from-primary-500 to-primary-600 rounded-2xl shadow-lg shadow-primary-500/30 flex items-center justify-center text-white z-30 hover:scale-105 transition-transform"
      >
        <Plus size={24} />
      </button>

      {/* Add Food Modal */}
      <Modal isOpen={showAddModal} onClose={() => setShowAddModal(false)} title="Add Food Entry">
        <div className="space-y-3">
          <div>
            <label className="block text-xs text-dark-400 mb-1">Food Name</label>
            <input className="input-field" placeholder="e.g., Chicken Breast" value={newFood.name} onChange={(e) => handleFoodFieldChange('name', e.target.value)} />
          </div>
          <div>
            <label className="block text-xs text-dark-400 mb-1">Serving Size / Weight</label>
            <input className="input-field" placeholder="e.g., 150g" value={newFood.servingSize} onChange={(e) => handleFoodFieldChange('servingSize', e.target.value)} />
          </div>
          {lookupLoading && (
            <div className="flex items-center gap-2 text-primary-400 text-xs py-1">
              <Loader2 size={14} className="animate-spin" />
              <span>Estimating nutrition info...</span>
            </div>
          )}
          {autoFilled && !lookupLoading && (
            <div className="text-xs text-primary-400 bg-primary-500/10 border border-primary-500/20 rounded-lg px-3 py-1.5">
              Nutrition auto-filled from AI estimate. You can adjust values if needed.
            </div>
          )}
          <div className="grid grid-cols-2 gap-3">
            <div>
              <label className="block text-xs text-dark-400 mb-1">Calories</label>
              <input type="number" className="input-field" placeholder="0" value={newFood.calories} onChange={(e) => setNewFood({ ...newFood, calories: e.target.value })} />
            </div>
            <div>
              <label className="block text-xs text-dark-400 mb-1">Protein (g)</label>
              <input type="number" className="input-field" placeholder="0" value={newFood.protein} onChange={(e) => setNewFood({ ...newFood, protein: e.target.value })} />
            </div>
            <div>
              <label className="block text-xs text-dark-400 mb-1">Carbs (g)</label>
              <input type="number" className="input-field" placeholder="0" value={newFood.carbs} onChange={(e) => setNewFood({ ...newFood, carbs: e.target.value })} />
            </div>
            <div>
              <label className="block text-xs text-dark-400 mb-1">Fats (g)</label>
              <input type="number" className="input-field" placeholder="0" value={newFood.fats} onChange={(e) => setNewFood({ ...newFood, fats: e.target.value })} />
            </div>
          </div>
          <Button onClick={handleAddFood} disabled={!newFood.name || !newFood.calories} className="w-full mt-2">
            Add Entry
          </Button>
        </div>
      </Modal>
    </div>
  );
}
