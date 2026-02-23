import { NextResponse } from 'next/server';
import { prisma } from '@/lib/prisma';
import { getUserIdFromBearer } from '@/lib/mobile-auth';

// GET /api/data/custom-workouts
export async function GET(req: Request) {
  const userId = getUserIdFromBearer(req);
  if (!userId) return NextResponse.json({ error: 'Unauthorized' }, { status: 401 });

  const logs = await prisma.customWorkoutLog.findMany({
    where: { userId },
    orderBy: { createdAt: 'desc' },
    take: 100,
  });

  return NextResponse.json(
    logs.map((l) => ({
      id: l.id,
      name: l.name,
      date: l.date,
      exercises: (l.data as any)?.exercises || [],
      createdAt: l.createdAt,
    }))
  );
}

// POST /api/data/custom-workouts
export async function POST(req: Request) {
  const userId = getUserIdFromBearer(req);
  if (!userId) return NextResponse.json({ error: 'Unauthorized' }, { status: 401 });

  const data = await req.json();

  const log = await prisma.customWorkoutLog.create({
    data: {
      id: data.id,
      userId,
      name: data.name || 'Custom Workout',
      date: data.date,
      data: { exercises: data.exercises || [] },
    },
  });

  return NextResponse.json({ id: log.id });
}

// DELETE /api/data/custom-workouts?id=xxx
export async function DELETE(req: Request) {
  const userId = getUserIdFromBearer(req);
  if (!userId) return NextResponse.json({ error: 'Unauthorized' }, { status: 401 });

  const { searchParams } = new URL(req.url);
  const id = searchParams.get('id');
  if (!id) return NextResponse.json({ error: 'ID required' }, { status: 400 });

  await prisma.customWorkoutLog.deleteMany({ where: { id, userId } });
  return NextResponse.json({ success: true });
}
