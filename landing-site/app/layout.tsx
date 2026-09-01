import type { Metadata } from 'next';
import { Geist, Geist_Mono } from 'next/font/google';
import './globals.css';
import './sections.css';

const geistSans = Geist({
  variable: '--font-geist-sans',
  subsets: ['latin'],
});

const geistMono = Geist_Mono({
  variable: '--font-geist-mono',
  subsets: ['latin'],
});

export const metadata: Metadata = {
  title: 'Nexal — AI Workout & Meal Planner',
  description: 'Track meals and workouts for free. Unlock personalized AI workout and meal plans with Nexal Premium.',
  openGraph: {
    title: 'Nexal — Build the body. Own the journey.',
    description: 'Your meals, workouts and progress—connected in one intelligent fitness app.',
    type: 'website',
    images: [{ url: '/og.png', width: 1731, height: 909, alt: 'Nexal fitness app' }],
  },
  twitter: {
    card: 'summary_large_image',
    title: 'Nexal — Build the body. Own the journey.',
    description: 'Your meals, workouts and progress—connected in one intelligent fitness app.',
    images: ['/og.png'],
  },
};

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <html lang="en">
      <body
        className={`${geistSans.variable} ${geistMono.variable} antialiased`}
      >
        {children}
      </body>
    </html>
  );
}
