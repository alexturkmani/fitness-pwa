import { NextRequest, NextResponse } from 'next/server';
import { getServerSession } from 'next-auth';
import { getAuthOptions } from '@/lib/auth';
import bcrypt from 'bcryptjs';
import prisma from '@/lib/prisma';

export async function POST(request: NextRequest) {
  try {
    const session = await getServerSession(getAuthOptions());
    if (!session?.user?.email) {
      return NextResponse.json({ error: 'Not authenticated' }, { status: 401 });
    }

    const { currentPassword, newPassword } = await request.json();

    if (!currentPassword || !newPassword) {
      return NextResponse.json({ error: 'Current and new password are required' }, { status: 400 });
    }

    if (newPassword.length < 6) {
      return NextResponse.json({ error: 'New password must be at least 6 characters' }, { status: 400 });
    }

    const user = await prisma.user.findUnique({
      where: { email: session.user.email },
      select: { password: true },
    });

    if (!user?.password) {
      return NextResponse.json({ error: 'Cannot change password for Google-linked accounts' }, { status: 400 });
    }

    const isValid = await bcrypt.compare(currentPassword, user.password);
    if (!isValid) {
      return NextResponse.json({ error: 'Current password is incorrect' }, { status: 400 });
    }

    const hashedPassword = await bcrypt.hash(newPassword, 12);
    await prisma.user.update({
      where: { email: session.user.email },
      data: { password: hashedPassword },
    });

    return NextResponse.json({ success: true });
  } catch (error: any) {
    console.error('Change password error:', error);
    return NextResponse.json({ error: 'Something went wrong' }, { status: 500 });
  }
}
