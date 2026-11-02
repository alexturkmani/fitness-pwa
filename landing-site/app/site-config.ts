import type { Metadata } from 'next';

export const siteUrl = (process.env.NEXT_PUBLIC_SITE_URL || 'https://www.nexalfitness.com').replace(/\/$/, '');

export const playStoreBaseUrl = 'https://play.google.com/store/apps/details?id=com.nexal.app';

export function playStoreUrl(placement: string): string {
  const referrer = new URLSearchParams({
    utm_source: 'nexal_website',
    utm_medium: 'website',
    utm_campaign: 'web_to_app',
    utm_content: placement,
  });
  return `${playStoreBaseUrl}&referrer=${encodeURIComponent(referrer.toString())}`;
}

export const appSchema = {
  '@context': 'https://schema.org',
  '@type': 'SoftwareApplication',
  '@id': `${siteUrl}/#app`,
  name: 'Nexal',
  url: `${siteUrl}/`,
  description: 'Android fitness app with AI workout and meal plans, calorie and macro tracking, workout logging and progress insights.',
  applicationCategory: 'HealthApplication',
  operatingSystem: 'Android',
  downloadUrl: playStoreBaseUrl,
  installUrl: playStoreBaseUrl,
  image: `${siteUrl}/nexal-logo.png`,
  offers: {
    '@type': 'Offer',
    url: playStoreBaseUrl,
    price: '0',
    priceCurrency: 'USD',
    description: 'Free to download with optional Premium subscriptions.',
  },
};

export const websiteSchema = {
  '@context': 'https://schema.org',
  '@type': 'WebSite',
  '@id': `${siteUrl}/#website`,
  name: 'Nexal',
  url: `${siteUrl}/`,
};

export function seoPageMetadata(path: string, title: string, description: string): Metadata {
  return {
    title,
    description,
    alternates: { canonical: path },
    openGraph: {
      title: `${title} | Nexal`,
      description,
      url: path,
      siteName: 'Nexal',
      type: 'website',
      images: [{ url: '/og.png', width: 1731, height: 909, alt: 'Nexal Android fitness app with workout, meal and progress screens' }],
    },
    twitter: { card: 'summary_large_image', title: `${title} | Nexal`, description, images: ['/og.png'] },
  };
}
