import { NextRequest, NextResponse } from 'next/server';
import { getServerSession } from 'next-auth';
import { getAuthOptions } from '@/lib/auth';
import crypto from 'crypto';
import prisma from '@/lib/prisma';
import { sendEmailChangeVerification } from '@/lib/email';

export async function POST(request: NextRequest) {
  try {
    const session = await getServerSession(getAuthOptions());
    if (!session?.user?.email) {
      return NextResponse.json({ error: 'Not authenticated' }, { status: 401 });
    }

    const { newEmail } = await request.json();

    if (!newEmail) {
      return NextResponse.json({ error: 'New email is required' }, { status: 400 });
    }

    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailRegex.test(newEmail)) {
      return NextResponse.json({ error: 'Invalid email address' }, { status: 400 });
    }

    if (newEmail === session.user.email) {
      return NextResponse.json({ error: 'New email is the same as your current email' }, { status: 400 });
    }

    // Check if the new email is already in use
    const existingUser = await prisma.user.findUnique({ where: { email: newEmail } });
    if (existingUser) {
      return NextResponse.json({ error: 'This email is already in use' }, { status: 409 });
    }

    const user = await prisma.user.findUnique({
      where: { email: session.user.email },
    });

    if (!user) {
      return NextResponse.json({ error: 'User not found' }, { status: 404 });
    }

    // Store pending email on the user
    await prisma.user.update({
      where: { id: user.id },
      data: { pendingEmail: newEmail },
    });

    // Delete old email change tokens for this user
    await prisma.verificationToken.deleteMany({
      where: { identifier: `email-change:${user.id}` },
    });

    // Generate verification token
    const token = crypto.randomBytes(32).toString('hex');
    await prisma.verificationToken.create({
      data: {
        identifier: `email-change:${user.id}`,
        token,
        expires: new Date(Date.now() + 24 * 60 * 60 * 1000), // 24 hours
      },
    });

    await sendEmailChangeVerification(newEmail, token);

    return NextResponse.json({ success: true });
  } catch (error: any) {
    console.error('Change email error:', error);
    return NextResponse.json({ error: 'Something went wrong' }, { status: 500 });
  }
}
