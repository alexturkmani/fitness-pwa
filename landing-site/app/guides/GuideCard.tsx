import Link from 'next/link';

export type GuideSummary = { slug: string; title: string; description: string; category: string; topic: string };

export default function GuideCard({ guide, index }: { guide: Omit<GuideSummary, 'topic'>; index: number }) {
  return <Link prefetch={false} className="guide-card" href={`/guides/${guide.slug}`}>
    <span className="guide-card-index" aria-hidden="true">{String(index + 1).padStart(2, '0')}</span>
    <span className="section-kicker">{guide.category}</span>
    <h3>{guide.title}</h3><p>{guide.description}</p>
    <span className="guide-card-link">Read the guide <span aria-hidden="true">→</span></span>
  </Link>;
}
