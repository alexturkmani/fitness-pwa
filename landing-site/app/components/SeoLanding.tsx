import Link from 'next/link';
import '../seo-landing.css';
import { appSchema } from '../site-config';
import { BrandMark, PlayButton, SiteFooter } from './MarketingUi';
import GuideCards from '../guides/GuideCards';

type Props = {
  slug: string; kicker: string; title: string; intro: string; sectionTitle: string;
  benefits: { title: string; text: string }[];
  sections: { title: string; text: string }[];
  faqs: { q: string; a: string }[];
};

const previews: Record<string, { label: string; heading: string; rows: { day: string; type: string; name: string; detail: string }[] }> = {
  ai_workout_planner: { label: 'YOUR TRAINING WEEK', heading: 'A plan that fits your life.', rows: [
    { day: 'MON', type: 'STRENGTH', name: 'Upper body', detail: '32 min' },
    { day: 'WED', type: 'RECOVERY', name: 'Mobility flow', detail: '18 min' },
    { day: 'FRI', type: 'STRENGTH', name: 'Lower body', detail: '40 min' },
  ] },
  ai_meal_planner: { label: 'YOUR DAILY MEALS', heading: 'Nutrition, made clearer.', rows: [
    { day: 'AM', type: 'BREAKFAST', name: 'Protein oat bowl', detail: '420 kcal' },
    { day: 'PM', type: 'LUNCH', name: 'Balanced lunch', detail: '610 kcal' },
    { day: 'EVE', type: 'DINNER', name: 'Dinner plan', detail: '540 kcal' },
  ] },
  calorie_macro_tracker: { label: 'TODAY AT A GLANCE', heading: 'Know your targets.', rows: [
    { day: 'P', type: 'PROTEIN', name: '124 of 150 g', detail: '83%' },
    { day: 'C', type: 'CARBS', name: '184 of 250 g', detail: '74%' },
    { day: 'F', type: 'FATS', name: '51 of 75 g', detail: '68%' },
  ] },
  workout_meal_planner: { label: 'YOUR DAY IN NEXAL', heading: 'Everything connects.', rows: [
    { day: '01', type: 'WORKOUT', name: 'Upper body strength', detail: '32 min' },
    { day: '02', type: 'NUTRITION', name: 'Balanced meal plan', detail: 'View' },
    { day: '03', type: 'PROGRESS', name: 'Review your week', detail: 'View' },
  ] },
};

const featureDetails: Record<string, { heading: string; intro: string; points: { title: string; text: string }[] }> = {
  ai_workout_planner: {
    heading: 'AI workout planner and tracker in one Android app',
    intro: 'A generated plan gives you direction. A workout tracker shows what you actually completed. Nexal combines both, so your training history stays connected to your next session.',
    points: [
      { title: 'Build a 4–6 week plan', text: 'Nexal Premium creates gym or home programmes around your goal, experience and 3–7 available training days per week.' },
      { title: 'See the session details', text: 'Plans include exercises with sets, reps and rest times. Choose a split that fits you, including push/pull/legs or muscle-group training.' },
      { title: 'Track the work you do', text: 'Log workouts and review your history and progress charts. Core workout tracking is free, while AI plan generation is a Premium feature.' },
    ],
  },
  ai_meal_planner: {
    heading: 'AI meal plans built around calories and macros',
    intro: 'Turn a daily nutrition target into meal ideas you can use. Nexal Premium plans for breakfast, lunch, dinner and snacks while keeping your training and food diary together.',
    points: [
      { title: 'Start with your targets', text: 'Meal ideas are shaped by your calorie and macro goals, food preferences, dietary restrictions and allergies.' },
      { title: 'Understand each meal', text: 'See nutritional breakdowns for suggested meals and use smart substitutions when a choice does not suit you.' },
      { title: 'Keep logging simple', text: 'Track meals with free core tools. Premium adds AI macro estimates and a barcode scanner for packaged foods.' },
    ],
  },
};

function SeoShowcase({ slug }: { slug: string }) {
  const preview = previews[slug];
  return <div className="seo-showcase" role="img" aria-label={`Illustrative Nexal app preview: ${preview.heading}`}>
    <div className="seo-showcase-orbit" aria-hidden="true" />
    <div className="seo-preview-card">
      <div className="seo-preview-top"><span>NEXAL <b>✦</b></span><i>YOUR PLAN</i></div>
      <span className="seo-preview-label">{preview.label}</span>
      <h2>{preview.heading}</h2>
      <div className="seo-preview-rows">{preview.rows.map((row) => <div className="seo-preview-row" key={row.name}>
        <span className="seo-preview-day">{row.day}</span><span className="seo-preview-row-copy"><small>{row.type}</small><strong>{row.name}</strong></span><em>{row.detail}</em>
      </div>)}</div>
      <div className="seo-preview-progress"><span>KEEP YOUR MOMENTUM</span><div className="seo-preview-bars" aria-hidden="true">{[35, 58, 47, 77, 64, 91, 76].map((height, index) => <i key={index} style={{ height: `${height}%` }} />)}</div></div>
    </div>
    <div className="seo-showcase-chip seo-showcase-chip-top"><span>✓</span> {slug === 'calorie_macro_tracker' ? 'Targets at a glance' : 'Plan ready for today'}</div>
    <div className="seo-showcase-chip seo-showcase-chip-bottom"><span>↗</span> Progress in one view</div>
  </div>;
}

