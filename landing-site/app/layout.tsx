import type { Metadata } from 'next';
import './globals.css';
import './sections.css';
import { siteUrl } from './site-config';

export const metadata: Metadata = {
  metadataBase: new URL(siteUrl),
  title: { default: 'AI Workout & Meal Planner App for Android | Nexal', template: '%s | Nexal' },
  description: 'Create personalised AI workout and meal plans, track calories and macros, and follow your progress in one Android fitness app. Download Nexal today.',
  applicationName: 'Nexal',
  alternates: { canonical: '/' },
  robots: { index: true, follow: true, googleBot: { index: true, follow: true, 'max-image-preview': 'large', 'max-snippet': -1, 'max-video-preview': -1 } },
  icons: { icon: '/nexal-logo.png', apple: '/nexal-logo.png' },
  openGraph: {
    title: 'Nexal: AI Workout & Meal Planner for Android',
    description: 'Personalised AI workout and meal plans, calorie and macro tracking, and progress insights in one Android app.',
    type: 'website',
    images: [{ url: '/og.png', width: 1731, height: 909, alt: 'Nexal Android fitness app with workout, meal and progress screens' }],
    url: '/',
    siteName: 'Nexal',
  },
  twitter: {
    card: 'summary_large_image',
    title: 'Nexal: AI Workout & Meal Planner for Android',
    description: 'Personalised AI workout and meal plans, calorie and macro tracking, and progress insights in one Android app.',
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
        <a className="skip-link" href="#main-content">Skip to content</a>
        <div id="main-content">{children}</div>
      </body>
    </html>
  );
}
