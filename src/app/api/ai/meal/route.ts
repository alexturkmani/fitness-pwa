import { NextRequest, NextResponse } from 'next/server';
import { callAI } from '@/lib/ai';
import { getMealPlanPrompt, getMealSystemPrompt } from '@/lib/prompts';
import { UserProfile, MealPlan } from '@/types';
import { generateId, calculateMacroTargets, formatDate } from '@/lib/utils';

export async function POST(request: NextRequest) {
  try {
    const body = await request.json();
    const { profile } = body as { profile: UserProfile };

    const prompt = getMealPlanPrompt(profile);
    const result = await callAI(prompt, getMealSystemPrompt());

    let parsed;
    try {
      parsed = JSON.parse(result);
    } catch (parseError) {
      console.error('Failed to parse Gemini response:', result.substring(0, 500));
      return NextResponse.json({ error: 'AI returned invalid response. Please try again.' }, { status: 500 });
    }

    const targets = calculateMacroTargets(profile);

    const plan: MealPlan = {
      id: generateId(),
      date: formatDate(new Date()),
      meals: (parsed.meals || []).map((meal: any) => ({
        ...meal,
        id: generateId(),
        foods: (meal.foods || []).map((food: any) => ({
          ...food,
          id: generateId(),
        })),
      })),
      dailyTotals: parsed.dailyTotals || targets,
      dailyTargets: targets,
      aiNotes: parsed.aiNotes || '',
      createdAt: new Date().toISOString(),
    };

    return NextResponse.json(plan);
  } catch (error: any) {
    console.error('Meal plan generation error:', error);
    return NextResponse.json({ error: error.message || 'Failed to generate meal plan' }, { status: 500 });
  }
}
