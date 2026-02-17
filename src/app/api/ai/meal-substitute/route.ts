import { NextRequest, NextResponse } from 'next/server';
import { callAI } from '@/lib/ai';
import { getMealSubstitutionPrompt, getMealSubstitutionSystemPrompt } from '@/lib/prompts';

export async function POST(request: NextRequest) {
  try {
    const body = await request.json();
    const { mealName, foodName, reason, currentMacros } = body;

    if (!foodName || !mealName) {
      return NextResponse.json({ error: 'Missing meal or food name' }, { status: 400 });
    }

    const prompt = getMealSubstitutionPrompt(mealName, foodName, reason || '', currentMacros || {});
    const result = await callAI(prompt, getMealSubstitutionSystemPrompt());
    return NextResponse.json(JSON.parse(result));
  } catch (error: any) {
    console.error('Meal substitution error:', error);
    return NextResponse.json({ error: error.message || 'Failed to find substitutions' }, { status: 500 });
  }
}
