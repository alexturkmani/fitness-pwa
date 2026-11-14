import assert from 'node:assert/strict';

// Crawls every sitemap URL and checks the JSON-LD graph: valid JSON, one graph
// per page, resolvable @id references, and page-type specific requirements.
const base = (process.env.SEO_CHECK_URL || 'http://localhost:3100').replace(/\/$/, '');
const origin = 'https://www.nexalfitness.com';
const landing = ['/', '/workout-meal-planner-app', '/ai-workout-planner', '/ai-meal-planner', '/calorie-macro-tracker', '/free-calorie-and-macro-tracker', '/free-workout-tracker-app', '/compare/myfitnesspal-alternative', '/compare/fitbod-alternative'];
const decode = value => value.replace(/&amp;/g, '&').replace(/&#x27;/g, "'").replace(/&quot;/g, '"').replace(/&lt;/g, '<').replace(/&gt;/g, '>');
const types = node => [node['@type']].flat();
const isDate = value => typeof value === 'string' && /^\d{4}-\d{2}-\d{2}$/.test(value) && !Number.isNaN(Date.parse(value));

const sitemap = await (await fetch(`${base}/sitemap.xml`)).text();
const entries = [...sitemap.matchAll(/<url>(.*?)<\/url>/gs)].map(([, block]) => ({
  loc: block.match(/<loc>(.*?)<\/loc>/)[1],
  lastmod: block.match(/<lastmod>(.*?)<\/lastmod>/)?.[1],
  priority: Number(block.match(/<priority>(.*?)<\/priority>/)?.[1]),
}));
assert.ok(entries.length >= 60, `sitemap has core pages and all guides (${entries.length})`);
for (const entry of entries) {
  assert.ok(entry.lastmod && !Number.isNaN(Date.parse(entry.lastmod)), `${entry.loc}: lastmod`);
  const path = new URL(entry.loc).pathname;
  if (path === '/') assert.equal(entry.priority, 1, 'home priority');
  else if (landing.slice(1, 4).includes(path)) assert.equal(entry.priority, 0.9, `${path}: priority`);
  else if (path.startsWith('/guides/')) assert.equal(entry.priority, 0.7, `${path}: priority`);
}

for (const { loc } of entries) {
  const path = new URL(loc).pathname;
  const html = await (await fetch(`${base}${path}`)).text();
  const blocks = [...html.matchAll(/<script type="application\/ld\+json">(.*?)<\/script>/gs)].map(match => match[1]);
  assert.equal(blocks.length, 1, `${path}: exactly one JSON-LD block`);
  let data;
  assert.doesNotThrow(() => { data = JSON.parse(blocks[0]); }, `${path}: JSON-LD parses`);
  assert.equal(data['@context'], 'https://schema.org', `${path}: context`);
  const nodes = data['@graph'];
  assert.ok(Array.isArray(nodes) && nodes.length > 0, `${path}: graph`);
  const byId = new Map(nodes.filter(node => node['@id']).map(node => [node['@id'], node]));
  assert.equal(byId.size, nodes.filter(node => node['@id']).length, `${path}: unique @id`);
  // Every bare {"@id"} reference must resolve inside the page graph.
  const walk = value => {
    if (Array.isArray(value)) return value.forEach(walk);
    if (!value || typeof value !== 'object') return;
    const keys = Object.keys(value);
    if (keys.length === 1 && keys[0] === '@id') assert.ok(byId.has(value['@id']), `${path}: dangling ref ${value['@id']}`);
    for (const key of keys) if (key !== '@id') walk(value[key]);
  };
  nodes.forEach(walk);
  for (const node of nodes) for (const [key, value] of Object.entries(node)) {
    if (/^(url|item|mainEntityOfPage)$/.test(key) && typeof value === 'string') assert.ok(value.startsWith(origin) || value.startsWith('https://play.google.com/'), `${path}: absolute ${key} ${value}`);
  }
  const page = nodes.find(node => node['@id'] === `${origin}${path === '/' ? '/' : path}#webpage`);
  assert.ok(page, `${path}: page node`);
  assert.equal(page.url, loc, `${path}: page url matches canonical`);

  // FAQ schema must mirror the visible accordion exactly.
  const visible = [...html.matchAll(/<details[^>]*><summary>(.*?)(?:<span>\+<\/span>)?<\/summary><p>(.*?)<\/p><\/details>/gs)].map(([, q, a]) => ({ q: decode(q), a: decode(a) }));
  if (visible.length) {
    assert.ok(types(page).includes('FAQPage'), `${path}: FAQPage for visible FAQ`);
    assert.deepEqual(page.mainEntity.map(item => ({ q: item.name, a: item.acceptedAnswer.text })), visible, `${path}: FAQ schema matches accordion`);
    for (const item of page.mainEntity) assert.equal(item['@type'], 'Question');
  }

  if (landing.includes(path)) {
    const app = nodes.find(node => types(node).includes('SoftwareApplication'));
    assert.ok(app, `${path}: SoftwareApplication`);
    assert.equal(app.applicationCategory, 'HealthApplication');
    assert.equal(app.operatingSystem, 'Android');
    assert.ok(app.offers.some(offer => offer.price === '0.00'), `${path}: free offer`);
    assert.ok(app.offers.some(offer => offer.priceSpecification?.billingDuration), `${path}: subscription offer`);
    for (const offer of app.offers) assert.match(offer.price, /^\d+\.\d{2}$/, `${path}: offer price`);
    for (const feature of ['AI workout', 'AI meal', 'Barcode', 'macro']) assert.ok(app.featureList.some(item => item.includes(feature)), `${path}: featureList ${feature}`);
  }
  if (path.startsWith('/compare/')) {
    const table = html.match(/<table[^>]*>.*?<\/table>/s)?.[0];
    assert.ok(table, `${path}: comparison table`);
    assert.match(table, /<caption>/, `${path}: table caption`);
    assert.ok((table.match(/<th scope="col"/g) || []).length === 3, `${path}: column headers`);
    assert.ok((table.match(/<th scope="row"/g) || []).length >= 5, `${path}: row headers`);
    assert.ok(page.mainEntity.length >= 4 && page.mainEntity.length <= 5, `${path}: 4 to 5 FAQs`);
  }
  if (path.startsWith('/guides/')) {
    const article = nodes.find(node => types(node).includes('Article'));
    assert.ok(article && types(article).includes('TechArticle'), `${path}: Article/TechArticle`);
    assert.ok(isDate(article.datePublished) && isDate(article.dateModified) && article.dateModified >= article.datePublished, `${path}: article dates`);
    assert.equal(article.author['@type'], 'Organization');
    assert.equal(article.author.name, 'Nexal');
    assert.ok(article.headline.length <= 110, `${path}: headline length`);
  }
  if (path !== '/') {
    const crumbs = nodes.find(node => node['@type'] === 'BreadcrumbList');
    assert.ok(crumbs, `${path}: BreadcrumbList`);
    crumbs.itemListElement.forEach((item, index) => assert.equal(item.position, index + 1, `${path}: crumb position`));
    assert.equal(crumbs.itemListElement.at(-1).item, loc, `${path}: last crumb is page`);
  }
}
console.log(`PASS structured data: ${entries.length} pages, JSON-LD parses, refs resolve, FAQ mirrors accordions`);
