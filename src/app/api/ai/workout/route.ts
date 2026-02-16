import { NextRequest, NextResponse } from 'next/server';
import { callOpenAI } from '@/lib/ai';
import { getWorkoutPlanPrompt, getWorkoutSystemPrompt } from '@/lib/prompts';
import { UserProfile, WorkoutPlan, WorkoutLog } from '@/types';
import { generateId } from '@/lib/utils';

export async function POST(request: NextRequest) {
  try {
    const body = await request.json();
    const { profile, previousLogs, assessment } = body as {
      profile: UserProfile;
      previousLogs?: WorkoutLog[];
      assessment?: string;
    };

    const prompt = getWorkoutPlanPrompt(profile, previousLogs, assessment);
    const result = await callOpenAI(prompt, getWorkoutSystemPrompt());
    const parsed = JSON.parse(result);

    const startDate = new Date();
    const endDate = new Date();
    endDate.setDate(endDate.getDate() + profile.intervalWeeks * 7);

    const plan: WorkoutPlan = {
      id: generateId(),
      intervalNumber: (body.currentInterval || 0) + 1,
      startDate: startDate.toISOString(),
      endDate: endDate.toISOString(),
      weeks: profile.intervalWeeks,
      days: parsed.days.map((day: any) => ({
        ...day,
        id: generateId(),
        exercises: day.exercises?.map((ex: any) => ({
          ...ex,
          id: generateId(),
        })) || [],
      })),
      aiNotes: parsed.aiNotes || '',
      assessmentSummary: assessment,
      createdAt: new Date().toISOString(),
    };

    return NextResponse.json(plan);
  } catch (error: any) {
    console.error('Workout generation error:', error);
    return NextResponse.json({ error: error.message || 'Failed to generate workout plan' }, { status: 500 });
  }
}
