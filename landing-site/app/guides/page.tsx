import Link from 'next/link';
import { BrandMark, PlayButton, SiteFooter } from '../components/MarketingUi';
import { seoPageMetadata, siteUrl } from '../site-config';
import { breadcrumbNode, graph, organizationNode, webPageNode, websiteNode } from '../schema';
import JsonLd from '../components/JsonLd';
import GuideLibrary from './GuideLibrary';
import { guides, guideTopic, libraryUpdatedDate } from './content';
import './guides.css';

const page = { path: '/guides', title: 'Workout & Macro Tracking Guides for Android', description: 'Practical workout planning, calorie and macro tracking guides. Learn with worked examples and explore free tracking in Nexal for Android.' };
export const metadata = seoPageMetadata(page.path, page.title, page.description);

export default function GuidesPage() {
  const summaries = guides.map(({ slug, title, description, category }) => ({ slug, title, description, category, topic: guideTopic({ category }) }));
  const schema = graph(
    organizationNode(),
    websiteNode(),
    webPageNode(page.path, page.title, page.description, {
      '@type': 'CollectionPage',
      dateModified: libraryUpdatedDate,
      mainEntity: { '@type': 'ItemList', numberOfItems: guides.length, itemListElement: guides.map((guide, index) => ({ '@type': 'ListItem', position: index + 1, url: `${siteUrl}/guides/${guide.slug}`, name: guide.title })) },
    }),
    breadcrumbNode(page.path, [{ name: 'Home', path: '/' }, { name: 'Guides', path: page.path }]),
  );
  return <main className="guides-page">
    <JsonLd data={schema} />
    <nav className="nav shell" aria-label="Main navigation"><Link href="/" className="brand" aria-label="Nexal home"><BrandMark /></Link><div className="nav-links"><Link href="/ai-workout-planner">AI Workouts</Link><Link href="/calorie-macro-tracker">Macro Tracking</Link></div><PlayButton compact placement="guides_nav" /></nav>
    <header className="guide-hub-hero shell"><span className="section-kicker">LEARN. PLAN. TRACK.</span><h1>Build a routine.<br /><em>Understand the details.</em></h1><p>Practical guides to organising workouts, logging meals and choosing tools that fit your life. Clear examples, honest feature boundaries and no promises of overnight results.</p><PlayButton placement="guides_hero" /></header>
    <div className="guide-hub-list shell"><GuideLibrary guides={summaries} /><p className="guide-note">Published by Nexal. General educational information, not individual medical, nutrition or exercise advice.</p></div>
    <SiteFooter />
  </main>;
}
