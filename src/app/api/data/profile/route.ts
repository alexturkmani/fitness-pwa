import { NextResponse } from 'next/server';
import { prisma } from '@/lib/prisma';
import { getUserIdFromBearer } from '@/lib/mobile-auth';

// GET /api/data/profile - Get user profile
export async function GET(req: Request) {
  const userId = getUserIdFromBearer(req);
  if (!userId) return NextResponse.json({ error: 'Unauthorized' }, { status: 401 });

  const profile = await prisma.userProfile.findUnique({ where: { userId } });
  return NextResponse.json(profile || null);
}

// POST /api/data/profile - Save/update user profile
export async function POST(req: Request) {
  const userId = getUserIdFromBearer(req);
  if (!userId) return NextResponse.json({ error: 'Unauthorized' }, { status: 401 });

  const data = await req.json();

  const profile = await prisma.userProfile.upsert({
    where: { userId },
    create: {
      userId,
      name: data.name || '',
      weight: data.weight || 0,
      height: data.height || 0,
      age: data.age || 0,
      gender: data.gender || 'male',
      activityLevel: data.activityLevel || 'moderately_active',
      fitnessGoals: data.fitnessGoals || [],
      targetWeight: data.targetWeight || 0,
      intervalWeeks: data.intervalWeeks || 6,
      gymDaysPerWeek: data.gymDaysPerWeek || 5,
      workoutStyle: data.workoutStyle || 'gym',
      onboardingDone: data.onboardingCompleted || false,
    },
    update: {
      name: data.name,
      weight: data.weight,
      height: data.height,
      age: data.age,
      gender: data.gender,
      activityLevel: data.activityLevel,
      fitnessGoals: data.fitnessGoals,
      targetWeight: data.targetWeight,
      intervalWeeks: data.intervalWeeks,
      gymDaysPerWeek: data.gymDaysPerWeek,
      workoutStyle: data.workoutStyle,
      onboardingDone: data.onboardingCompleted,
    },
  });

  return NextResponse.json(profile);
}
