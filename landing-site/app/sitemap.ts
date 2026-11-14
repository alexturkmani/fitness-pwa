import type { MetadataRoute } from 'next';
import { siteUrl } from './site-config';
import { guides, guideDates, libraryUpdatedDate } from './guides/content';

/**
 * Last meaningful content change per static route (YYYY-MM-DD). Bump the date
 * when a page's visible copy changes; markup-only changes don't count, so
 * search engines keep trusting <lastmod>.
 */
const pageUpdated = {
  home: '2026-11-07',
  landing: '2026-10-11',
  freeTrackers: '2026-11-14',
  /** Comparison tables were checked against competitor pages on this date; re-check before bumping. */
  comparisons: '2026-11-14',
  about: '2026-10-11',
  privacy: '2026-09-01',
};

export default function sitemap(): MetadataRoute.Sitemap {
  const guideEntries = guides.map(guide => ({ url: `${siteUrl}/guides/${guide.slug}`, lastModified: guideDates(guide).modified, changeFrequency: 'monthly' as const, priority: 0.7 }));
  const newestGuide = guideEntries.reduce((latest, entry) => (entry.lastModified > latest ? entry.lastModified : latest), libraryUpdatedDate);
  return [
    { url: `${siteUrl}/`, lastModified: pageUpdated.home, changeFrequency: 'weekly', priority: 1 },
    { url: `${siteUrl}/workout-meal-planner-app`, lastModified: pageUpdated.landing, changeFrequency: 'monthly', priority: 0.9 },
    { url: `${siteUrl}/ai-workout-planner`, lastModified: pageUpdated.landing, changeFrequency: 'monthly', priority: 0.9 },
    { url: `${siteUrl}/ai-meal-planner`, lastModified: pageUpdated.landing, changeFrequency: 'monthly', priority: 0.9 },
    { url: `${siteUrl}/calorie-macro-tracker`, lastModified: pageUpdated.landing, changeFrequency: 'monthly', priority: 0.8 },
    { url: `${siteUrl}/free-calorie-and-macro-tracker`, lastModified: pageUpdated.freeTrackers, changeFrequency: 'monthly', priority: 0.9 },
    { url: `${siteUrl}/free-workout-tracker-app`, lastModified: pageUpdated.freeTrackers, changeFrequency: 'monthly', priority: 0.9 },
    { url: `${siteUrl}/compare/myfitnesspal-alternative`, lastModified: pageUpdated.comparisons, changeFrequency: 'monthly', priority: 0.8 },
    { url: `${siteUrl}/compare/fitbod-alternative`, lastModified: pageUpdated.comparisons, changeFrequency: 'monthly', priority: 0.8 },
    { url: `${siteUrl}/guides`, lastModified: newestGuide, changeFrequency: 'weekly', priority: 0.8 },
    ...guideEntries,
    { url: `${siteUrl}/about`, lastModified: pageUpdated.about, changeFrequency: 'yearly', priority: 0.3 },
    { url: `${siteUrl}/privacy`, lastModified: pageUpdated.privacy, changeFrequency: 'yearly', priority: 0.2 },
  ];
}
