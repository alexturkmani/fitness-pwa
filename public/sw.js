const CACHE_NAME = 'fitmate-v2';
const STATIC_ASSETS = ['/dashboard', '/workouts', '/meals', '/scanner', '/nutrition', '/progress'];

self.addEventListener('install', (event) => {
  event.waitUntil(
    caches.open(CACHE_NAME).then((cache) => cache.addAll(STATIC_ASSETS))
  );
  self.skipWaiting();
});

self.addEventListener('activate', (event) => {
  event.waitUntil(
    caches.keys().then((keys) =>
      Promise.all(keys.filter((k) => k !== CACHE_NAME).map((k) => caches.delete(k)))
    )
  );
  self.clients.claim();
});

self.addEventListener('fetch', (event) => {
  const url = new URL(event.request.url);

  // Never cache API calls, auth pages, or the landing page
  if (url.pathname.startsWith('/api/') || url.pathname === '/' || url.pathname === '/login' || url.pathname === '/register') {
    event.respondWith(fetch(event.request));
    return;
  }

  // Network-first for HTML pages, cache-first for assets
  if (event.request.mode === 'navigate') {
    event.respondWith(
      fetch(event.request).catch(() => caches.match(event.request))
    );
  } else {
    event.respondWith(
      caches.match(event.request).then((cached) => cached || fetch(event.request))
    );
  }
});
