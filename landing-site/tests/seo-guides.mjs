import assert from 'node:assert/strict';
import { readFile, readdir } from 'node:fs/promises';

const base = (process.env.SEO_CHECK_URL || 'http://localhost:3100').replace(/\/$/, '');
const canonicalBase = 'https://www.nexalfitness.com';
const files = (await readdir(new URL('../app/guides/', import.meta.url))).filter(file => file.endsWith('.ts'));
const source = (await Promise.all(files.map(file => readFile(new URL(`../app/guides/${file}`, import.meta.url), 'utf8')))).join('\n');
const slugs = [...source.matchAll(/slug: '([^']+)'/g)].map(match => match[1]);
assert.equal(new Set(slugs).size, slugs.length, 'Unique guide URLs');
const decode = value => value.replace(/&amp;/g, '&').replace(/&#x27;/g, "'").replace(/&quot;/g, '"');
async function html(path) {
  const response = await fetch(`${base}${path}`);
  assert.equal(response.status, 200, path);
  return response.text();
}

for (const slug of slugs) {
  const path = `/guides/${slug}`;
  const document = await html(path);
  assert.equal((document.match(/<h1(?:\s|>)/g) || []).length, 1, `${path}: one H1`);
  const title = decode(document.match(/<title>(.*?)<\/title>/)?.[1] || '');
  assert.ok(title.length > 0 && title.length < 60, `${path}: title length ${title.length}`);
  const description = decode(document.match(/<meta name="description" content="([^"]*)"/)?.[1] || '');
  assert.ok(description.length > 0 && description.length < 155, `${path}: description length ${description.length}`);
  assert.ok(document.includes(`rel="canonical" href="${canonicalBase}${path}"`), `${path}: canonical`);
  const blocks = [...document.matchAll(/<script type="application\/ld\+json">(.*?)<\/script>/gs)].map(match => JSON.parse(match[1]));
  const graph = blocks.find(block => block['@graph'])?.['@graph'];
  assert.ok(graph?.some(block => block['@type'] === 'Article' && block.mainEntityOfPage === `${canonicalBase}${path}`), `${path}: article schema`);
  assert.ok(graph?.some(block => block['@type'] === 'BreadcrumbList'), `${path}: breadcrumbs`);
  const playLinks = [...document.matchAll(/href="(https:\/\/play.google.com\/store\/apps\/details[^" ]*)"/g)];
  assert.ok(playLinks.length >= 2, `${path}: Play CTAs`);
  for (const match of playLinks) {
    const url = new URL(decode(match[1]));
    assert.equal(url.searchParams.get('id'), 'com.nexal.app');
    const referrer = new URLSearchParams(url.searchParams.get('referrer'));
    assert.equal(referrer.get('utm_campaign'), 'web_to_app');
    assert.ok(referrer.get('utm_content'));
  }
  assert.ok(!document.includes('—'), `${path}: no em dashes`);
  console.log(`PASS ${path}: metadata, schema, canonical, Play attribution`);
}
for (const path of ['/', '/guides', '/ai-workout-planner', '/ai-meal-planner', '/calorie-macro-tracker', '/workout-meal-planner-app']) {
  const document = await html(path);
  if (path === '/guides') {
    assert.ok(document.includes('id="guide-query"'), 'Guide search is server rendered');
    assert.ok(document.includes('aria-live="polite"'), 'Guide search announces result counts');
    for (const slug of slugs) assert.ok(document.includes(`href="/guides/${slug}"`), `${path}: crawlable link to ${slug}`);
  } else assert.ok(document.includes('href="/guides/'), `${path}: crawlable guide links`);
  console.log(`PASS ${path}: linked guide cluster`);
}
const sitemap = await html('/sitemap.xml');
for (const slug of slugs) assert.ok(sitemap.includes(`${canonicalBase}/guides/${slug}`));
const missing = await fetch(`${base}/guides/not-a-real-guide`);
assert.equal(missing.status, 404, 'Unknown guide returns 404');
console.log('PASS sitemap and unknown-route 404');
