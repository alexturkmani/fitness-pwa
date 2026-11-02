export function searchGuides<T extends { title: string; description: string; category: string }>(guides: T[], query: string): T[] {
  const terms = query.trim().toLocaleLowerCase().split(/\s+/).filter(Boolean);
  return guides.filter(guide => terms.every(term => `${guide.title} ${guide.description} ${guide.category}`.toLocaleLowerCase().includes(term)));
}
