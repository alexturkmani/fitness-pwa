'use client';
import { useState } from 'react';
import { useWorkoutLogs } from '@/hooks/useWorkoutLogs';
import { useFoodLog } from '@/hooks/useFoodLog';
import { useWeightLog } from '@/hooks/useWeightLog';
import { useUserProfile } from '@/hooks/useUserProfile';
import { useWaterLog } from '@/hooks/useWaterLog';
import { useCardioLog } from '@/hooks/useCardioLog';
import { formatDate, formatWeight, formatWater, calculateDailyWaterIntake, kgToLbs } from '@/lib/utils';
import Card from '@/components/ui/Card';
import Button from '@/components/ui/Button';
import Modal from '@/components/ui/Modal';
import { BarChart3, TrendingUp, TrendingDown, Scale, Flame, Dumbbell, Minus, GlassWater, Bike } from 'lucide-react';
import {
  LineChart, Line, BarChart, Bar, PieChart, Pie, Cell,
  XAxis, YAxis, Tooltip, ResponsiveContainer, CartesianGrid, Area, AreaChart,
} from 'recharts';

const CHART_COLORS = {
  primary: '#10b981',
  accent: '#06b6d4',
  yellow: '#f59e0b',
  protein: '#10b981',
  carbs: '#06b6d4',
  fats: '#f59e0b',
  bg: '#1e293b',
  border: '#334155',
  text: '#94a3b8',
};

const CustomTooltip = ({ active, payload, label }: any) => {
  if (!active || !payload?.length) return null;
  return (
    <div className="glass px-3 py-2 text-xs">
      <p className="text-dark-400 mb-1">{label}</p>
      {payload.map((p: any, i: number) => (
        <p key={i} style={{ color: p.color }} className="font-medium">
          {p.name}: {typeof p.value === 'number' ? p.value.toLocaleString() : p.value}
        </p>
      ))}
    </div>
  );
};

