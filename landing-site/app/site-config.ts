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
      locale: 'en_US',
      type: 'website',
      images: [{ url: '/og.png', width: 1731, height: 909, alt: 'Nexal Android fitness app with workout, meal and progress screens' }],
    },
    twitter: { card: 'summary_large_image', title: `${title} | Nexal`, description, images: ['/og.png'] },
  };
}
