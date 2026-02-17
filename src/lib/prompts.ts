import { UserProfile, WorkoutLog } from '@/types';
import { calculateTDEE, calculateMacroTargets } from './utils';

export function getWorkoutPlanPrompt(profile: UserProfile, previousLogs?: WorkoutLog[], assessment?: string, workoutStyle?: 'single_muscle' | 'muscle_group'): string {
  const isFirstPlan = !previousLogs || previousLogs.length === 0;
  const styleLabel = workoutStyle === 'single_muscle' 
    ? 'single muscle isolation (each day targets one specific muscle, e.g., Chest Day, Back Day, Shoulder Day, Bicep Day, Tricep Day, Leg Day)' 
    : 'muscle group split (combine related muscle groups per day, e.g., Push/Pull/Legs or Upper/Lower)';

  let prompt = `Generate a structured ${profile.intervalWeeks}-week workout plan for the following user:

- Weight: ${profile.weight}kg
- Height: ${profile.height}cm
- Age: ${profile.age}
- Gender: ${profile.gender}
- Activity Level: ${profile.activityLevel.replace('_', ' ')}
- Goal: ${profile.fitnessGoal.replace('_', ' ')}
- Target Weight: ${profile.targetWeight}kg
- Workout Style Preference: ${styleLabel}
`;

  if (!isFirstPlan && assessment) {
    prompt += `\nPrevious interval assessment: ${assessment}\nPlease design the next interval to address the identified weak points.\n`;
  }

  prompt += `
Requirements:
- Plan for 7 days (Monday to Sunday, include 1-2 rest days)
- Each workout day should have 4-6 exercises
- For each exercise provide: name, muscleGroup, sets (3-5), reps (use a range string like "8-12"), restSeconds (60-180)
- Use a split appropriate for the user's goal
- Include brief notes for the overall plan

Return JSON with this exact structure:
{
  "days": [
    {
      "dayNumber": 1,
      "dayLabel": "Push Day",
      "isRestDay": false,
      "exercises": [
        { "name": "Bench Press", "muscleGroup": "Chest", "sets": 4, "reps": "8-12", "restSeconds": 90, "notes": "" }
      ]
    }
  ],
  "aiNotes": "Brief explanation of the plan design"
}`;

  return prompt;
}

export function getWorkoutSystemPrompt(): string {
  return 'You are an expert fitness coach and exercise scientist. Generate structured, evidence-based workout plans. Always return valid JSON matching the requested structure exactly. Do not include any text outside the JSON.';
}

export function getMealPlanPrompt(profile: UserProfile, allergies?: string[]): string {
  const tdee = calculateTDEE(profile);
  const macros = calculateMacroTargets(profile);

  let allergyClause = '';
  if (allergies && allergies.length > 0) {
    allergyClause = `\n- ALLERGIES/INTOLERANCES: ${allergies.join(', ')} — STRICTLY avoid all foods containing these allergens. Do NOT include any ingredient that contains or is derived from these.\n`;
  }

  return `Generate a complete daily meal plan for:

- Weight: ${profile.weight}kg, Height: ${profile.height}cm, Age: ${profile.age}, Gender: ${profile.gender}
- Goal: ${profile.fitnessGoal.replace('_', ' ')}
- TDEE: ${tdee} calories
- Target Macros: ${macros.protein}g protein, ${macros.carbs}g carbs, ${macros.fats}g fats, ${macros.calories} calories${allergyClause}

Requirements:
- Include 5 meals: Breakfast, Morning Snack, Lunch, Afternoon Snack, Dinner
- Use common, accessible foods with specific portion sizes
- Calculate accurate macros for each food item
- Daily totals should be within 5% of the targets

Return JSON with this structure:
{
  "meals": [
    {
      "name": "Breakfast",
      "foods": [
        { "name": "Oatmeal", "servingSize": "80g dry", "macros": { "calories": 300, "protein": 10, "carbs": 54, "fats": 5 } }
      ],
      "totalMacros": { "calories": 500, "protein": 30, "carbs": 60, "fats": 15 }
    }
  ],
  "dailyTotals": { "calories": ${macros.calories}, "protein": ${macros.protein}, "carbs": ${macros.carbs}, "fats": ${macros.fats} },
  "aiNotes": "Explanation of the meal plan"
}`;
}

export function getMealSystemPrompt(): string {
  return 'You are a certified sports nutritionist. Generate practical, balanced meal plans with accurate macro calculations. Always return valid JSON matching the requested structure exactly. Do not include any text outside the JSON.';
}

export function getAssessmentPrompt(logs: WorkoutLog[]): string {
  const summary = logs.map(log => ({
    date: log.date,
    exercises: log.exercises.map(e => ({
      name: e.exerciseName,
      sets: e.sets.map(s => `${s.weight}kg x ${s.reps} reps`).join(', '),
    })),
  }));

  return `Analyze the following workout logs from the current training interval and provide an assessment:

${JSON.stringify(summary, null, 2)}

Provide:
1. Overall consistency assessment
2. Progressive overload analysis (are weights/reps increasing?)
3. Identify muscle groups that are lagging or stalled
4. Specific recommendations for the next training interval

Return JSON:
{
  "overallScore": "good/moderate/needs_improvement",
  "consistency": "description of workout consistency",
  "progressiveOverload": "analysis of strength progression",
  "weakPoints": ["list", "of", "weak", "areas"],
  "recommendations": ["specific", "recommendations", "for", "next", "interval"],
  "summary": "One paragraph summary"
}`;
}

export function getAssessmentSystemPrompt(): string {
  return 'You are an expert fitness coach analyzing workout performance data. Provide honest, actionable assessments. Always return valid JSON matching the requested structure exactly.';
}

export function getFoodAlternativePrompt(productName: string, macros: { calories: number; protein: number; carbs: number; fats: number }, ratio: number): string {
  return `A user scanned a food product with these details:
- Product: ${productName}
- Calories: ${macros.calories}, Protein: ${macros.protein}g, Carbs: ${macros.carbs}g, Fats: ${macros.fats}g
- Protein-to-calorie ratio: ${ratio.toFixed(1)}g per 100 calories

This ratio is ${ratio >= 10 ? 'good' : ratio >= 5 ? 'moderate' : 'poor'}.

${ratio < 10 ? 'Suggest 3 better alternatives with higher protein-to-calorie ratios from the same food category.' : 'Briefly confirm this is a good choice.'}

Return JSON:
{
  "assessment": "Brief assessment of the product",
  "alternatives": [
    { "name": "Product Name", "typicalMacros": { "calories": 100, "protein": 20, "carbs": 5, "fats": 2 }, "reason": "Why this is better" }
  ]
}`;
}

export function getFoodAlternativeSystemPrompt(): string {
  return 'You are a sports nutrition expert. Assess food products and suggest better alternatives when protein-to-calorie ratios are suboptimal. Always return valid JSON.';
}
