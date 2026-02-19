export type UnitSystem = 'metric' | 'imperial';
export type LiftingExperience = 'beginner' | 'intermediate' | 'advanced' | 'expert';
export type TrainingLocation = 'home' | 'gym';

export interface UserProfile {
  id: string;
  name: string;
  weight: number;
  height: number;
  age: number;
  gender: 'male' | 'female' | 'other';
  activityLevel: 'sedentary' | 'lightly_active' | 'moderately_active' | 'very_active' | 'extremely_active';
  fitnessGoals: ('weight_loss' | 'muscle_gain' | 'strength' | 'endurance' | 'general_fitness')[];
  targetWeight: number;
  intervalWeeks: 6 | 8;
  gymDaysPerWeek: number;
  liftingExperience: LiftingExperience;
  trainingLocation: TrainingLocation;
  unitSystem: UnitSystem;
  onboardingCompleted: boolean;
  createdAt: string;
  updatedAt: string;
}

export interface Exercise {
  id: string;
  name: string;
  muscleGroup: string;
  sets: number;
  reps: string;
  restSeconds: number;
  notes?: string;
}

export interface WorkoutDay {
  id: string;
  dayNumber: number;
  dayLabel: string;
  isRestDay: boolean;
  exercises: Exercise[];
}

export interface WorkoutPlan {
  id: string;
  intervalNumber: number;
  startDate: string;
  endDate: string;
  weeks: number;
  days: WorkoutDay[];
  aiNotes: string;
  assessmentSummary?: string;
  createdAt: string;
}

export interface SetLog {
  setNumber: number;
  weight: number;
  reps: number;
  completed: boolean;
}

export interface ExerciseLog {
  exerciseId: string;
  exerciseName: string;
  sets: SetLog[];
}

export interface WorkoutLog {
  id: string;
  date: string;
  planId: string;
  dayId: string;
  exercises: ExerciseLog[];
  duration?: number;
  notes?: string;
  createdAt: string;
}

export interface MacroNutrients {
  calories: number;
  protein: number;
  carbs: number;
  fats: number;
  fiber?: number;
}

export interface FoodItem {
  id: string;
  name: string;
  servingSize: string;
  macros: MacroNutrients;
}

export interface Meal {
  id: string;
  name: string;
  foods: FoodItem[];
  totalMacros: MacroNutrients;
}

export interface MealPlan {
  id: string;
  date: string;
  meals: Meal[];
  dailyTotals: MacroNutrients;
  dailyTargets: MacroNutrients;
  dailyWaterIntakeMl?: number;
  aiNotes: string;
  createdAt: string;
}

export interface ScannedProduct {
  barcode: string;
  name: string;
  brand?: string;
  servingSize: string;
  macros: MacroNutrients;
  proteinCalorieRatio: number;
  imageUrl?: string;
}

export interface FoodLogEntry {
  id: string;
  date: string;
  foodName: string;
  servingSize: string;
  quantity: number;
  macros: MacroNutrients;
  source: 'manual' | 'scanner' | 'meal_plan';
  barcode?: string;
  createdAt: string;
}

export interface DailyNutritionSummary {
  date: string;
  entries: FoodLogEntry[];
  totalMacros: MacroNutrients;
  targetMacros: MacroNutrients;
}

export interface WeightEntry {
  date: string;
  weight: number;
}

export interface ProgressSnapshot {
  id: string;
  weekStartDate: string;
  weekEndDate: string;
  averageDailyCalories: number;
  averageDailyProtein: number;
  totalWorkoutVolume: number;
  workoutsCompleted: number;
  weightEntries: WeightEntry[];
  createdAt: string;
}

export interface CustomExerciseLog {
  id: string;
  name: string;
  muscleGroup: string;
  sets: { weight: number; reps: number }[];
}

export interface CustomWorkoutLog {
  id: string;
  date: string;
  name: string;
  exercises: CustomExerciseLog[];
  duration?: number;
  notes?: string;
  createdAt: string;
}

export interface WaterLogEntry {
  id: string;
  date: string;
  amount: number; // in ml
  createdAt: string;
}

export interface CardioLogEntry {
  id: string;
  date: string;
  type: string; // e.g., 'running', 'cycling', 'swimming'
  durationMinutes: number;
  estimatedCaloriesBurnt: number;
  notes?: string;
  createdAt: string;
}

export interface ExerciseSuggestion {
  exerciseName: string;
  assessment: string;
  improvementTips: string[];
  alternatives: { name: string; reason: string }[];
}

export enum StorageKeys {
  USER_PROFILE = 'fitpwa_user_profile',
  WORKOUT_PLANS = 'fitpwa_workout_plans',
  WORKOUT_LOGS = 'fitpwa_workout_logs',
  MEAL_PLANS = 'fitpwa_meal_plans',
  FOOD_LOGS = 'fitpwa_food_logs',
  PROGRESS_SNAPSHOTS = 'fitpwa_progress_snapshots',
  WEIGHT_ENTRIES = 'fitpwa_weight_entries',
  OPENAI_KEY = 'fitpwa_openai_key',
  CUSTOM_WORKOUT_LOGS = 'fitpwa_custom_workout_logs',
  WATER_LOGS = 'fitpwa_water_logs',
  CARDIO_LOGS = 'fitpwa_cardio_logs',
}
