import { NextRequest, NextResponse } from 'next/server';
import prisma from '@/lib/prisma';

export const dynamic = 'force-dynamic';

export async function GET(request: NextRequest) {
  try {
    const token = request.nextUrl.searchParams.get('token');

    if (!token) {
      return NextResponse.redirect(new URL('/profile?email-change=invalid', request.url));
    }

    const verificationToken = await prisma.verificationToken.findUnique({
      where: { token },
    });

    if (!verificationToken || !verificationToken.identifier.startsWith('email-change:')) {
      return NextResponse.redirect(new URL('/profile?email-change=invalid', request.url));
    }

    if (new Date() > verificationToken.expires) {
      await prisma.verificationToken.delete({ where: { token } });
      return NextResponse.redirect(new URL('/profile?email-change=expired', request.url));
    }

    const userId = verificationToken.identifier.replace('email-change:', '');

    const user = await prisma.user.findUnique({
      where: { id: userId },
      select: { pendingEmail: true },
    });

    if (!user?.pendingEmail) {
      await prisma.verificationToken.delete({ where: { token } });
      return NextResponse.redirect(new URL('/profile?email-change=invalid', request.url));
    }

    // Check the new email isn't taken (race condition guard)
    const existing = await prisma.user.findUnique({ where: { email: user.pendingEmail } });
    if (existing) {
      await prisma.verificationToken.delete({ where: { token } });
      return NextResponse.redirect(new URL('/profile?email-change=taken', request.url));
    }

    // Update the user's email
    await prisma.user.update({
      where: { id: userId },
      data: {
        email: user.pendingEmail,
        pendingEmail: null,
        emailVerified: new Date(),
      },
    });

    // Delete the used token
    await prisma.verificationToken.delete({ where: { token } });

    return NextResponse.redirect(new URL('/profile?email-change=success', request.url));
  } catch (error) {
    console.error('Email change verification error:', error);
    return NextResponse.redirect(new URL('/profile?email-change=failed', request.url));
  }
}
