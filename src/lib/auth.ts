import { NextAuthOptions } from 'next-auth';
import { PrismaAdapter } from '@next-auth/prisma-adapter';
import GoogleProvider from 'next-auth/providers/google';
import CredentialsProvider from 'next-auth/providers/credentials';
import bcrypt from 'bcryptjs';
import prisma from './prisma';

function createAuthOptions(): NextAuthOptions {
  return {
    adapter: process.env.DATABASE_URL ? PrismaAdapter(prisma) : undefined,
    session: { strategy: 'jwt' },
    pages: {
      signIn: '/login',
    },
  providers: [
    GoogleProvider({
      clientId: process.env.GOOGLE_CLIENT_ID!,
      clientSecret: process.env.GOOGLE_CLIENT_SECRET!,
    }),
    CredentialsProvider({
      name: 'Email',
      credentials: {
        email: { label: 'Email', type: 'email' },
        password: { label: 'Password', type: 'password' },
      },
      async authorize(credentials) {
        if (!credentials?.email || !credentials?.password) return null;

        const user = await prisma.user.findUnique({
          where: { email: credentials.email },
        });

        if (!user || !user.password) return null;

        const isValid = await bcrypt.compare(credentials.password, user.password);
        if (!isValid) return null;

        return { id: user.id, name: user.name, email: user.email, image: user.image };
      },
    }),
  ],
  callbacks: {
    async jwt({ token, user }) {
      if (user) {
        token.id = user.id;
      }
      if (token.id) {
        const dbUser = await prisma.user.findUnique({
          where: { id: token.id as string },
          select: { trialEndsAt: true, subscriptionActive: true },
        });
        if (dbUser) {
          token.trialEndsAt = dbUser.trialEndsAt?.toISOString() || null;
          token.subscriptionActive = dbUser.subscriptionActive;
        }
      }
      return token;
    },
    async session({ session, token }) {
      if (session.user) {
        (session.user as any).id = token.id;
        (session.user as any).trialEndsAt = token.trialEndsAt;
        (session.user as any).subscriptionActive = token.subscriptionActive;
      }
      return session;
    },
  },
  events: {
    async createUser({ user }) {
      await prisma.user.update({
        where: { id: user.id },
        data: { trialEndsAt: new Date(Date.now() + 7 * 24 * 60 * 60 * 1000) },
      });
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
