import type { Metadata, Viewport } from 'next';
import './globals.css';
import BottomNav from '@/components/layout/BottomNav';
import AuthProvider from '@/components/AuthProvider';
import AppShell from '@/components/layout/AppShell';

export const metadata: Metadata = {
  title: 'FitMate - AI Fitness Coach',
  description: 'AI-powered fitness and nutrition tracking',
  manifest: '/manifest.json',
  appleWebApp: {
    capable: true,
    statusBarStyle: 'black-translucent',
    title: 'FitMate',
  },
};

export const viewport: Viewport = {
  themeColor: '#ffffff',
  width: 'device-width',
  initialScale: 1,
  maximumScale: 1,
  userScalable: false,
};

export default function RootLayout({ children }: { children: React.ReactNode }) {
  return (
    <html lang="en">
      <head>
        <link rel="apple-touch-icon" href="/icons/icon-192x192.png" />
      </head>
      <body className="animated-bg min-h-screen">
        <AuthProvider>
          <AppShell>
            {children}
          </AppShell>
          <BottomNav />
        </AuthProvider>
        <script
          dangerouslySetInnerHTML={{
            __html: `
              if ('serviceWorker' in navigator) {
                window.addEventListener('load', () => {
                  navigator.serviceWorker.register('/sw.js').catch(() => {});
                });
              }
            `,
          }}
        />
      </body>
    </html>
  );
}
