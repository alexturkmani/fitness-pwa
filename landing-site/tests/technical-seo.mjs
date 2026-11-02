import assert from 'node:assert/strict';

const base = (process.env.SEO_CHECK_URL || 'http://localhost:3100').replace(/\/$/, '');
const origin = 'https://www.nexalfitness.com';
const decode = value => value.replace(/&amp;/g, '&').replace(/&#x27;/g, "'").replace(/&quot;/g, '"');
const get = async path => {
  const response = await fetch(`${base}${path}`, { redirect: 'manual' });
  assert.equal(response.status, 200, `200 status: ${path}`);
  assert.ok(!/noindex/i.test(response.headers.get('x-robots-tag') || ''), `Indexable header: ${path}`);
  return response.text();
};
const robots = await get('/robots.txt');
assert.ok(robots.includes(`Sitemap: ${origin}/sitemap.xml`));
assert.ok(!/Disallow:\s*\/\s*$/m.test(robots));
const sitemap = await get('/sitemap.xml');
const urls = [...sitemap.matchAll(/<loc>(.*?)<\/loc>/g)].map(match => decode(match[1]));
assert.equal(new Set(urls).size, urls.length, 'No duplicate sitemap URLs');
if (process.env.EXPECT_SITEMAP_COUNT) assert.equal(urls.length, Number(process.env.EXPECT_SITEMAP_COUNT), 'Expected published sitemap URL count');
const documents = new Map();
const titles = new Set();
const descriptions = new Set();
const assets = new Set();
for (const url of urls) {
  assert.ok(url.startsWith(origin), `Canonical origin: ${url}`);
  const path = new URL(url).pathname;
  const html = await get(path);
  documents.set(path, html);
  assert.ok(/<html[^>]*lang="en"/.test(html), `${path}: language`);
  assert.ok(html.includes('name="viewport"'), `${path}: viewport`);
  assert.equal((html.match(/<h1(?:\s|>)/g) || []).length, 1, `${path}: one H1`);
  const canonical = html.match(/rel="canonical" href="([^"]*)"/)?.[1];
  assert.equal(canonical?.replace(/\/$/, ''), url.replace(/\/$/, ''), `${path}: self canonical`);
  const title = decode(html.match(/<title>(.*?)<\/title>/)?.[1] || '');
  const description = decode(html.match(/<meta name="description" content="([^"]*)"/)?.[1] || '');
  assert.ok(title && title.length < 60, `${path}: title ${title.length}`);
  assert.ok(description && description.length < 155, `${path}: description ${description.length}`);
  assert.ok(!titles.has(title), `${path}: unique title`); titles.add(title);
  assert.ok(!descriptions.has(description), `${path}: unique description`); descriptions.add(description);
  assert.ok(!/<meta name="robots" content="[^"]*noindex/.test(html), `${path}: indexable metadata`);
  assert.ok(html.includes('property="og:title"') && html.includes('name="twitter:card"'), `${path}: social metadata`);
  const og = html.match(/property="og:url" content="([^"]*)"/)?.[1];
  assert.equal(og?.replace(/\/$/, ''), url.replace(/\/$/, ''), `${path}: OG URL`);
  for (const block of html.matchAll(/<script type="application\/ld\+json">(.*?)<\/script>/gs)) JSON.parse(block[1]);
  for (const image of html.matchAll(/<img\b[^>]*>/g)) {
    assert.ok(/\balt="[^"]*"/.test(image[0]), `${path}: image alt`);
    assert.ok(/\bwidth=/.test(image[0]) && /\bheight=/.test(image[0]), `${path}: stable image dimensions`);
    const src = decode(image[0].match(/\bsrc="([^"]*)"/)?.[1] || '');
    if (src.startsWith('/')) assets.add(src);
  }
  console.log(`PASS ${path}: status, indexability, canonical, unique metadata, headings, schema, social, images`);
}
const seenLinks = new Set();
const guideReferrers = new Map();
for (const [path, html] of documents) {
  for (const match of html.matchAll(/href="(\/[^" ]*|#[^" ]*)"/g)) {
    const href = decode(match[1]);
    const url = new URL(href, `${base}${path}`);
    if (url.pathname.startsWith('/guides/') && url.pathname !== path) {
      if (!guideReferrers.has(url.pathname)) guideReferrers.set(url.pathname, new Set());
      guideReferrers.get(url.pathname).add(path);
    }
    if (url.hash && documents.has(url.pathname)) {
      const id = decodeURIComponent(url.hash.slice(1));
      assert.ok(documents.get(url.pathname).includes(`id="${id}"`), `${path}: anchor ${href}`);
    }
    if (seenLinks.has(url.pathname) || documents.has(url.pathname) || url.pathname.startsWith('/_next/')) continue;
    seenLinks.add(url.pathname);
    const response = await fetch(`${base}${url.pathname}`);
    assert.equal(response.status, 200, `${path}: internal link ${href}`);
  }
}
for (const path of documents.keys()) {
  if (path.startsWith('/guides/')) assert.ok(guideReferrers.get(path)?.size >= 2, `${path}: linked from hub and another relevant page`);
}
for (const asset of [...assets, '/og.png', '/nexal-logo.png']) {
  const response = await fetch(`${base}${asset}`);
  assert.equal(response.status, 200, `Asset: ${asset}`);
  assert.ok(response.headers.get('content-type')?.startsWith('image/'), `Image type: ${asset}`);
}
for (const path of ['/not-a-real-page', '/guides/not-a-real-guide']) {
  const response = await fetch(`${base}${path}`);
  assert.equal(response.status, 404, `${path}: real 404`);
  const html = await response.text();
  assert.ok(html.includes('noindex'), `${path}: noindex`);
}
if (process.env.SEO_CHECK_REDIRECTS === '1') {
  for (const host of ['http://nexalfitness.com', 'https://nexalfitness.com', 'http://www.nexalfitness.com']) {
    for (const path of ['/', '/ai-workout-planner']) {
      const initial = await fetch(`${host}${path}`, { redirect: 'manual' });
      assert.ok([301,308].includes(initial.status), `${host}${path}: permanent redirect`);
      const final = await fetch(`${host}${path}`);
      assert.equal(final.url, `${origin}${path}`, `${host}${path}: canonical destination`);
    }
  }
}
console.log(`PASS technical crawl: ${urls.length} sitemap pages, internal links, anchors, images, robots and 404s`);
