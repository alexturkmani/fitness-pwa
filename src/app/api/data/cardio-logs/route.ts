import { NextResponse } from 'next/server';
import { prisma } from '@/lib/prisma';
import { getUserIdFromBearer } from '@/lib/mobile-auth';

// GET /api/data/cardio-logs
export async function GET(req: Request) {
  const userId = getUserIdFromBearer(req);
  if (!userId) return NextResponse.json({ error: 'Unauthorized' }, { status: 401 });

  const entries = await prisma.cardioLogEntry.findMany({
    where: { userId },
    orderBy: { createdAt: 'asc' },
    take: 365,
  });

  return NextResponse.json(
    entries.map((e) => ({
      id: e.id,
      date: e.date,
      type: e.type,
      durationMinutes: e.durationMinutes,
      estimatedCaloriesBurnt: e.estimatedCaloriesBurnt,
      notes: e.notes,
      createdAt: e.createdAt,
    }))
  );
}

// POST /api/data/cardio-logs
export async function POST(req: Request) {
  const userId = getUserIdFromBearer(req);
  if (!userId) return NextResponse.json({ error: 'Unauthorized' }, { status: 401 });

  const data = await req.json();

  const entry = await prisma.cardioLogEntry.create({
    data: {
      id: data.id,
      userId,
      date: data.date,
      type: data.type,
      durationMinutes: data.durationMinutes,
      estimatedCaloriesBurnt: data.estimatedCaloriesBurnt,
      notes: data.notes ?? null,
    },
  });

  return NextResponse.json({ id: entry.id });
}

// DELETE /api/data/cardio-logs?id=...
export async function DELETE(req: Request) {
  const userId = getUserIdFromBearer(req);
  if (!userId) return NextResponse.json({ error: 'Unauthorized' }, { status: 401 });

  const { searchParams } = new URL(req.url);
  const id = searchParams.get('id');
  if (!id) return NextResponse.json({ error: 'Missing id' }, { status: 400 });

  await prisma.cardioLogEntry.deleteMany({
    where: { id, userId },
  });

  return NextResponse.json({ ok: true });
}
