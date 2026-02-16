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

export const ROTATION_EXPLANATION = {
  title: 'Why Rotate Every 6-8 Weeks?',
  points: [
    'Your body adapts to repetitive exercises, leading to plateaus in progress.',
    'Rotating exercises prevents overuse injuries by varying movement patterns.',
    'New stimuli force muscles to adapt, promoting continued growth and strength.',
    'It keeps your workouts fresh and mentally engaging.',
  ],
};
