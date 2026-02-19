import { UserProfile, MacroNutrients, UnitSystem } from '@/types';

export function calculateTDEE(profile: UserProfile): number {
  const bmr = profile.gender === 'male'
    ? 10 * profile.weight + 6.25 * profile.height - 5 * profile.age + 5
    : 10 * profile.weight + 6.25 * profile.height - 5 * profile.age - 161;

  const multipliers: Record<string, number> = {
    sedentary: 1.2,
    lightly_active: 1.375,
    moderately_active: 1.55,
    very_active: 1.725,
    extremely_active: 1.9,
  };

  return Math.round(bmr * multipliers[profile.activityLevel]);
}

export function calculateMacroTargets(profile: UserProfile): MacroNutrients {
  const tdee = calculateTDEE(profile);
  // Use the primary (first) goal for macro calculation, blend if multiple
  const goals = profile.fitnessGoals || ['general_fitness'];
  
  const goalMacros: Record<string, { calAdjust: number; proteinRatio: number; fatRatio: number }> = {
    weight_loss: { calAdjust: -500, proteinRatio: 0.35, fatRatio: 0.25 },
    muscle_gain: { calAdjust: 300, proteinRatio: 0.30, fatRatio: 0.25 },
    strength: { calAdjust: 0, proteinRatio: 0.30, fatRatio: 0.30 },
    endurance: { calAdjust: 200, proteinRatio: 0.20, fatRatio: 0.25 },
    general_fitness: { calAdjust: 0, proteinRatio: 0.25, fatRatio: 0.30 },
  };

  // Average the macro ratios across all selected goals
  let totalCalAdjust = 0;
  let totalProteinRatio = 0;
  let totalFatRatio = 0;
  const count = goals.length || 1;

  for (const goal of goals) {
    const g = goalMacros[goal] || goalMacros.general_fitness;
    totalCalAdjust += g.calAdjust;
    totalProteinRatio += g.proteinRatio;
    totalFatRatio += g.fatRatio;
  }

  const calories = tdee + Math.round(totalCalAdjust / count);
  const proteinRatio = totalProteinRatio / count;
  const fatRatio = totalFatRatio / count;

  const protein = Math.round((calories * proteinRatio) / 4);
  const fats = Math.round((calories * fatRatio) / 9);
  const carbs = Math.round((calories - protein * 4 - fats * 9) / 4);

  return { calories: Math.round(calories), protein, carbs, fats };
}

export function formatDate(date: Date): string {
  return date.toISOString().split('T')[0];
}

export function getDayName(date: Date): string {
  return date.toLocaleDateString('en-US', { weekday: 'short' });
}

export function getProteinCalorieRatio(macros: MacroNutrients): number {
  if (macros.calories === 0) return 0;
  return (macros.protein / macros.calories) * 100;
}

export function getRatioRating(ratio: number): { label: string; color: string } {
  if (ratio >= 10) return { label: 'Excellent', color: '#10b981' };
  if (ratio >= 5) return { label: 'Moderate', color: '#f59e0b' };
  return { label: 'Poor', color: '#ef4444' };
}

export function generateId(): string {
  return Math.random().toString(36).substring(2, 15) + Math.random().toString(36).substring(2, 15);
}

export function cn(...classes: (string | undefined | false)[]): string {
  return classes.filter(Boolean).join(' ');
}

// ─── Unit Conversion Utilities ───

export function kgToLbs(kg: number): number {
  return Math.round(kg * 2.20462 * 10) / 10;
}

export function lbsToKg(lbs: number): number {
  return Math.round(lbs / 2.20462 * 10) / 10;
}

export function cmToFeetInches(cm: number): { feet: number; inches: number } {
  const totalInches = cm / 2.54;
  const feet = Math.floor(totalInches / 12);
  const inches = Math.round(totalInches % 12);
  return { feet, inches };
}

export function feetInchesToCm(feet: number, inches: number): number {
  return Math.round((feet * 12 + inches) * 2.54 * 10) / 10;
}

export function mlToOz(ml: number): number {
  return Math.round(ml / 29.5735 * 10) / 10;
}

export function ozToMl(oz: number): number {
  return Math.round(oz * 29.5735);
}

export function formatWeight(kg: number, unit: UnitSystem): string {
  return unit === 'imperial' ? `${kgToLbs(kg)} lbs` : `${kg} kg`;
}

export function formatHeight(cm: number, unit: UnitSystem): string {
  if (unit === 'imperial') {
    const { feet, inches } = cmToFeetInches(cm);
    return `${feet}'${inches}"`;
  }
  return `${cm} cm`;
}

export function formatWater(ml: number, unit: UnitSystem): string {
  return unit === 'imperial' ? `${mlToOz(ml)} oz` : `${ml} ml`;
}

// ─── Water Intake Recommendation ───

export function calculateDailyWaterIntake(profile: UserProfile): number {
  // Base: 30-35ml per kg of body weight
  let waterMl = profile.weight * 33;

  // Adjust for activity level
  const activityMultipliers: Record<string, number> = {
    sedentary: 1.0,
    lightly_active: 1.1,
    moderately_active: 1.2,
    very_active: 1.35,
    extremely_active: 1.5,
  };
  waterMl *= activityMultipliers[profile.activityLevel] || 1.2;

  // Adjust for goals
  if (profile.fitnessGoals?.includes('weight_loss')) {
    waterMl *= 1.1; // extra hydration helps with weight loss
  }
  if (profile.fitnessGoals?.includes('muscle_gain')) {
    waterMl *= 1.05; // muscle tissue needs more water
  }

  return Math.round(waterMl / 100) * 100; // round to nearest 100ml
}

// ─── Cardio Calorie Estimation ───

export function estimateCardioCalories(type: string, durationMinutes: number, weightKg: number): number {
  // MET values for common cardio activities
  const metValues: Record<string, number> = {
    'Running': 9.8,
    'Walking': 3.8,
    'Cycling': 7.5,
    'Swimming': 8.0,
    'Rowing': 7.0,
    'Jump Rope': 12.3,
    'Elliptical': 5.0,
    'Stair Climbing': 9.0,
    'HIIT': 10.0,
    'Dancing': 5.5,
    'Hiking': 6.0,
    'Other': 5.0,
  };

  const met = metValues[type] || 5.0;
  // Calories = MET * weight(kg) * duration(hours)
  return Math.round(met * weightKg * (durationMinutes / 60));
}
