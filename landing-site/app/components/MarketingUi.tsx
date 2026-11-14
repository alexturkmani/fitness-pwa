import Image from 'next/image';
import Link from 'next/link';
import { playStoreUrl } from '../site-config';

/**
 * Nav copies load eagerly at low priority so they never compete with the hero
 * (React would otherwise emit a preload for every non-lazy image). Pass `lazy`
 * for copies below the fold (final CTA, footer).
 */
export function BrandMark({ lazy = false }: { lazy?: boolean }) {
  return <span className="brand-mark" aria-hidden="true"><Image src="/nexal-horizontal.png" width={158} height={48} alt="" loading={lazy ? 'lazy' : 'eager'} fetchPriority={lazy ? undefined : 'low'} /></span>;
}

function GooglePlayMark() {
  return <svg className="google-play-mark" viewBox="0 0 32 36" aria-hidden="true">
    <path fill="#00d7fe" d="M1.8 1.6 18.9 18 1.9 34.5A4 4 0 0 1 .7 31.6V4.4c0-1 .4-2 1.1-2.8Z"/>
    <path fill="#00f076" d="m18.9 18 5.4-5.2L5.2 1.9C4 1.2 2.8 1 1.8 1.6L18.9 18Z"/>
    <path fill="#ffdc00" d="m18.9 18-17 16.5c1 .6 2.2.4 3.4-.3l19-10.9-5.4-5.3Z"/>
    <path fill="#ff3a44" d="m29.4 15.7-5.1-2.9-5.4 5.2 5.4 5.3 5.1-3c2-1.1 2-3.4 0-4.6Z"/>
  </svg>;
}

export function PlayButton({ compact = false, placement }: { compact?: boolean; placement: string }) {
  return <a className={`play-button ${compact ? 'compact' : ''}`} href={playStoreUrl(placement)} aria-label="Get Nexal on Google Play"><GooglePlayMark/><span><small>GET IT ON</small><strong>Google Play</strong></span></a>;
}

/** Product and comparison pages, shared by the footer and landing-page cross-links. */
export const productLinks = [
  { href: '/workout-meal-planner-app', label: 'Workout and meal planner app', short: 'Workout and meal planner' },
  { href: '/ai-workout-planner', label: 'AI workout planner for Android', short: 'AI workout planner' },
  { href: '/ai-meal-planner', label: 'AI meal planner', short: 'AI meal planner' },
  { href: '/calorie-macro-tracker', label: 'Calorie and macro tracker', short: 'Calorie and macro tracker' },
  { href: '/free-calorie-and-macro-tracker', label: 'Free calorie and macro tracker', short: 'Free macro tracker' },
  { href: '/free-workout-tracker-app', label: 'Free workout tracker and gym log', short: 'Free workout tracker' },
  { href: '/compare/myfitnesspal-alternative', label: 'Nexal vs MyFitnessPal', short: 'MyFitnessPal alternative' },
  { href: '/compare/fitbod-alternative', label: 'Nexal vs Fitbod', short: 'Fitbod alternative' },
];

export function SiteFooter() {
  return <footer><div className="shell"><Link href="/" className="brand" aria-label="Nexal home"><BrandMark lazy /></Link><p>Fitness, nutrition and progress, connected.</p><div>{productLinks.map(link => <Link key={link.href} href={link.href}>{link.label}</Link>)}<Link href="/guides">Workout and macro tracking guides</Link><Link href="/about">About Nexal and our guides</Link><a href={playStoreUrl('footer')}>Get Nexal on Google Play</a><a href="mailto:support@nexal.app">Support</a><Link href="/privacy">Privacy</Link></div><div className="footer-meta"><span>© 2026 Nexal</span><a className="designer-credit" href="https://blumint.com.au/" target="_blank" rel="noopener noreferrer">Designed by Blu Mint</a></div></div></footer>;
}
