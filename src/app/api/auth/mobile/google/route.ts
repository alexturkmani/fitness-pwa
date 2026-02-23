import { NextResponse } from 'next/server';
import { prisma } from '@/lib/prisma';
import jwt from 'jsonwebtoken';
import { OAuth2Client } from 'google-auth-library';

const JWT_SECRET = process.env.NEXTAUTH_SECRET || 'fallback-secret';
const googleClient = new OAuth2Client(process.env.GOOGLE_CLIENT_ID);

export async function POST(req: Request) {
  try {
    const { idToken } = await req.json();

    if (!idToken) {
      return NextResponse.json({ error: 'ID token is required' }, { status: 400 });
    }

    const ticket = await googleClient.verifyIdToken({
      idToken,
      audience: process.env.GOOGLE_CLIENT_ID,
    });

    const payload = ticket.getPayload();
    if (!payload || !payload.email) {
      return NextResponse.json({ error: 'Invalid Google token' }, { status: 401 });
    }

    // Find or create user
    let user = await prisma.user.findUnique({ where: { email: payload.email } });

    if (!user) {
      // Create new user with 7-day free trial
      const trialEnd = new Date();
      trialEnd.setDate(trialEnd.getDate() + 7);

      user = await prisma.user.create({
        data: {
          email: payload.email,
          name: payload.name || '',
          emailVerified: new Date(),
          trialEndsAt: trialEnd,
          accounts: {
            create: {
              type: 'oauth',
              provider: 'google',
              providerAccountId: payload.sub,
            },
          },
        },
      });
    } else {
      // Link Google account if not already linked
      const existingAccount = await prisma.account.findFirst({
        where: { userId: user.id, provider: 'google' },
      });
      if (!existingAccount) {
        await prisma.account.create({
          data: {
            userId: user.id,
            type: 'oauth',
            provider: 'google',
            providerAccountId: payload.sub,
          },
        });
      }
    }

    const hasAccess =
      user.subscriptionActive ||
      user.isFreeAccount ||
      (user.trialEndsAt && new Date(user.trialEndsAt) > new Date());

    const token = jwt.sign(
      { userId: user.id, email: user.email, hasAccess },
      JWT_SECRET,
      { expiresIn: '30d' }
    );

    return NextResponse.json({
      token,
      user: {
        id: user.id,
        name: user.name,
        email: user.email,
        hasAccess,
        subscriptionActive: user.subscriptionActive,
        trialEndsAt: user.trialEndsAt?.toISOString() || null,
      },
    });
  } catch (error: any) {
    console.error('Mobile Google sign-in error:', error);
    return NextResponse.json({ error: 'Authentication failed' }, { status: 500 });
  }
}
