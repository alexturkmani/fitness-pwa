'use client';
import { usePathname } from 'next/navigation';
import { ReactNode } from 'react';

// Pages that use full-width layout (no max-w-lg container)
const FULL_WIDTH_PAGES = ['/', '/login', '/register'];

export default function AppShell({ children }: { children: ReactNode }) {
  const pathname = usePathname();
  const isFullWidth = FULL_WIDTH_PAGES.includes(pathname);

  if (isFullWidth) {
    return <main>{children}</main>;
  }

  return (
    <main className="max-w-lg mx-auto px-4 pb-nav">
      {children}
    </main>
  );
}
