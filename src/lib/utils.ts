import { UserProfile, MacroNutrients } from '@/types';

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
  let calories: number;
  let proteinRatio: number;
  let fatRatio: number;

  switch (profile.fitnessGoal) {
    case 'weight_loss':
      calories = tdee - 500;
      proteinRatio = 0.35;
      fatRatio = 0.25;
      break;
    case 'muscle_gain':
      calories = tdee + 300;
      proteinRatio = 0.30;
      fatRatio = 0.25;
      break;
    case 'strength':
      calories = tdee;
      proteinRatio = 0.30;
      fatRatio = 0.30;
      break;
    case 'endurance':
      calories = tdee + 200;
      proteinRatio = 0.20;
      fatRatio = 0.25;
      break;
    default:
      calories = tdee;
      proteinRatio = 0.25;
      fatRatio = 0.30;
  }

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