export default function SeoLanding({ slug, kicker, title, intro, sectionTitle, benefits, sections, faqs }: Props) {
  return <main className="seo-page" id="top">
    <script type="application/ld+json" dangerouslySetInnerHTML={{ __html: JSON.stringify(appSchema) }} />
    <nav className="nav shell"><Link href="/" className="brand" aria-label="Nexal home"><BrandMark /></Link><div className="nav-links"><Link href="/">Home</Link><a href="#benefits">Benefits</a><a href="#details">Details</a><a href="#faq">Questions</a></div><PlayButton compact placement={`${slug}_nav`} /></nav>
    <header className="seo-hero shell"><div className="seo-hero-copy"><span className="eyebrow">{kicker}</span><h1>{title}</h1><p>{intro}</p><div className="hero-actions"><PlayButton placement={`${slug}_hero`} /><a className="text-link" href="#benefits">Explore the benefits <span>↓</span></a></div><div className="trust-row"><p>Free core tracking. Premium AI planning. Available on Android.</p></div></div><SeoShowcase slug={slug} /></header>
    <div className="signal-strip" role="region" aria-label="Nexal benefits"><div className="shell"><span>◆ PERSONALIZED DAILY TARGETS</span><span>◆ AI-POWERED PLANS</span><span>◆ MEANINGFUL PROGRESS</span></div></div>
    <section className="seo-benefits shell" id="benefits"><div className="section-heading"><div><span className="section-kicker">BUILT AROUND YOUR ROUTINE</span><h2>{sectionTitle}</h2></div><p>Helpful tools for your daily fitness and nutrition decisions, all in one Android app.</p></div><div className="seo-benefit-grid">{benefits.map((benefit, index) => <article className={`seo-benefit-card seo-benefit-${index + 1}`} key={benefit.title}><span className="card-number">0{index + 1}</span><div className="seo-benefit-icon" aria-hidden="true">{index === 0 ? '◎' : index === 1 ? '✦' : '↗'}</div><h3>{benefit.title}</h3><p>{benefit.text}</p><div className="seo-benefit-meter" aria-hidden="true"><i /></div></article>)}</div></section>
    {featureDetails[slug] && <section className="seo-feature-details shell"><div className="seo-feature-intro"><span className="section-kicker">WHAT YOU CAN ACTUALLY DO</span><h2>{featureDetails[slug].heading}</h2><p>{featureDetails[slug].intro}</p></div><div className="seo-feature-steps">{featureDetails[slug].points.map((point, index) => <article key={point.title}><span>0{index + 1}</span><h3>{point.title}</h3><p>{point.text}</p></article>)}</div><div className="seo-feature-action"><PlayButton placement={`${slug}_feature_details`} /></div></section>}
    <section className="seo-details" id="details"><div className="shell seo-details-grid"><div className="seo-details-copy"><span className="section-kicker light">PLAN. TRACK. PROGRESS.</span><h2>A clearer way to<br /><em>keep moving.</em></h2><p>Your plan and the actions you take belong together. Keep the details visible without losing sight of the bigger picture.</p><PlayButton placement={`${slug}_details`} /></div><div className="seo-detail-panel"><div className="seo-detail-panel-head"><span>ILLUSTRATIVE APP OVERVIEW</span><strong>Your momentum</strong></div><div className="seo-detail-chart" role="img" aria-label="Illustrative weekly progress chart"><div className="seo-detail-grid"/><div className="seo-detail-bars">{[42, 57, 48, 71, 64, 83, 92].map((height,index)=><i key={index} style={{height:`${height}%`}} />)}</div></div><div className="seo-detail-labels"><span>MON</span><span>TUE</span><span>WED</span><span>THU</span><span>FRI</span><span>SAT</span><span>SUN</span></div><div className="seo-detail-caption"><span>Training</span><span>Nutrition</span><span>Progress</span></div></div></div></section>
    <section className="seo-copy shell"><span className="section-kicker">THE DETAILS</span><div className="seo-copy-grid">{sections.map((section, index) => <article key={section.title}><span className="seo-copy-number">0{index + 1}</span><h3>{section.title}</h3><p>{section.text}</p></article>)}</div></section>
    <section className="faq-section seo-faq shell" id="faq"><div><span className="section-kicker">THE DETAILS</span><h2>Nexal app<br /><em>questions.</em></h2></div><div className="faq-list">{faqs.map((faq, index) => <details key={faq.q} open={index === 0}><summary>{faq.q}<span>+</span></summary><p>{faq.a}</p></details>)}</div></section>
    <section className="final-cta seo-final"><div className="shell"><BrandMark /><h2>Build a routine<br />you can <em>follow.</em></h2><p>Start with free tracking. Unlock AI planning when you are ready.</p><PlayButton placement={`${slug}_bottom`} /><div className="seo-links"><Link href="/ai-workout-planner">AI workout planner</Link><Link href="/ai-meal-planner">AI meal planner</Link><Link href="/workout-meal-planner-app">Workout and meal planner</Link><Link href="/calorie-macro-tracker">Calorie and macro tracker</Link></div></div></section>
    <section className="home-guides shell" aria-labelledby="related-guides-heading"><span className="section-kicker">PUT THE DETAILS INTO PRACTICE</span><h2 id="related-guides-heading">Workout and nutrition guides</h2><GuideCards category={slug === 'ai_workout_planner' ? 'WORKOUT PLANNING' : slug === 'workout_meal_planner_app' ? 'CHOOSING AN APP' : 'NUTRITION TRACKING'} /><Link className="text-link" href="/guides">Explore all guides →</Link></section>
    <SiteFooter />
  </main>;
}
