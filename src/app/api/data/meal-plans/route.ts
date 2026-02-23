import { NextResponse } from 'next/server';
import { prisma } from '@/lib/prisma';
import { getUserIdFromBearer } from '@/lib/mobile-auth';

// GET /api/data/meal-plans
export async function GET(req: Request) {
  const userId = getUserIdFromBearer(req);
  if (!userId) return NextResponse.json({ error: 'Unauthorized' }, { status: 401 });

  const plans = await prisma.mealPlan.findMany({
    where: { userId },
    orderBy: { createdAt: 'desc' },
    take: 5,
  });

  return NextResponse.json(plans.map((p) => ({ id: p.id, ...p.data as object, createdAt: p.createdAt })));
}

// POST /api/data/meal-plans
export async function POST(req: Request) {
  const userId = getUserIdFromBearer(req);
  if (!userId) return NextResponse.json({ error: 'Unauthorized' }, { status: 401 });

  const data = await req.json();

  const plan = await prisma.mealPlan.upsert({
    where: { id: data.id || 'new' },
    create: { id: data.id, userId, data },
    update: { data },
  });

  return NextResponse.json({ id: plan.id });
}

// DELETE /api/data/meal-plans?id=xxx
export async function DELETE(req: Request) {
  const userId = getUserIdFromBearer(req);
  if (!userId) return NextResponse.json({ error: 'Unauthorized' }, { status: 401 });

  const { searchParams } = new URL(req.url);
  const id = searchParams.get('id');
  if (!id) return NextResponse.json({ error: 'ID required' }, { status: 400 });

  await prisma.mealPlan.deleteMany({ where: { id, userId } });
  return NextResponse.json({ success: true });
}
