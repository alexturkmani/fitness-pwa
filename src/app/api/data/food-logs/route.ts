import { NextResponse } from 'next/server';
import { prisma } from '@/lib/prisma';
import { getUserIdFromBearer } from '@/lib/mobile-auth';

// GET /api/data/food-logs?from=yyyy-mm-dd&to=yyyy-mm-dd
export async function GET(req: Request) {
  const userId = getUserIdFromBearer(req);
  if (!userId) return NextResponse.json({ error: 'Unauthorized' }, { status: 401 });

  const { searchParams } = new URL(req.url);
  const from = searchParams.get('from');
  const to = searchParams.get('to');

  const where: any = { userId };
  if (from) where.date = { ...(where.date || {}), gte: from };
  if (to) where.date = { ...(where.date || {}), lte: to };

  const entries = await prisma.foodLogEntry.findMany({
    where,
    orderBy: { createdAt: 'desc' },
    take: 500,
  });

  return NextResponse.json(
    entries.map((e) => ({
      id: e.id,
      date: e.date,
      foodName: e.foodName,
      servingSize: e.servingSize,
      quantity: e.quantity,
      macros: e.macros,
      source: e.source,
      barcode: e.barcode,
      createdAt: e.createdAt,
    }))
  );
}

// POST /api/data/food-logs
export async function POST(req: Request) {
  const userId = getUserIdFromBearer(req);
  if (!userId) return NextResponse.json({ error: 'Unauthorized' }, { status: 401 });

  const data = await req.json();

  const entry = await prisma.foodLogEntry.create({
    data: {
      id: data.id,
      userId,
      date: data.date,
      foodName: data.foodName,
      servingSize: data.servingSize || '1 serving',
      quantity: data.quantity || 1,
      macros: data.macros || { calories: 0, protein: 0, carbs: 0, fats: 0 },
      source: data.source || 'manual',
      barcode: data.barcode || null,
    },
  });

  return NextResponse.json({ id: entry.id });
}

// DELETE /api/data/food-logs?id=xxx
export async function DELETE(req: Request) {
  const userId = getUserIdFromBearer(req);
  if (!userId) return NextResponse.json({ error: 'Unauthorized' }, { status: 401 });

  const { searchParams } = new URL(req.url);
  const id = searchParams.get('id');
  if (!id) return NextResponse.json({ error: 'ID required' }, { status: 400 });

  await prisma.foodLogEntry.deleteMany({ where: { id, userId } });
  return NextResponse.json({ success: true });
}
