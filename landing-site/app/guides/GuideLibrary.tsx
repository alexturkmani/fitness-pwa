'use client';

import { useRef, useState } from 'react';
import GuideCard, { type GuideSummary } from './GuideCard';
import { searchGuides } from './search-guides';

const categories = ['CHOOSING AN APP', 'WORKOUT PLANNING', 'NUTRITION TRACKING'];
const anchor = (category: string) => category.toLowerCase().replace(/[^a-z0-9]+/g, '-');

export default function GuideLibrary({ guides }: { guides: GuideSummary[] }) {
  const [query, setQuery] = useState('');
  const searchInput = useRef<HTMLInputElement>(null);
  const matches = searchGuides(guides, query);
  return <>
    <div className="guide-search">
      <label htmlFor="guide-query">Find a guide for your next step</label>
      <div className="guide-search-controls"><input ref={searchInput} id="guide-query" type="search" value={query} onChange={event => setQuery(event.target.value)} placeholder="Try dumbbells, food labels or subscriptions" aria-describedby="guide-search-count" maxLength={120} />{query && <button type="button" onClick={() => { setQuery(''); searchInput.current?.focus(); }}>Clear search</button>}</div>
      <p id="guide-search-count" role="status" aria-live="polite">{matches.length} {matches.length === 1 ? 'guide' : 'guides'}{query.trim() ? ' matching your search' : ' to explore'}</p>
    </div>
    <nav className="guide-topics" aria-label="Guide topics">{categories.map(category => <a key={category} href={`#${anchor(category)}`}>{category.toLowerCase()} <span>{matches.filter(guide => guide.topic === category).length}</span></a>)}</nav>
    {matches.length === 0 && <p className="guide-search-empty">No guides match that search. Try a shorter phrase, such as “workout” or “meal”, or clear your search to browse every topic.</p>}
    {categories.map(category => {
      const group = matches.filter(guide => guide.topic === category);
      return <section className="guide-category" id={anchor(category)} key={category}>
        <span className="section-kicker">PRACTICAL FITNESS GUIDES</span><h2>{category.toLowerCase()}</h2>
        {group.length ? <div className="guide-cards">{group.map((guide, index) => <GuideCard guide={guide} index={index} key={guide.slug} />)}</div> : <p className="guide-note">No matching guides in this topic.</p>}
      </section>;
    })}
  </>;
}
