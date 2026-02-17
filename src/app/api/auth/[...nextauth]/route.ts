import { NextRequest } from 'next/server';
import NextAuth from 'next-auth';
import { getAuthOptions } from '@/lib/auth';

async function auth(req: NextRequest, context: { params: { nextauth: string[] } }) {
  return NextAuth(req as any, context as any, getAuthOptions());
}

export { auth as GET, auth as POST };
