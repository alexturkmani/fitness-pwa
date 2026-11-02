import type { Metadata } from 'next';
import Link from 'next/link';
import { notFound } from 'next/navigation';
import { BrandMark, PlayButton, SiteFooter } from '../../components/MarketingUi';
import { seoPageMetadata, siteUrl } from '../../site-config';
import GuideCards from '../GuideCards';
import { guides, findGuide, guideDates } from '../content';

export const dynamicParams = false;
export function generateStaticParams() { return guides.map(({ slug }) => ({ slug })); }

export async function generateMetadata({ params }: { params: Promise<{ slug: string }> }): Promise<Metadata> {
  const guide = findGuide((await params).slug);
  if (!guide) return {};
  const metadata = seoPageMetadata(`/guides/${guide.slug}`, guide.metaTitle, guide.description);
  const dates = guideDates(guide);
  return { ...metadata, openGraph: { ...metadata.openGraph, type: 'article', publishedTime: dates.published, modifiedTime: dates.modified, authors: ['Nexal'] } };
}

export default async function GuidePage({ params }: { params: Promise<{ slug: string }> }) {
  const guide = findGuide((await params).slug);
  if (!guide) notFound();
  const dates = guideDates(guide);
  const url = `${siteUrl}/guides/${guide.slug}`;
  const schema = { '@context': 'https://schema.org', '@graph': [
    { '@type': 'Article', '@id': `${url}#article`, headline: guide.title, description: guide.description, mainEntityOfPage: url, datePublished: dates.published, dateModified: dates.modified, inLanguage: 'en', image: `${siteUrl}/og.png`, author: { '@type': 'Organization', name: 'Nexal', url: siteUrl }, publisher: { '@type': 'Organization', name: 'Nexal', url: siteUrl, logo: { '@type': 'ImageObject', url: `${siteUrl}/nexal-logo.png` } } },
    { '@type': 'BreadcrumbList', itemListElement: [
      { '@type': 'ListItem', position: 1, name: 'Home', item: siteUrl },
      { '@type': 'ListItem', position: 2, name: 'Guides', item: `${siteUrl}/guides` },
      { '@type': 'ListItem', position: 3, name: guide.title, item: url },
    ] },
  ] };
  return <main className="guides-page">
    <script type="application/ld+json" dangerouslySetInnerHTML={{ __html: JSON.stringify(schema).replace(/</g, '\\u003c') }} />
    <nav className="nav shell" aria-label="Main navigation"><Link href="/" className="brand" aria-label="Nexal home"><BrandMark /></Link><div className="nav-links"><Link href="/guides">All Guides</Link><Link href={guide.feature.href}>App Features</Link></div><PlayButton compact placement={`${guide.slug}_nav`} /></nav>
    <header className="guide-hero shell"><nav className="guide-breadcrumb" aria-label="Breadcrumb"><Link href="/">Home</Link><span aria-hidden="true">/</span><Link href="/guides">Guides</Link><span aria-hidden="true">/</span><span>{guide.category.toLowerCase()}</span></nav><span className="section-kicker">{guide.category}</span><h1>{guide.title}</h1><p>{guide.intro}</p><div className="guide-byline"><span>By Nexal</span><time dateTime={dates.published}>{new Intl.DateTimeFormat('en-AU', { day: 'numeric', month: 'long', year: 'numeric', timeZone: 'UTC' }).format(new Date(`${dates.published}T00:00:00Z`))}</time><span>{guide.readTime}</span></div></header>
    <div className="guide-entry-cta shell"><PlayButton placement={`${guide.slug}_intro`} /><p>Download on Android, create your free account and start tracking. AI planning is optional Premium.</p></div>
    <div className="guide-layout shell">
      <aside className="guide-sidebar"><nav aria-label="In this guide"><h2>In this guide</h2>{guide.sections.map(section => <a key={section.id} href={`#${section.id}`}>{section.title}</a>)}</nav><div className="guide-sidebar-offer"><strong>Start with free tracking.</strong><p>AI planning is optional Premium.</p><PlayButton placement={`${guide.slug}_sidebar`} /></div></aside>
      <article className="guide-article" aria-label={guide.title}>
        <div className="guide-takeaway"><span className="section-kicker">THE SHORT VERSION</span><p>{guide.takeaway}</p></div>
        {guide.sections.map(section => <section id={section.id} key={section.id}><h2>{section.title}</h2>{section.paragraphs.map(paragraph => <p key={paragraph}>{paragraph}</p>)}{section.example && <figure className="guide-example"><figcaption>{section.example.title}</figcaption><div className="guide-table-scroll" tabIndex={0} role="region" aria-label={section.example.title}><table><thead><tr>{section.example.headers.map(header => <th scope="col" key={header}>{header}</th>)}</tr></thead><tbody>{section.example.rows.map(row => <tr key={row[0]}>{row.map((cell,index) => index === 0 ? <th scope="row" key={index}>{cell}</th> : <td key={index}>{cell}</td>)}</tr>)}</tbody></table></div><p>{section.example.caption}</p></figure>}{section.bullets && <ul>{section.bullets.map(bullet => <li key={bullet}>{bullet}</li>)}</ul>}</section>)}
        <section className="guide-product"><span className="section-kicker light">PUT IT INTO PRACTICE</span><h2>{guide.feature.text}</h2><p><Link href={guide.feature.href}>{guide.feature.label} →</Link></p><PlayButton placement={`${guide.slug}_article`} /><p className="guide-product-note">Free core tracking. Premium AI tools. Local prices and eligible trial offers are shown in Google Play.</p></section>
        <section className="guide-faq"><h2>Frequently asked questions</h2>{guide.faqs.map(faq => <details key={faq.question}><summary>{faq.question}</summary><p>{faq.answer}</p></details>)}</section>
        <section className="guide-sources"><h2>Sources and editorial notes</h2><p>Prepared by Nexal for general education. This is product-team content, not an independent review or a clinically reviewed programme. Exercise and nutrition examples are illustrative, not personalised recommendations.</p><ul>{guide.sources.map(source => <li key={source.href}><a href={source.href}>{source.label}</a></li>)}</ul><p>App feature availability and subscription offers can change. Check the current Play listing and checkout before subscribing.</p></section>
      </article>
    </div>
    <section className="guide-related shell"><span className="section-kicker">KEEP LEARNING</span><h2>More practical guides</h2><GuideCards exclude={guide.slug} /></section>
    <SiteFooter />
  </main>;
}
