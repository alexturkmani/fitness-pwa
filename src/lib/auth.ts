import { NextAuthOptions } from 'next-auth';
import { PrismaAdapter } from '@next-auth/prisma-adapter';
import GoogleProvider from 'next-auth/providers/google';
import CredentialsProvider from 'next-auth/providers/credentials';
import bcrypt from 'bcryptjs';
import prisma from './prisma';

function createAuthOptions(): NextAuthOptions {
  const providers: any[] = [];

  // Only add Google provider when credentials are configured
  if (process.env.GOOGLE_CLIENT_ID && process.env.GOOGLE_CLIENT_SECRET) {
    providers.push(
      GoogleProvider({
        clientId: process.env.GOOGLE_CLIENT_ID,
        clientSecret: process.env.GOOGLE_CLIENT_SECRET,
      })
    );
  }

  // Always add credentials provider
  providers.push(
    CredentialsProvider({
      name: 'Email',
      credentials: {
        email: { label: 'Email', type: 'email' },
        password: { label: 'Password', type: 'password' },
      },
      async authorize(credentials) {
        if (!credentials?.email || !credentials?.password) return null;
        if (!process.env.DATABASE_URL) return null;

        try {
          const user = await prisma.user.findUnique({
            where: { email: credentials.email },
          });

          if (!user || !user.password) return null;

          const isValid = await bcrypt.compare(credentials.password, user.password);
          if (!isValid) return null;

          return { id: user.id, name: user.name, email: user.email, image: user.image };
        } catch (e) {
          return null;
        }
      },
    })
  );

  return {
    adapter: process.env.DATABASE_URL ? PrismaAdapter(prisma) : undefined,
    secret: process.env.NEXTAUTH_SECRET,
    session: { strategy: 'jwt' },
    pages: {
      signIn: '/login',
    },
    providers,
    callbacks: {
      async jwt({ token, user }) {
        if (user) {
          token.id = user.id;
        }
        if (token.id && process.env.DATABASE_URL) {
          try {
            const dbUser = await prisma.user.findUnique({
              where: { id: token.id as string },
              select: { trialEndsAt: true, subscriptionActive: true, isFreeAccount: true, hasUsedTrial: true },
            });
            if (dbUser) {
              token.trialEndsAt = dbUser.trialEndsAt?.toISOString() || null;
              token.subscriptionActive = dbUser.subscriptionActive;
              token.isFreeAccount = dbUser.isFreeAccount;
              token.hasUsedTrial = dbUser.hasUsedTrial;
            }
          } catch (e) {
            // DB not available — continue with existing token data
          }
        }
        return token;
      },
      async session({ session, token }) {
        if (session.user) {
          (session.user as any).id = token.id;
          (session.user as any).trialEndsAt = token.trialEndsAt;
          (session.user as any).subscriptionActive = token.subscriptionActive;
          (session.user as any).isFreeAccount = token.isFreeAccount;
          (session.user as any).hasUsedTrial = token.hasUsedTrial;
        }
        return session;
      },
    },
    events: {
      async createUser({ user }) {
        if (!process.env.DATABASE_URL) return;
        try {
          await prisma.user.update({
            where: { id: user.id },
            data: { trialEndsAt: new Date(Date.now() + 7 * 24 * 60 * 60 * 1000), hasUsedTrial: true },
          });
        } catch (e) {
          // DB not available
        }
      },
    },
  };
}

// Lazy getter — only creates options when actually needed at runtime
let _authOptions: NextAuthOptions | null = null;
export function getAuthOptions(): NextAuthOptions {
  if (!_authOptions) {
    _authOptions = createAuthOptions();
  }
  return _authOptions;
}
