export const ACTIVITY_LEVELS = [
  { value: 'sedentary', label: 'Sedentary', description: 'Little or no exercise, desk job' },
  { value: 'lightly_active', label: 'Lightly Active', description: 'Light exercise 1-3 days/week' },
  { value: 'moderately_active', label: 'Moderately Active', description: 'Moderate exercise 3-5 days/week' },
  { value: 'very_active', label: 'Very Active', description: 'Hard exercise 6-7 days/week' },
  { value: 'extremely_active', label: 'Extremely Active', description: 'Very hard exercise, physical job' },
] as const;

export const FITNESS_GOALS = [
  { value: 'weight_loss', label: 'Weight Loss', description: 'Burn fat and reduce body weight', icon: 'TrendingDown' },
  { value: 'muscle_gain', label: 'Muscle Gain', description: 'Build lean muscle mass', icon: 'Dumbbell' },
  { value: 'strength', label: 'Strength', description: 'Increase max lifting capacity', icon: 'Zap' },
  { value: 'endurance', label: 'Endurance', description: 'Improve cardiovascular fitness', icon: 'Heart' },
  { value: 'general_fitness', label: 'General Fitness', description: 'Overall health and wellness', icon: 'Activity' },
] as const;

export const LIFTING_EXPERIENCE_LEVELS = [
  { value: 'beginner', label: 'Beginner', description: 'Less than 6 months of lifting experience' },
  { value: 'intermediate', label: 'Intermediate', description: '6 months to 2 years of consistent training' },
  { value: 'advanced', label: 'Advanced', description: '2-5 years of serious lifting' },
  { value: 'expert', label: 'Expert', description: '5+ years of dedicated strength training' },
] as const;

export const TRAINING_LOCATIONS = [
  { value: 'gym', label: 'Gym', description: 'Full gym with machines, barbells, and dumbbells', icon: 'Dumbbell' },
  { value: 'home', label: 'Home', description: 'Home workouts with limited or no equipment', icon: 'Home' },
] as const;

export const CARDIO_TYPES = [
  'Running', 'Walking', 'Cycling', 'Swimming', 'Rowing', 'Jump Rope',
  'Elliptical', 'Stair Climbing', 'HIIT', 'Dancing', 'Hiking', 'Other',
] as const;

export const GYM_DAYS_COMMITMENT = [
  { days: 3, label: 'Easy', description: 'Great for beginners or busy schedules', color: '#10b981', emoji: '🟢' },
  { days: 4, label: 'Moderate', description: 'Good balance of training and recovery', color: '#06b6d4', emoji: '🔵' },
  { days: 5, label: 'Committed', description: 'Solid routine for serious progress', color: '#f59e0b', emoji: '🟡' },
  { days: 6, label: 'Intense', description: 'High volume, needs good recovery', color: '#f97316', emoji: '🟠' },
  { days: 7, label: 'Extreme', description: 'Maximum effort, risk of overtraining', color: '#ef4444', emoji: '🔴' },
] as const;

export const ROTATION_EXPLANATION = {
  title: 'Why Rotate Every 6-8 Weeks?',
  points: [
    'Your body adapts to repetitive exercises, leading to plateaus in progress.',
    'Rotating exercises prevents overuse injuries by varying movement patterns.',
    'New stimuli force muscles to adapt, promoting continued growth and strength.',
    'It keeps your workouts fresh and mentally engaging.',
  ],
};