export default function ProgressPage() {
  const { profile } = useUserProfile();
  const { logs } = useWorkoutLogs();
  const { getWeekEntries } = useFoodLog();
  const { entries: weightEntries, addEntry: addWeightEntry, getLatestWeight, getWeightChange } = useWeightLog();
  const { getWeekData: getWaterWeekData, getTodayTotal: getTodayWater } = useWaterLog();
  const { getWeekData: getCardioWeekData, getTodayCaloriesBurnt } = useCardioLog();
  const [showWeightModal, setShowWeightModal] = useState(false);
  const [newWeight, setNewWeight] = useState('');
  const isImperial = profile.unitSystem === 'imperial';

  const handleAddWeight = () => {
    if (!newWeight) return;
    const weightKg = isImperial ? parseFloat(newWeight) / 2.205 : parseFloat(newWeight);
    addWeightEntry(weightKg);
    setNewWeight('');
    setShowWeightModal(false);
  };

  // Weight chart data
  const weightData = weightEntries.slice(-30).map((e) => ({
    date: new Date(e.date).toLocaleDateString('en-US', { month: 'short', day: 'numeric' }),
    weight: isImperial ? Math.round(kgToLbs(e.weight) * 10) / 10 : e.weight,
  }));

  // Water intake weekly data
  const waterWeekData = getWaterWeekData().map((d) => ({
    date: new Date(d.date + 'T00:00:00').toLocaleDateString('en-US', { weekday: 'short' }),
    amount: isImperial ? Math.round(d.total * 0.0338) : Math.round(d.total / 1000 * 10) / 10,
  }));
  const waterTarget = calculateDailyWaterIntake(profile);

  // Cardio weekly data
  const cardioWeekData = getCardioWeekData().map((d) => ({
    date: new Date(d.date + 'T00:00:00').toLocaleDateString('en-US', { weekday: 'short' }),
    calories: d.totalCalories,
    sessions: d.entries.length,
  }));

  // Workout volume per week (last 8 weeks)
  const volumeData: { week: string; volume: number; workouts: number }[] = [];
  for (let i = 7; i >= 0; i--) {
    const weekStart = new Date();
    weekStart.setDate(weekStart.getDate() - (i * 7 + weekStart.getDay()));
    weekStart.setHours(0, 0, 0, 0);
    const weekEnd = new Date(weekStart);
    weekEnd.setDate(weekEnd.getDate() + 7);

    const weekLogs = logs.filter((l) => {
      const d = new Date(l.date);
      return d >= weekStart && d < weekEnd;
    });

    const volume = weekLogs.reduce((total, log) =>
      total + log.exercises.reduce((exT, ex) =>
        exT + ex.sets.reduce((sT, s) => sT + (s.completed ? s.weight * s.reps : 0), 0), 0), 0);

    volumeData.push({
      week: `W${8 - i}`,
      volume: Math.round(volume),
      workouts: weekLogs.length,
    });
  }

  // Calorie data from food log (last 7 days)
  const weekNutrition = getWeekEntries();
  const calorieData = weekNutrition.map((day) => ({
    date: new Date(day.date + 'T00:00:00').toLocaleDateString('en-US', { weekday: 'short' }),
    calories: Math.round(day.totals.calories),
    protein: Math.round(day.totals.protein),
  }));

  // Macro breakdown (average of last 7 days)
  const avgMacros = weekNutrition.reduce(
    (acc, day) => ({
      protein: acc.protein + day.totals.protein,
      carbs: acc.carbs + day.totals.carbs,
      fats: acc.fats + day.totals.fats,
    }),
    { protein: 0, carbs: 0, fats: 0 }
  );
  const macroData = [
    { name: 'Protein', value: Math.round(avgMacros.protein / 7) || 0 },
    { name: 'Carbs', value: Math.round(avgMacros.carbs / 7) || 0 },
    { name: 'Fats', value: Math.round(avgMacros.fats / 7) || 0 },
  ];
  const MACRO_PIE_COLORS = [CHART_COLORS.protein, CHART_COLORS.carbs, CHART_COLORS.fats];

  // Weekly stats
  const thisWeekLogs = logs.filter((l) => {
    const d = new Date(l.date);
    const now = new Date();
    const weekStart = new Date(now);
    weekStart.setDate(now.getDate() - (now.getDay() === 0 ? 6 : now.getDay() - 1));
    weekStart.setHours(0, 0, 0, 0);
    return d >= weekStart;
  });

  const todayNutrition = weekNutrition[weekNutrition.length - 1];
  const weightChange = getWeightChange();

  return (
    <div className="py-6 space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold text-dark-100">Progress</h1>
          <p className="text-dark-400 mt-1">Weekly performance report</p>
        </div>
        <Button size="sm" variant="secondary" onClick={() => setShowWeightModal(true)}>
          <Scale size={16} /> Log Weight
        </Button>
      </div>

      {/* Summary Stats */}
      <div className="grid grid-cols-2 gap-3">
        <div className="stat-card">
          <div className="flex items-center gap-2 mb-1">
            <Dumbbell className="text-primary-400" size={16} />
            <span className="text-xs text-dark-500">Workouts This Week</span>
          </div>
          <p className="text-2xl font-bold text-dark-100">{thisWeekLogs.length}</p>
        </div>
        <div className="stat-card">
          <div className="flex items-center gap-2 mb-1">
            <Flame className="text-primary-400" size={16} />
            <span className="text-xs text-dark-500">Avg Daily Calories</span>
          </div>
          <p className="text-2xl font-bold text-dark-100">
            {Math.round(weekNutrition.reduce((sum, d) => sum + d.totals.calories, 0) / 7) || 0}
          </p>
        </div>
        <div className="stat-card">
          <div className="flex items-center gap-2 mb-1">
            <Scale className="text-accent-400" size={16} />
            <span className="text-xs text-dark-500">Current Weight</span>
          </div>
          <p className="text-2xl font-bold text-dark-100">{formatWeight(getLatestWeight() || profile.weight || 0, profile.unitSystem)}</p>
        </div>
        <div className="stat-card">
          <div className="flex items-center gap-2 mb-1">
            {weightChange < 0 ? <TrendingDown className="text-primary-400" size={16} /> : weightChange > 0 ? <TrendingUp className="text-yellow-400" size={16} /> : <Minus className="text-dark-500" size={16} />}
            <span className="text-xs text-dark-500">Weight Change</span>
          </div>
          <p className={`text-2xl font-bold ${weightChange < 0 ? 'text-primary-400' : weightChange > 0 ? 'text-yellow-400' : 'text-dark-400'}`}>
            {weightChange > 0 ? '+' : ''}{weightChange ? (isImperial ? (weightChange * 2.205).toFixed(1) : weightChange.toFixed(1)) : '0'} {isImperial ? 'lbs' : 'kg'}
          </p>
        </div>
        <div className="stat-card">
          <div className="flex items-center gap-2 mb-1">
            <GlassWater className="text-blue-400" size={16} />
            <span className="text-xs text-dark-500">Today&apos;s Water</span>
          </div>
          <p className="text-2xl font-bold text-dark-100">{formatWater(getTodayWater(), profile.unitSystem)}</p>
          <p className="text-xs text-dark-500">{Math.round((getTodayWater() / waterTarget) * 100)}% of target</p>
        </div>
        <div className="stat-card">
          <div className="flex items-center gap-2 mb-1">
            <Bike className="text-orange-400" size={16} />
            <span className="text-xs text-dark-500">Cardio Burnt Today</span>
          </div>
          <p className="text-2xl font-bold text-dark-100">{getTodayCaloriesBurnt()}</p>
          <p className="text-xs text-dark-500">kcal</p>
        </div>
      </div>

      {/* Weight Chart */}
      <Card>
        <h3 className="font-semibold text-dark-200 mb-4">Weight Trend</h3>
        {weightData.length > 0 ? (
          <ResponsiveContainer width="100%" height={200}>
            <AreaChart data={weightData}>
              <defs>
                <linearGradient id="weightGrad" x1="0" y1="0" x2="0" y2="1">
                  <stop offset="5%" stopColor={CHART_COLORS.primary} stopOpacity={0.3} />
                  <stop offset="95%" stopColor={CHART_COLORS.primary} stopOpacity={0} />
                </linearGradient>
              </defs>
              <CartesianGrid strokeDasharray="3 3" stroke={CHART_COLORS.border} />
              <XAxis dataKey="date" tick={{ fill: CHART_COLORS.text, fontSize: 11 }} />
              <YAxis domain={['auto', 'auto']} tick={{ fill: CHART_COLORS.text, fontSize: 11 }} />
              <Tooltip content={<CustomTooltip />} />
              <Area type="monotone" dataKey="weight" stroke={CHART_COLORS.primary} fill="url(#weightGrad)" strokeWidth={2} name={`Weight (${isImperial ? 'lbs' : 'kg'})`} />
              {profile.targetWeight > 0 && (
                <Line type="monotone" dataKey={() => isImperial ? kgToLbs(profile.targetWeight) : profile.targetWeight} stroke={CHART_COLORS.accent} strokeDasharray="5 5" strokeWidth={1} name="Target" dot={false} />
              )}
            </AreaChart>
          </ResponsiveContainer>
        ) : (
          <p className="text-dark-500 text-sm text-center py-8">Log your weight to see trends</p>
        )}
      </Card>

      {/* Water Intake Chart */}
      <Card>
        <h3 className="font-semibold text-dark-200 mb-4">Daily Water Intake</h3>
        <ResponsiveContainer width="100%" height={200}>
          <BarChart data={waterWeekData}>
            <CartesianGrid strokeDasharray="3 3" stroke={CHART_COLORS.border} />
            <XAxis dataKey="date" tick={{ fill: CHART_COLORS.text, fontSize: 11 }} />
            <YAxis tick={{ fill: CHART_COLORS.text, fontSize: 11 }} />
            <Tooltip content={<CustomTooltip />} />
            <Bar dataKey="amount" fill="#3b82f6" radius={[4, 4, 0, 0]} name={isImperial ? 'Water (oz)' : 'Water (L)'} />
          </BarChart>
        </ResponsiveContainer>
      </Card>

      {/* Cardio Calories Chart */}
      <Card>
        <h3 className="font-semibold text-dark-200 mb-4">Cardio Calories Burnt</h3>
        <ResponsiveContainer width="100%" height={200}>
          <BarChart data={cardioWeekData}>
            <CartesianGrid strokeDasharray="3 3" stroke={CHART_COLORS.border} />
            <XAxis dataKey="date" tick={{ fill: CHART_COLORS.text, fontSize: 11 }} />
            <YAxis tick={{ fill: CHART_COLORS.text, fontSize: 11 }} />
            <Tooltip content={<CustomTooltip />} />
            <Bar dataKey="calories" fill="#f97316" radius={[4, 4, 0, 0]} name="Calories Burnt" />
          </BarChart>
        </ResponsiveContainer>
      </Card>

      {/* Workout Volume Chart */}
      <Card>
        <h3 className="font-semibold text-dark-200 mb-4">Workout Volume (kg x reps)</h3>
        <ResponsiveContainer width="100%" height={200}>
          <BarChart data={volumeData}>
            <CartesianGrid strokeDasharray="3 3" stroke={CHART_COLORS.border} />
            <XAxis dataKey="week" tick={{ fill: CHART_COLORS.text, fontSize: 11 }} />
            <YAxis tick={{ fill: CHART_COLORS.text, fontSize: 11 }} />
            <Tooltip content={<CustomTooltip />} />
            <Bar dataKey="volume" fill={CHART_COLORS.primary} radius={[4, 4, 0, 0]} name="Volume" />
          </BarChart>
        </ResponsiveContainer>
      </Card>

      {/* Calorie Chart */}
      <Card>
        <h3 className="font-semibold text-dark-200 mb-4">Daily Calories & Protein</h3>
        <ResponsiveContainer width="100%" height={200}>
          <LineChart data={calorieData}>
            <CartesianGrid strokeDasharray="3 3" stroke={CHART_COLORS.border} />
            <XAxis dataKey="date" tick={{ fill: CHART_COLORS.text, fontSize: 11 }} />
            <YAxis tick={{ fill: CHART_COLORS.text, fontSize: 11 }} />
            <Tooltip content={<CustomTooltip />} />
            <Line type="monotone" dataKey="calories" stroke={CHART_COLORS.primary} strokeWidth={2} name="Calories" dot={{ fill: CHART_COLORS.primary, r: 3 }} />
            <Line type="monotone" dataKey="protein" stroke={CHART_COLORS.accent} strokeWidth={2} name="Protein (g)" dot={{ fill: CHART_COLORS.accent, r: 3 }} />
          </LineChart>
        </ResponsiveContainer>
      </Card>

      {/* Macro Breakdown Pie */}
      <Card>
        <h3 className="font-semibold text-dark-200 mb-4">Avg. Daily Macro Split</h3>
        <div className="flex items-center gap-4">
          <ResponsiveContainer width={120} height={120}>
            <PieChart>
              <Pie data={macroData} cx="50%" cy="50%" innerRadius={35} outerRadius={55} dataKey="value" strokeWidth={0}>
                {macroData.map((_, i) => (
                  <Cell key={i} fill={MACRO_PIE_COLORS[i]} />
                ))}
              </Pie>
            </PieChart>
          </ResponsiveContainer>
          <div className="flex-1 space-y-2">
            {macroData.map((m, i) => (
              <div key={m.name} className="flex items-center justify-between">
                <div className="flex items-center gap-2">
                  <div className="w-3 h-3 rounded-full" style={{ backgroundColor: MACRO_PIE_COLORS[i] }} />
                  <span className="text-sm text-dark-300">{m.name}</span>
                </div>
                <span className="text-sm font-medium text-dark-200">{m.value}g</span>
              </div>
            ))}
          </div>
        </div>
      </Card>

      {/* Weight Log Modal */}
      <Modal isOpen={showWeightModal} onClose={() => setShowWeightModal(false)} title="Log Weight">
        <div className="space-y-4">
          <div>
            <label className="block text-xs text-dark-400 mb-1">Weight ({isImperial ? 'lbs' : 'kg'})</label>
            <input
              type="number"
              step="0.1"
              className="input-field"
              placeholder={String(isImperial ? Math.round(kgToLbs(getLatestWeight() || profile.weight || 75)) : (getLatestWeight() || profile.weight || 75))}
              value={newWeight}
              onChange={(e) => setNewWeight(e.target.value)}
            />
          </div>
          <Button onClick={handleAddWeight} disabled={!newWeight} className="w-full">
            Save Weight
          </Button>
        </div>
      </Modal>
    </div>
  );
}
