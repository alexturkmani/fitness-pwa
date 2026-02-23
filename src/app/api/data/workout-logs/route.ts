import { NextResponse } from 'next/server';
import { prisma } from '@/lib/prisma';
import { getUserIdFromBearer } from '@/lib/mobile-auth';

// GET /api/data/workout-logs
export async function GET(req: Request) {
  const userId = getUserIdFromBearer(req);
  if (!userId) return NextResponse.json({ error: 'Unauthorized' }, { status: 401 });

  const logs = await prisma.workoutLog.findMany({
    where: { userId },
    orderBy: { createdAt: 'desc' },
    take: 100,
  });

  return NextResponse.json(
    logs.map((l) => ({
      id: l.id,
      date: l.date,
      planId: l.planId,
      dayId: l.dayId,
      exercises: (l.data as any)?.exercises || [],
      createdAt: l.createdAt,
    }))
  );
}

// POST /api/data/workout-logs
export async function POST(req: Request) {
  const userId = getUserIdFromBearer(req);
  if (!userId) return NextResponse.json({ error: 'Unauthorized' }, { status: 401 });

  const data = await req.json();

  const log = await prisma.workoutLog.create({
    data: {
      id: data.id,
      userId,
      date: data.date,
      planId: data.planId || null,
      dayId: data.dayId || null,
      data: { exercises: data.exercises || [] },
    },
  });

  return NextResponse.json({ id: log.id });
}
