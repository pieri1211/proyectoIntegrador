// IndexedDB helper
const DB_NAME = 'agriwave_db';
const DB_VERSION = 1;
const stores = ['animales','finanzas','lotes','salud','logs'];

function openDB(){
  return new Promise((resolve,reject)=>{
    const req = indexedDB.open(DB_NAME, DB_VERSION);
    req.onupgradeneeded = (e)=>{
      const db = req.result;
      stores.forEach(s=>{
        if(!db.objectStoreNames.contains(s)){
          const os = db.createObjectStore(s, { keyPath: 'id', autoIncrement: true });
          if(s==='animales'){ db.deleteObjectStore('animales'); db.createObjectStore('animales', { keyPath: 'id' }); }
        }
      });
      if(!db.objectStoreNames.contains('animales')){
        db.createObjectStore('animales',{keyPath:'id'});
      }
    };
    req.onerror = ()=>reject(req.error);
    req.onsuccess = ()=>resolve(req.result);
  });
}

async function tx(store, mode='readonly'){
  const db = await openDB();
  return db.transaction(store, mode).objectStore(store);
}

export const db = {
  async put(store, value){ const os = await tx(store,'readwrite'); return new Promise((res,rej)=>{ const r = os.put(value); r.onsuccess=()=>res(r.result); r.onerror=()=>rej(r.error); }); },
  async add(store, value){ const os = await tx(store,'readwrite'); return new Promise((res,rej)=>{ const r = os.add(value); r.onsuccess=()=>res(r.result); r.onerror=()=>rej(r.error); }); },
  async get(store, id){ const os = await tx(store); return new Promise((res,rej)=>{ const r = os.get(id); r.onsuccess=()=>res(r.result); r.onerror=()=>rej(r.error); }); },
  async del(store, id){ const os = await tx(store,'readwrite'); return new Promise((res,rej)=>{ const r = os.delete(id); r.onsuccess=()=>res(true); r.onerror=()=>rej(r.error); }); },
  async all(store){ const os = await tx(store); return new Promise((res,rej)=>{ const r = os.getAll(); r.onsuccess=()=>res(r.result); r.onerror=()=>rej(r.error); }); },
  async clear(store){ const os = await tx(store,'readwrite'); return new Promise((res,rej)=>{ const r = os.clear(); r.onsuccess=()=>res(true); r.onerror=()=>rej(r.error); }); },
  async exportAll(){ const result={}; for(const s of stores){ result[s]=await this.all(s); } return result; },
  async importAll(payload){ for(const s of stores){ await this.clear(s); const items = payload[s]||[]; for(const item of items){ if(s!=='animales' && !item.id) delete item.id; await this.put(s, item); } } }
};
