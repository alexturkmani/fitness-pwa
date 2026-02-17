import 'next-auth';

declare module 'next-auth' {
  interface Session {
    user: {
      id: string;
      name?: string | null;
      email?: string | null;
      image?: string | null;
      trialEndsAt?: string | null;
      subscriptionActive?: boolean;
    };
  }
}

declare module 'next-auth/jwt' {
  interface JWT {
    id?: string;
    trialEndsAt?: string | null;
    subscriptionActive?: boolean;
  }
}
