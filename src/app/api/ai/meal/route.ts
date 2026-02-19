import { NextRequest, NextResponse } from 'next/server';
import { callAI } from '@/lib/ai';
import { getMealPlanPrompt, getMealSystemPrompt } from '@/lib/prompts';
import { UserProfile, MealPlan } from '@/types';
import { generateId, calculateMacroTargets, calculateDailyWaterIntake, formatDate } from '@/lib/utils';

export async function POST(request: NextRequest) {
  try {
    const body = await request.json();
    const { profile, allergies } = body as { profile: UserProfile; allergies?: string[] };

    const prompt = getMealPlanPrompt(profile, allergies);
    const result = await callAI(prompt, getMealSystemPrompt());

    let parsed;
    try {
      parsed = JSON.parse(result);
    } catch (parseError) {
      console.error('Failed to parse Gemini response:', result.substring(0, 500));
      return NextResponse.json({ error: 'AI returned invalid response. Please try again.' }, { status: 500 });
    }

    const targets = calculateMacroTargets(profile);

    const roundMacros = (macros: any) => ({
      calories: Math.round(macros?.calories || 0),
      protein: Math.round(macros?.protein || 0),
      carbs: Math.round(macros?.carbs || 0),
      fats: Math.round(macros?.fats || 0),
      ...(macros?.fiber != null ? { fiber: Math.round(macros.fiber) } : {}),
    });

    const plan: MealPlan = {
      id: generateId(),
      date: formatDate(new Date()),
      meals: (parsed.meals || []).map((meal: any) => ({
        ...meal,
        id: generateId(),
        foods: (meal.foods || []).map((food: any) => ({
          ...food,
          id: generateId(),
          macros: roundMacros(food.macros),
        })),
        totalMacros: roundMacros(meal.totalMacros),
      })),
      dailyTotals: roundMacros(parsed.dailyTotals || targets),
      dailyTargets: targets,
      dailyWaterIntakeMl: parsed.dailyWaterIntakeMl || calculateDailyWaterIntake(profile),
      aiNotes: parsed.aiNotes || '',
      createdAt: new Date().toISOString(),
    };

    return NextResponse.json(plan);
  } catch (error: any) {
    console.error('Meal plan generation error:', error);
    return NextResponse.json({ error: error.message || 'Failed to generate meal plan' }, { status: 500 });
  }
}
