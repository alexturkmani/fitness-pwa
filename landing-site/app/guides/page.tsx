import Link from 'next/link';
import { BrandMark, PlayButton, SiteFooter } from '../components/MarketingUi';
import { seoPageMetadata } from '../site-config';
import GuideLibrary from './GuideLibrary';
import { guides, guideTopic } from './content';
import './guides.css';

export const metadata = seoPageMetadata('/guides', 'Workout & Macro Tracking Guides for Android', 'Practical workout planning, calorie and macro tracking guides. Learn with worked examples and explore free tracking in Nexal for Android.');

export default function GuidesPage() {
  const summaries = guides.map(({ slug, title, description, category }) => ({ slug, title, description, category, topic: guideTopic({ category }) }));
  return <main className="guides-page">
    <nav className="nav shell" aria-label="Main navigation"><Link href="/" className="brand" aria-label="Nexal home"><BrandMark /></Link><div className="nav-links"><Link href="/ai-workout-planner">AI Workouts</Link><Link href="/calorie-macro-tracker">Macro Tracking</Link></div><PlayButton compact placement="guides_nav" /></nav>
    <header className="guide-hub-hero shell"><span className="section-kicker">LEARN. PLAN. TRACK.</span><h1>Build a routine.<br /><em>Understand the details.</em></h1><p>Practical guides to organising workouts, logging meals and choosing tools that fit your life. Clear examples, honest feature boundaries and no promises of overnight results.</p><PlayButton placement="guides_hero" /></header>
    <div className="guide-hub-list shell"><GuideLibrary guides={summaries} /><p className="guide-note">Published by Nexal. General educational information, not individual medical, nutrition or exercise advice.</p></div>
    <SiteFooter />
  </main>;
}
