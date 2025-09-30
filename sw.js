const CACHE = 'agriwave-cache-v1';
const ASSETS = [
  './',
  './index.html',
  './styles.css',
  './app.js',
  './db.js',
  './manifest.json',
  './icons/icon-192.png',
  './icons/icon-512.png',
  'https://cdn.jsdelivr.net/npm/chart.js'
];

self.addEventListener('install', e=>{
  e.waitUntil(caches.open(CACHE).then(c=>c.addAll(ASSETS)));
  self.skipWaiting();
});

self.addEventListener('activate', e=>{
  e.waitUntil(
    caches.keys().then(keys=>Promise.all(keys.filter(k=>k!==CACHE).map(k=>caches.delete(k)))),
  );
  self.clients.claim();
});

self.addEventListener('fetch', e=>{
  const url = new URL(e.request.url);
  // Network-first for JSON sync endpoints, cache-first for static assets
  if(url.origin===location.origin){
    e.respondWith(
      caches.match(e.request).then(res=>res||fetch(e.request).then(resp=>{
        if(e.request.method==='GET' && resp.status===200){
          const clone = resp.clone();
          caches.open(CACHE).then(c=>c.put(e.request, clone));
        }
        return resp;
      }).catch(()=>res))
    );
  }else{
    // For CDN like chart.js: try cache, else network then cache
    e.respondWith(
      caches.match(e.request).then(res=>res||fetch(e.request).then(resp=>{
        const clone = resp.clone();
        caches.open(CACHE).then(c=>c.put(e.request, clone));
        return resp;
      }))
    );
  }
});
