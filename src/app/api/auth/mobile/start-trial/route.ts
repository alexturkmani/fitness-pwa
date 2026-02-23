import { NextRequest, NextResponse } from 'next/server';
import prisma from '@/lib/prisma';
import jwt from 'jsonwebtoken';

export async function POST(request: NextRequest) {
  try {
    const authHeader = request.headers.get('authorization');
    if (!authHeader?.startsWith('Bearer ')) {
      return NextResponse.json({ error: 'Unauthorized' }, { status: 401 });
    }
    const token = authHeader.substring(7);
    const decoded = jwt.verify(token, process.env.NEXTAUTH_SECRET!) as any;
    const userId = decoded.sub || decoded.userId;
    if (!userId) {
      return NextResponse.json({ error: 'Unauthorized' }, { status: 401 });
    }

    const user = await prisma.user.findUnique({
      where: { id: userId },
      select: { hasUsedTrial: true, trialEndsAt: true, subscriptionActive: true },
    });

    if (!user) {
      return NextResponse.json({ error: 'User not found' }, { status: 404 });
    }

    if (user.hasUsedTrial) {
      return NextResponse.json({ error: 'Trial already used' }, { status: 400 });
    }

    const trialEnd = new Date();
    trialEnd.setDate(trialEnd.getDate() + 7);

    await prisma.user.update({
      where: { id: userId },
      data: { trialEndsAt: trialEnd, hasUsedTrial: true },
    });

    return NextResponse.json({
      success: true,
      trialEndsAt: trialEnd.toISOString(),
    });
  } catch (error: any) {
    console.error('Start trial error:', error);
    return NextResponse.json({ error: error.message || 'Failed to start trial' }, { status: 500 });
  }
}
