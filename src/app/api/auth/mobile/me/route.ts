import { NextResponse } from 'next/server';
import { prisma } from '@/lib/prisma';
import jwt from 'jsonwebtoken';

const JWT_SECRET = process.env.NEXTAUTH_SECRET || 'fallback-secret';

function getUserIdFromRequest(req: Request): string | null {
  const auth = req.headers.get('authorization');
  if (!auth?.startsWith('Bearer ')) return null;
  try {
    const decoded = jwt.verify(auth.slice(7), JWT_SECRET) as { userId: string };
    return decoded.userId;
  } catch {
    return null;
  }
}

export async function GET(req: Request) {
  const userId = getUserIdFromRequest(req);
  if (!userId) return NextResponse.json({ error: 'Unauthorized' }, { status: 401 });

  const user = await prisma.user.findUnique({ where: { id: userId } });
  if (!user) return NextResponse.json({ error: 'User not found' }, { status: 404 });

  const hasAccess =
    user.subscriptionActive ||
    user.isFreeAccount ||
    (user.trialEndsAt && new Date(user.trialEndsAt) > new Date());

  return NextResponse.json({
    id: user.id,
    name: user.name,
    email: user.email,
    hasAccess,
    subscriptionActive: user.subscriptionActive,
    trialEndsAt: user.trialEndsAt?.toISOString() || null,
  });
}
