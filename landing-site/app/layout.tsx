import type { Metadata } from 'next';
import './globals.css';
import './sections.css';

export const metadata: Metadata = {
  metadataBase: new URL('https://www.nexalfitness.com'),
  title: { default: 'Nexal: AI Workout & Meal Planner App for Android', template: '%s | Nexal' },
  description: 'Get personalized AI workout and meal plans, track calories and macros, log workouts and see your progress in one Android fitness app.',
  applicationName: 'Nexal',
  alternates: { canonical: '/' },
  keywords: ['AI workout planner', 'AI meal planner', 'workout tracker', 'calorie tracker', 'macro tracker', 'Android fitness app'],
  robots: { index: true, follow: true, googleBot: { index: true, follow: true, 'max-image-preview': 'large', 'max-snippet': -1, 'max-video-preview': -1 } },
  icons: { icon: '/nexal-logo.png', apple: '/nexal-logo.png' },
  openGraph: {
    title: 'Nexal | Build the body. Own the journey.',
    description: 'Your meals, workouts and progress, connected in one intelligent fitness app.',
    type: 'website',
    images: [{ url: '/og.png', width: 1731, height: 909, alt: 'Nexal fitness app' }],
    url: '/',
    siteName: 'Nexal',
  },
  twitter: {
    card: 'summary_large_image',
    title: 'Nexal | Build the body. Own the journey.',
    description: 'Your meals, workouts and progress, connected in one intelligent fitness app.',
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
      <body style={{ fontFamily: 'Inter, Segoe UI, Arial, sans-serif' }}>
        {children}
      </body>
    </html>
  );
}
