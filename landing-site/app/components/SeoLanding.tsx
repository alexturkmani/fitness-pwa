import Link from 'next/link';
import '../seo-landing.css';

const playUrl = 'https://play.google.com/store/apps/details?id=com.nexal.app';

type Props = {
  kicker: string; title: string; intro: string;
  benefits: { title: string; text: string }[];
  sections: { title: string; text: string }[];
  faqs: { q: string; a: string }[];
};

export default function SeoLanding({ kicker, title, intro, benefits, sections, faqs }: Props) {
  const schema = {
    '@context': 'https://schema.org', '@type': 'SoftwareApplication', name: 'Nexal',
    applicationCategory: 'HealthApplication', operatingSystem: 'Android',
    description: intro, url: 'https://www.nexalfitness.com',
    downloadUrl: playUrl, offers: { '@type': 'Offer', price: '0', priceCurrency: 'USD' },
  };
  const faqSchema = { '@context': 'https://schema.org', '@type': 'FAQPage', mainEntity: faqs.map(({q,a})=>({ '@type':'Question', name:q, acceptedAnswer:{'@type':'Answer',text:a} })) };
  return <main className="seo-page">
    <script type="application/ld+json" dangerouslySetInnerHTML={{__html:JSON.stringify(schema)}} />
    <script type="application/ld+json" dangerouslySetInnerHTML={{__html:JSON.stringify(faqSchema)}} />
    <nav className="seo-nav shell"><Link href="/"><img src="/nexal-horizontal.png" alt="Nexal" /></Link><a className="seo-cta" href={playUrl}>Get Nexal free</a></nav>
    <header className="seo-hero shell"><span className="seo-kicker">{kicker}</span><h1>{title}</h1><p>{intro}</p><a className="seo-cta" href={playUrl}>Download on Google Play</a></header>
    <section className="seo-proof shell">{benefits.map((b,i)=><article key={b.title}><span className="seo-kicker">0{i+1}</span><h2>{b.title}</h2><p>{b.text}</p></article>)}</section>
    <section className="seo-copy"><div className="shell"><h2>Fitness guidance that fits real life.</h2><div className="seo-copy-grid">{sections.map(s=><article key={s.title}><h3>{s.title}</h3><p>{s.text}</p></article>)}</div></div></section>
    <section className="seo-faq"><div className="shell"><h2>Questions, answered.</h2>{faqs.map(f=><details key={f.q}><summary>{f.q}</summary><p>{f.a}</p></details>)}</div></section>
    <section className="seo-bottom"><h2>Start building your plan.</h2><p>Track for free and unlock personalized AI planning when you are ready.</p><a className="seo-cta" href={playUrl}>Get Nexal on Android</a><div className="seo-links"><Link href="/ai-workout-planner">AI workout planner</Link><Link href="/ai-meal-planner">AI meal planner</Link><Link href="/workout-meal-planner-app">Workout & meal planner</Link><Link href="/calorie-macro-tracker">Calorie & macro tracker</Link></div></section>
  </main>;
}
