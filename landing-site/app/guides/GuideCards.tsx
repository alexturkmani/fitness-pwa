import GuideCard from './GuideCard';
import { guides, guideTopic } from './content';
import './guides.css';

export default function GuideCards({ exclude, category, limit = 3 }: { exclude?: string; category?: string; limit?: number }) {
  const current = guides.find(guide => guide.slug === exclude);
  const candidates = guides.filter(guide => guide.slug !== exclude && (!category || guideTopic(guide) === category));
  if (current) {
    const topic = guideTopic(current);
    candidates.sort((a, b) => Number(guideTopic(b) === topic) - Number(guideTopic(a) === topic));
    // Rotate the recommendations so later guides also receive contextual inbound links.
    const start = Math.max(0, guides.filter(guide => guideTopic(guide) === topic).findIndex(guide => guide.slug === exclude));
    const related = candidates.filter(guide => guideTopic(guide) === topic);
    const rotated = [...related.slice(start), ...related.slice(0, start)];
    candidates.splice(0, candidates.length, ...rotated, ...candidates.filter(guide => guideTopic(guide) !== topic));
  }
  return <div className="guide-cards">{candidates.slice(0, limit).map((guide, index) =>
    <GuideCard guide={guide} index={index} key={guide.slug} />)}</div>;
}
