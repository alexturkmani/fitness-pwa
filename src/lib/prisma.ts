import { PrismaClient } from '@prisma/client';

const globalForPrisma = globalThis as unknown as { prisma: PrismaClient | undefined };

function createPrismaClient() {
  if (!process.env.DATABASE_URL) {
    // Return a proxy that throws helpful errors at runtime but doesn't crash at build time
    return new Proxy({} as PrismaClient, {
      get(_, prop) {
        if (prop === 'then' || prop === '$connect' || prop === '$disconnect') return undefined;
        return () => {
          throw new Error('DATABASE_URL is not configured. Add it to your environment variables.');
        };
      },
    });
  }
  return new PrismaClient();
}

export const prisma = globalForPrisma.prisma ?? createPrismaClient();

if (process.env.NODE_ENV !== 'production') globalForPrisma.prisma = prisma;

export default prisma;
