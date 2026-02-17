import { NextRequest, NextResponse } from 'next/server';
import { callAI } from '@/lib/ai';
import { getExerciseSuggestionPrompt, getExerciseSuggestionSystemPrompt } from '@/lib/prompts';
import { CustomExerciseLog } from '@/types';

export async function POST(request: NextRequest) {
  try {
    const body = await request.json();
    const { exercises, goals } = body as { exercises: CustomExerciseLog[]; goals: string[] };

    if (!exercises || exercises.length === 0) {
      return NextResponse.json({ error: 'No exercises provided' }, { status: 400 });
    }

    const prompt = getExerciseSuggestionPrompt(exercises, goals || ['general_fitness']);
    const result = await callAI(prompt, getExerciseSuggestionSystemPrompt());
    return NextResponse.json(JSON.parse(result));
  } catch (error: any) {
    console.error('Exercise suggestion error:', error);
    return NextResponse.json({ error: error.message || 'Failed to generate suggestions' }, { status: 500 });
  }
}
