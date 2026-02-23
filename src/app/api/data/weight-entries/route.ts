import { NextResponse } from 'next/server';
import { prisma } from '@/lib/prisma';
import { getUserIdFromBearer } from '@/lib/mobile-auth';

// GET /api/data/weight-entries
export async function GET(req: Request) {
  const userId = getUserIdFromBearer(req);
  if (!userId) return NextResponse.json({ error: 'Unauthorized' }, { status: 401 });

  const entries = await prisma.weightEntry.findMany({
    where: { userId },
    orderBy: { createdAt: 'asc' },
    take: 365,
  });

  return NextResponse.json(
    entries.map((e) => ({
      id: e.id,
      date: e.date,
      weight: e.weight,
      createdAt: e.createdAt,
    }))
  );
}

// POST /api/data/weight-entries
export async function POST(req: Request) {
  const userId = getUserIdFromBearer(req);
  if (!userId) return NextResponse.json({ error: 'Unauthorized' }, { status: 401 });

  const data = await req.json();

  const entry = await prisma.weightEntry.create({
    data: {
      id: data.id,
      userId,
      date: data.date,
      weight: data.weight,
    },
  });

  return NextResponse.json({ id: entry.id });
}
