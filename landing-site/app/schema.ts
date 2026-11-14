import { playStoreBaseUrl, siteUrl } from './site-config';

/**
 * JSON-LD builders. Every node carries a stable @id so pages can reference the
 * shared Organization / WebSite / SoftwareApplication nodes instead of
 * repeating them, and each page emits a single @graph.
 */

export type Faq = { question: string; answer: string };
export type Crumb = { name: string; path: string };
type Node = Record<string, unknown>;

const abs = (path: string) => `${siteUrl}${path === '/' ? '/' : path}`;

export const ids = {
  organization: `${siteUrl}/#organization`,
  website: `${siteUrl}/#website`,
  app: `${siteUrl}/#app`,
  logo: `${siteUrl}/#logo`,
};

/**
 * Premium list prices in USD, as configured in Google Play Console
 * (see PLAY_STORE_SETUP.md). Structured data must match what users are charged,
 * so update these whenever the Play base-plan prices change.
 */
export const premiumPlans = [
  { name: 'Nexal Premium (monthly)', price: '12.99', duration: 'P1M', unitCode: 'MON' },
  { name: 'Nexal Premium (yearly)', price: '110.00', duration: 'P1Y', unitCode: 'ANN' },
] as const;

export const appFeatureList = [
  'AI workout plans (Premium)',
  'AI meal plans (Premium)',
  'Barcode scanner for packaged foods (Premium)',
  'Calorie and macro tracking',
  'Workout logging and history',
  'Progress and weight trends',
];

export function organizationNode(): Node {
  return {
    '@type': 'Organization',
    '@id': ids.organization,
    name: 'Nexal',
    url: abs('/'),
    logo: { '@type': 'ImageObject', '@id': ids.logo, url: `${siteUrl}/nexal-logo.png` },
    email: 'support@nexal.app',
    sameAs: [playStoreBaseUrl],
  };
}

export function websiteNode(): Node {
  return { '@type': 'WebSite', '@id': ids.website, name: 'Nexal', url: abs('/'), inLanguage: 'en', publisher: { '@id': ids.organization } };
}

export function softwareApplicationNode(): Node {
  return {
    '@type': 'SoftwareApplication',
    '@id': ids.app,
    name: 'Nexal',
    url: abs('/'),
    description: 'Android fitness app with AI workout and meal plans, calorie and macro tracking, barcode scanning, workout logging and progress insights.',
    applicationCategory: 'HealthApplication',
    applicationSubCategory: 'Fitness',
    operatingSystem: 'Android',
    downloadUrl: playStoreBaseUrl,
    installUrl: playStoreBaseUrl,
    image: `${siteUrl}/og.png`,
    screenshot: [`${siteUrl}/app/home.webp`, `${siteUrl}/app/workouts.webp`],
    featureList: appFeatureList,
    publisher: { '@id': ids.organization },
    offers: [
      {
        '@type': 'Offer',
        name: 'Nexal Free',
        description: 'Free download with core meal, calorie, macro, workout and progress tracking.',
        price: '0.00',
        priceCurrency: 'USD',
        url: playStoreBaseUrl,
        availability: 'https://schema.org/InStock',
        category: 'free',
      },
      ...premiumPlans.map(plan => ({
        '@type': 'Offer',
        name: plan.name,
        description: 'Optional auto-renewing subscription adding AI workout plans, AI meal plans, AI macro estimates and barcode scanning. Local prices are shown in Google Play.',
        price: plan.price,
        priceCurrency: 'USD',
        url: playStoreBaseUrl,
        availability: 'https://schema.org/InStock',
        category: 'subscription',
        priceSpecification: {
          '@type': 'UnitPriceSpecification',
          price: plan.price,
          priceCurrency: 'USD',
          billingDuration: plan.duration,
          unitCode: plan.unitCode,
        },
      })),
    ],
  };
}

export function webPageNode(path: string, name: string, description: string, extra: Node = {}): Node {
  const url = abs(path);
  return {
    '@type': 'WebPage',
    '@id': `${url}#webpage`,
    url,
    name,
    description,
    inLanguage: 'en',
    isPartOf: { '@id': ids.website },
    breadcrumb: path === '/' ? undefined : { '@id': `${url}#breadcrumb` },
    ...extra,
  };
}

export function faqEntities(faqs: Faq[]): Node[] {
  return faqs.map(faq => ({ '@type': 'Question', name: faq.question, acceptedAnswer: { '@type': 'Answer', text: faq.answer } }));
}

export function breadcrumbNode(path: string, crumbs: Crumb[]): Node {
  return {
    '@type': 'BreadcrumbList',
    '@id': `${abs(path)}#breadcrumb`,
    itemListElement: crumbs.map((crumb, index) => ({ '@type': 'ListItem', position: index + 1, name: crumb.name, item: abs(crumb.path) })),
  };
}

export function articleNode(path: string, article: { headline: string; description: string; published: string; modified: string; section?: string }): Node {
  const url = abs(path);
  return {
    '@type': ['Article', 'TechArticle'],
    '@id': `${url}#article`,
    headline: article.headline,
    description: article.description,
    articleSection: article.section,
    mainEntityOfPage: url,
    isPartOf: { '@id': `${url}#webpage` },
    url,
    datePublished: article.published,
    dateModified: article.modified,
    inLanguage: 'en',
    image: `${siteUrl}/og.png`,
    author: { '@type': 'Organization', '@id': ids.organization, name: 'Nexal', url: siteUrl },
    publisher: { '@id': ids.organization },
    about: { '@type': 'SoftwareApplication', '@id': ids.app, name: 'Nexal', operatingSystem: 'Android', applicationCategory: 'HealthApplication' },
  };
}

/** Wrap nodes in one @graph. JSON.stringify drops undefined fields. */
export function graph(...nodes: Node[]): Node {
  return { '@context': 'https://schema.org', '@graph': nodes };
}

/**
 * Landing pages: org + site + app + page + breadcrumb. When the page has an FAQ
 * accordion the page node itself becomes an FAQPage (a WebPage subtype) whose
 * mainEntity is the same question list the accordion renders.
 */
export function landingGraph(path: string, name: string, description: string, faqs: Faq[]): Node {
  const crumbs: Crumb[] = path === '/' ? [] : [{ name: 'Home', path: '/' }, { name, path }];
  const page = webPageNode(path, name, description, {
    about: { '@id': ids.app },
    ...(faqs.length ? { '@type': 'FAQPage', mainEntity: faqEntities(faqs) } : { mainEntity: { '@id': ids.app } }),
  });
  return graph(organizationNode(), websiteNode(), softwareApplicationNode(), page, ...(crumbs.length ? [breadcrumbNode(path, crumbs)] : []));
}
