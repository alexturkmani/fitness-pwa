import type { MetadataRoute } from 'next';

export default function sitemap(): MetadataRoute.Sitemap {
  const now = new Date();
  return [
    { url: 'https://www.nexalfitness.com/', lastModified: now, changeFrequency: 'weekly', priority: 1 },
    { url: 'https://www.nexalfitness.com/ai-workout-planner', lastModified: now, changeFrequency: 'monthly', priority: 0.9 },
    { url: 'https://www.nexalfitness.com/ai-meal-planner', lastModified: now, changeFrequency: 'monthly', priority: 0.9 },
    { url: 'https://www.nexalfitness.com/workout-meal-planner-app', lastModified: now, changeFrequency: 'monthly', priority: 0.9 },
    { url: 'https://www.nexalfitness.com/calorie-macro-tracker', lastModified: now, changeFrequency: 'monthly', priority: 0.8 },
    { url: 'https://www.nexalfitness.com/privacy', lastModified: now, changeFrequency: 'yearly', priority: 0.2 },
  ];
}
