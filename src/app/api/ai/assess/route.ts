import { NextRequest, NextResponse } from 'next/server';
import { callOpenAI } from '@/lib/ai';
import {
  getAssessmentPrompt,
  getAssessmentSystemPrompt,
  getFoodAlternativePrompt,
  getFoodAlternativeSystemPrompt,
} from '@/lib/prompts';
import { WorkoutLog } from '@/types';

export async function POST(request: NextRequest) {
  try {
    const body = await request.json();

    if (body.type === 'workout') {
      const { logs } = body as { type: string; logs: WorkoutLog[] };
      const prompt = getAssessmentPrompt(logs);
      const result = await callOpenAI(prompt, getAssessmentSystemPrompt());
      return NextResponse.json(JSON.parse(result));
    }

    if (body.type === 'food') {
      const { productName, macros, ratio } = body;
      const prompt = getFoodAlternativePrompt(productName, macros, ratio);
      const result = await callOpenAI(prompt, getFoodAlternativeSystemPrompt());
      return NextResponse.json(JSON.parse(result));
    }

    return NextResponse.json({ error: 'Invalid assessment type' }, { status: 400 });
  } catch (error: any) {
    console.error('Assessment error:', error);
    return NextResponse.json({ error: error.message || 'Failed to generate assessment' }, { status: 500 });
  }
}
