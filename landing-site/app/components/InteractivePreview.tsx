'use client';

import { useState } from 'react';

/**
 * Tappable, illustrative app previews for the landing-page hero. Server and
 * client render the same initial state, and every mode keeps a fixed number of
 * rows, so hydrating or tapping never changes the card's height.
 */

type Meal = { badge: string; type: string; name: string; kcal: number; protein: number; carbs: number; fat: number };
type WorkoutSet = { badge: string; type: string; name: string; kg: number; reps: number };
type Row = { badge: string; type: string; name: string; detail: string };

export type PreviewConfig =
  | { mode: 'meals'; label: string; heading: string; meals: Meal[]; targets: { kcal: number; protein: number; carbs: number; fat: number }; initiallyLogged: number[] }
  | { mode: 'sets'; label: string; heading: string; sets: WorkoutSet[]; initiallyDone: number[]; streakDays: number }
  | { mode: 'tabs'; label: string; tabs: { id: string; label: string; heading: string; rows: Row[] }[] };

const pct = (value: number, target: number) => Math.min(100, Math.round((value / target) * 100));

function Frame({ label, heading, children, chip }: { label: string; heading: string; children: React.ReactNode; chip: string }) {
  return <section className="seo-showcase" aria-label="Illustrative Nexal app preview. Tap the rows to try it.">
    <div className="seo-showcase-orbit" aria-hidden="true" />
    <div className="seo-preview-card seo-preview-live">
      <div className="seo-preview-top"><span>NEXAL <b>✦</b></span><i>TRY IT</i></div>
      <span className="seo-preview-label">{label}</span>
      <h2>{heading}</h2>
      {children}
    </div>
    <div className="seo-showcase-chip seo-showcase-chip-top" aria-hidden="true"><span>✓</span> {chip}</div>
    <div className="seo-showcase-chip seo-showcase-chip-bottom" aria-hidden="true"><span>↗</span> Illustrative example</div>
  </section>;
}

function MealsPreview({ config }: { config: Extract<PreviewConfig, { mode: 'meals' }> }) {
  const [logged, setLogged] = useState(() => new Set(config.initiallyLogged));
  const totals = config.meals.reduce((sum, meal, index) => logged.has(index)
    ? { kcal: sum.kcal + meal.kcal, protein: sum.protein + meal.protein, carbs: sum.carbs + meal.carbs, fat: sum.fat + meal.fat }
    : sum, { kcal: 0, protein: 0, carbs: 0, fat: 0 });
  const toggle = (index: number) => setLogged(previous => { const next = new Set(previous); if (next.has(index)) next.delete(index); else next.add(index); return next; });
  const macros = [
    { name: 'Protein', value: totals.protein, target: config.targets.protein },
    { name: 'Carbs', value: totals.carbs, target: config.targets.carbs },
    { name: 'Fat', value: totals.fat, target: config.targets.fat },
  ];
  return <Frame label={config.label} heading={config.heading} chip="Free core logging">
    <div className="seo-preview-rows">{config.meals.map((meal, index) => <button type="button" className="seo-preview-row" aria-pressed={logged.has(index)} onClick={() => toggle(index)} key={meal.name}>
      <span className="seo-preview-day">{logged.has(index) ? '✓' : meal.badge}</span><span className="seo-preview-row-copy"><small>{meal.type}</small><strong>{meal.name}</strong></span><em>{logged.has(index) ? `${meal.kcal} kcal` : 'Tap to log'}</em>
    </button>)}</div>
    <div className="seo-preview-progress" aria-live="polite">
      <span>{totals.kcal} OF {config.targets.kcal} KCAL LOGGED</span>
      <div className="seo-preview-macros">{macros.map(macro => <div key={macro.name}><span>{macro.name}</span><b>{macro.value} / {macro.target} g</b><i style={{ '--bar': `${pct(macro.value, macro.target)}%` } as React.CSSProperties} /></div>)}</div>
    </div>
  </Frame>;
}

function SetsPreview({ config }: { config: Extract<PreviewConfig, { mode: 'sets' }> }) {
  const [done, setDone] = useState(() => new Set(config.initiallyDone));
  const toggle = (index: number) => setDone(previous => { const next = new Set(previous); if (next.has(index)) next.delete(index); else next.add(index); return next; });
  const volume = config.sets.reduce((sum, set, index) => done.has(index) ? sum + set.kg * set.reps : sum, 0);
  return <Frame label={config.label} heading={config.heading} chip={`${config.streakDays} day streak`}>
    <div className="seo-preview-rows">{config.sets.map((set, index) => <button type="button" className="seo-preview-row" aria-pressed={done.has(index)} onClick={() => toggle(index)} key={`${set.name}-${set.badge}`}>
      <span className="seo-preview-day">{done.has(index) ? '✓' : set.badge}</span><span className="seo-preview-row-copy"><small>{set.type}</small><strong>{set.name}</strong></span><em>{set.kg} kg × {set.reps}</em>
    </button>)}</div>
    <div className="seo-preview-progress" aria-live="polite">
      <span>{done.size} OF {config.sets.length} SETS DONE</span>
      <div className="seo-preview-stat"><strong>{volume.toLocaleString('en-US')} kg</strong><small>session volume (kg × reps)</small></div>
    </div>
  </Frame>;
}

function TabsPreview({ config }: { config: Extract<PreviewConfig, { mode: 'tabs' }> }) {
  const [active, setActive] = useState(config.tabs[0].id);
  const tab = config.tabs.find(candidate => candidate.id === active) ?? config.tabs[0];
  return <Frame label={config.label} heading={tab.heading} chip="Training + nutrition">
    <div className="seo-preview-tabs" role="tablist" aria-label="Preview section">{config.tabs.map(candidate => <button type="button" role="tab" id={`preview-tab-${candidate.id}`} aria-controls="preview-panel" aria-selected={candidate.id === active} key={candidate.id} onClick={() => setActive(candidate.id)}>{candidate.label}</button>)}</div>
    <div className="seo-preview-rows" role="tabpanel" id="preview-panel" aria-labelledby={`preview-tab-${tab.id}`}>{tab.rows.map(row => <div className="seo-preview-row" key={row.name}>
      <span className="seo-preview-day">{row.badge}</span><span className="seo-preview-row-copy"><small>{row.type}</small><strong>{row.name}</strong></span><em>{row.detail}</em>
    </div>)}</div>
  </Frame>;
}

export default function InteractivePreview({ config }: { config: PreviewConfig }) {
  if (config.mode === 'meals') return <MealsPreview config={config} />;
  if (config.mode === 'sets') return <SetsPreview config={config} />;
  return <TabsPreview config={config} />;
}
