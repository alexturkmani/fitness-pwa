import { UserProfile, WorkoutLog, CustomExerciseLog } from '@/types';
import { calculateTDEE, calculateMacroTargets } from './utils';

export function getWorkoutPlanPrompt(profile: UserProfile, previousLogs?: WorkoutLog[], assessment?: string, workoutStyle?: 'single_muscle' | 'muscle_group'): string {
  const isFirstPlan = !previousLogs || previousLogs.length === 0;
  const isSingleMuscle = workoutStyle === 'single_muscle';

  let prompt = `Generate a structured ${profile.intervalWeeks}-week workout plan for the following user:

- Weight: ${profile.weight}kg
- Height: ${profile.height}cm
- Age: ${profile.age}
- Gender: ${profile.gender}
- Activity Level: ${profile.activityLevel.replace('_', ' ')}
- Goal: ${(profile.fitnessGoals || ['general_fitness']).map(g => g.replace('_', ' ')).join(', ')}
- Target Weight: ${profile.targetWeight}kg
`;

  if (!isFirstPlan && assessment) {
    prompt += `\nPrevious interval assessment: ${assessment}\nPlease design the next interval to address the identified weak points.\n`;
  }

  if (isSingleMuscle) {
    prompt += `
CRITICAL REQUIREMENT — SINGLE MUSCLE SPLIT:
The user has explicitly chosen a SINGLE MUSCLE isolation split. You MUST follow these rules strictly:
- Each training day focuses on ONE specific muscle ONLY (not a combination)
- Valid day labels: "Chest Day", "Back Day", "Shoulder Day", "Bicep Day", "Tricep Day", "Leg Day", "Abs Day", "Forearm Day", "Glute Day", "Hamstring Day", "Quad Day", "Calf Day"
- NEVER label a day "Push Day", "Pull Day", "Upper Body", "Lower Body", or any combined muscle group
- ALL exercises on a given day must target that single muscle exclusively
- Example schedule: Monday: Chest Day, Tuesday: Back Day, Wednesday: Shoulder Day, Thursday: Leg Day, Friday: Bicep & Tricep Day is WRONG — separate them into individual days
- Include 1-2 rest days

Requirements:
- Plan for 7 days (Monday to Sunday, include 1-2 rest days)
- Each workout day should have 4-6 exercises ALL targeting the SAME single muscle
- For each exercise provide: name, muscleGroup (must be the SAME for all exercises in a day), sets (3-5), reps (use a range string like "8-12"), restSeconds (60-180)
- Include brief notes for the overall plan

Return JSON with this exact structure:
{
  "days": [
    {
      "dayNumber": 1,
      "dayLabel": "Chest Day",
      "isRestDay": false,
      "exercises": [
        { "name": "Bench Press", "muscleGroup": "Chest", "sets": 4, "reps": "8-12", "restSeconds": 90, "notes": "" },
        { "name": "Incline Dumbbell Press", "muscleGroup": "Chest", "sets": 4, "reps": "8-12", "restSeconds": 90, "notes": "" }
      ]
    },
    {
      "dayNumber": 2,
      "dayLabel": "Back Day",
      "isRestDay": false,
      "exercises": [
        { "name": "Barbell Row", "muscleGroup": "Back", "sets": 4, "reps": "8-12", "restSeconds": 90, "notes": "" }
      ]
    }
  ],
  "aiNotes": "Brief explanation of the plan design"
}`;
  } else {
    prompt += `
WORKOUT STYLE: Muscle Group Split
- Combine related muscle groups per day (e.g., Push/Pull/Legs, Upper/Lower, or similar grouping)
- Each day can target multiple related muscles

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
  }

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
- Goal: ${(profile.fitnessGoals || ['general_fitness']).map(g => g.replace('_', ' ')).join(', ')}
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

export function getExerciseSuggestionPrompt(exercises: CustomExerciseLog[], userGoals: string[]): string {
  const exerciseSummary = exercises.map(ex => ({
    name: ex.name,
    muscleGroup: ex.muscleGroup,
    sets: ex.sets.map(s => `${s.weight}kg x ${s.reps} reps`),
  }));

  return `Analyze each exercise from the following custom workout log and provide improvement suggestions and alternative exercises:

User's fitness goals: ${userGoals.map(g => g.replace('_', ' ')).join(', ')}

Exercises logged:
${JSON.stringify(exerciseSummary, null, 2)}

For EACH exercise provide:
1. A brief assessment of their form/performance based on the weight and reps logged
2. 2-3 specific tips to improve or progress on that exercise
3. 2-3 alternative exercises that target the same muscle group, with a reason why each is a good alternative

Return JSON with this exact structure:
{
  "suggestions": [
    {
      "exerciseName": "Exercise Name",
      "assessment": "Brief assessment of performance",
      "improvementTips": ["tip 1", "tip 2", "tip 3"],
      "alternatives": [
        { "name": "Alternative Exercise", "reason": "Why this is a good alternative" }
      ]
    }
  ]
}`;
}

export function getExerciseSuggestionSystemPrompt(): string {
  return 'You are an expert strength and conditioning coach. Analyze workout exercises and provide actionable improvement tips and smart alternative exercise suggestions. Always return valid JSON matching the requested structure exactly. Do not include any text outside the JSON.';
}
