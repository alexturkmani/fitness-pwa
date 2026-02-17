'use client';
import { usePathname } from 'next/navigation';
import Link from 'next/link';
import { LayoutDashboard, Dumbbell, ScanLine, Apple, BarChart3 } from 'lucide-react';

const tabs = [
  { href: '/', icon: LayoutDashboard, label: 'Home' },
  { href: '/workouts', icon: Dumbbell, label: 'Workouts' },
  { href: '/scanner', icon: ScanLine, label: 'Scan' },
  { href: '/nutrition', icon: Apple, label: 'Nutrition' },
  { href: '/progress', icon: BarChart3, label: 'Progress' },
];

export default function BottomNav() {
  const pathname = usePathname();

  if (pathname === '/onboarding' || pathname === '/login' || pathname === '/register' || pathname === '/paywall') return null;

  return (
    <nav className="fixed bottom-0 left-0 right-0 z-40 bg-white/95 backdrop-blur-lg border-t border-dark-700/50 shadow-[0_-1px_3px_rgba(0,0,0,0.05)]">
      <div className="flex items-center justify-around max-w-lg mx-auto px-2 py-2">
        {tabs.map((tab) => {
          const isActive = pathname === tab.href;
          const isScanner = tab.href === '/scanner';
          const Icon = tab.icon;

          return (
            <Link
              key={tab.href}
              href={tab.href}
              className={`flex flex-col items-center gap-1 px-3 py-1.5 rounded-xl transition-all duration-200 ${
                isScanner
                  ? 'relative -mt-5 bg-gradient-to-r from-primary-500 to-primary-600 text-white p-3 rounded-2xl shadow-lg shadow-primary-500/30'
                  : isActive
                  ? 'text-primary-400'
                  : 'text-dark-500 hover:text-dark-300'
              }`}
            >
              <Icon size={isScanner ? 24 : 20} />
              <span className={`text-xs ${isScanner ? 'text-white' : ''}`}>{tab.label}</span>
            </Link>
          );
        })}
      </div>
    </nav>
  );
}
