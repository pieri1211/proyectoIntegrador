import { db } from './db.js';

// UI helpers
const $ = sel => document.querySelector(sel);
const $$ = sel => Array.from(document.querySelectorAll(sel));

// Tabs
$$('.tab').forEach(btn=>btn.addEventListener('click',()=>{
  $$('.tab').forEach(b=>b.classList.remove('active'));
  btn.classList.add('active');
  const tab = btn.dataset.tab;
  $$('.panel').forEach(p=>p.classList.remove('active'));
  $('#'+tab).classList.add('active');
  if(tab==='reportes') renderReportes();
}));

// Online/offline indicators
function updateOnline(){
  const online = navigator.onLine;
  $('#onlineStatus').textContent = online ? '●' : '○';
  $('#onlineStatus').style.color = online ? '#6ee7b7' : '#ef4444';
  $('#syncStatus').textContent = online ? 'Online' : 'Offline';
}
window.addEventListener('online', updateOnline);
window.addEventListener('offline', updateOnline);
updateOnline();

// ------- Animales (RF1, RF2, RF3) --------
const animalForm = $('#animalForm');
const animalTable = $('#animalTable tbody');
const animalSearch = $('#animalSearch');
const animalHistory = $('#animalHistory');

async function loadAnimales(){
  const q = animalSearch.value.trim().toLowerCase();
  const items = (await db.all('animales')).filter(a=>!q || a.id.toLowerCase().includes(q) || (a.raza||'').toLowerCase().includes(q));
  animalTable.innerHTML = items.map(a=>`<tr>
    <td>${a.id}</td><td>${a.raza||''}</td><td>${a.sexo||''}</td><td>${a.fechaNacimiento||''}</td>
    <td>
      <button data-edit="${a.id}">Editar</button>
      <button class="danger" data-del="${a.id}">Eliminar</button>
    </td>
  </tr>`).join('');
}
animalSearch.addEventListener('input', loadAnimales);

animalForm.addEventListener('submit', async (e)=>{
  e.preventDefault();
  const form = new FormData(animalForm);
  const animal = {
    id: (form.get('id')||'').trim(),
    raza: form.get('raza'),
    sexo: form.get('sexo'),
    fechaNacimiento: form.get('fechaNacimiento'),
    observaciones: form.get('observaciones'),
    // embed a history array inside animal record
    history: (await db.get('animales', form.get('id')))?.history || []
  };
  await db.put('animales', animal);
  await log(`Animal ${animal.id} guardado/actualizado`);
  animalForm.reset();
  loadAnimales();
});
$('#animalReset').onclick = ()=>animalForm.reset();

animalTable.addEventListener('click', async (e)=>{
  const id = e.target.dataset.edit || e.target.dataset.del;
  if(!id) return;
  if(e.target.dataset.edit){
    const a = await db.get('animales', id);
    for(const [k,v] of Object.entries(a)){
      if(animalForm[k]) animalForm[k].value = v;
    }
    renderAnimalHistory(a);
  }else if(e.target.dataset.del){
    if(confirm('¿Eliminar animal y su historial?')){
      await db.del('animales', id);
      await log(`Animal ${id} eliminado`);
      loadAnimales();
      animalHistory.innerHTML = '';
    }
  }
});

function renderAnimalHistory(a){
  if(!a) return;
  animalHistory.innerHTML = (a.history||[]).slice().reverse().map(h=>`<li>${h.fecha||''} – ${h.texto}</li>`).join('') || '<li class="muted">Sin registros</li>';
}

// ------- Finanzas (RF4, RF5) --------
const finForm = $('#finForm');
const finTable = $('#finTable tbody');
const finSearch = $('#finSearch');
const balanceCell = $('#balanceCell');

async function loadFin(){
  const q = finSearch.value.trim().toLowerCase();
  const items = (await db.all('finanzas')).filter(r=>!q || (r.concepto||'').toLowerCase().includes(q));
  let balance = 0;
  finTable.innerHTML = items.map(r=>{
    const m = Number(r.monto)||0;
    balance += r.tipo==='ingreso' ? m : -m;
    return `<tr>
      <td>${r.fecha||''}</td><td>${r.tipo}</td><td>${r.concepto||''}</td><td>${m.toFixed(2)}</td>
      <td><button data-del-fin="${r.id}">✕</button></td></tr>`;
  }).join('');
  balanceCell.textContent = balance.toFixed(2);
}
finSearch.addEventListener('input', loadFin);
finForm.addEventListener('submit', async (e)=>{
  e.preventDefault();
  const f = new FormData(finForm);
  const row = { id: Date.now(), fecha:f.get('fecha'), tipo:f.get('tipo'), monto:parseFloat(f.get('monto')), concepto:f.get('concepto') };
  await db.put('finanzas', row);
  await log(`Finanza ${row.tipo} registrada: ${row.monto}`);
  finForm.reset();
  loadFin();
});
$('#finReset').onclick = ()=>finForm.reset();
$('#finTable').addEventListener('click', async (e)=>{
  const id = e.target.dataset.delFin;
  if(id){ await db.del('finanzas', Number(id)); loadFin(); }
});

// ------- Lotes (RF6, RF7) --------
const loteForm = $('#loteForm');
const loteTable = $('#loteTable tbody');
const loteSearch = $('#loteSearch');
async function loadLotes(){
  const q = loteSearch.value.trim().toLowerCase();
  const items = (await db.all('lotes')).filter(r=>!q || (r.lote||'').toLowerCase().includes(q) || (r.animalId||'').toLowerCase().includes(q));
  loteTable.innerHTML = items.map(r=>`<tr>
    <td>${r.lote||''}</td><td>${r.animalId||''}</td><td>${r.entrada||''}</td><td>${r.salida||''}</td><td>${r.nota||''}</td>
    <td><button data-del-lote="${r.id}">✕</button></td></tr>`).join('');
}
loteSearch.addEventListener('input', loadLotes);
loteForm.addEventListener('submit', async (e)=>{
  e.preventDefault();
  const f = new FormData(loteForm);
  const row = { id: Date.now(), lote:f.get('lote'), animalId:f.get('animalId'), entrada:f.get('entrada'), salida:f.get('salida'), nota:f.get('nota') };
  await db.put('lotes', row);
  await log(`Uso de lote ${row.lote} registrado`);
  loteForm.reset();
  loadLotes();
});
$('#loteReset').onclick=()=>loteForm.reset();
$('#loteTable').addEventListener('click', async (e)=>{
  const id = e.target.dataset.delLote;
  if(id){ await db.del('lotes', Number(id)); loadLotes(); }
});

// ------- Salud (RF8, RF9) --------
const saludForm = $('#saludForm');
const saludTable = $('#saludTable tbody');
const saludSearch = $('#saludSearch');
async function loadSalud(){
  const q = saludSearch.value.trim().toLowerCase();
  const items = (await db.all('salud')).filter(r=>!q || (r.animalId||'').toLowerCase().includes(q) || (r.tipo||'').toLowerCase().includes(q));
  saludTable.innerHTML = items.map(r=>`<tr>
    <td>${r.fecha}</td><td>${r.animalId}</td><td>${r.tipo}</td><td>${r.descripcion||''}</td>
    <td><button data-del-salud="${r.id}">✕</button></td></tr>`).join('');
}
saludSearch.addEventListener('input', loadSalud);
saludForm.addEventListener('submit', async (e)=>{
  e.preventDefault();
  const f = new FormData(saludForm);
  const row = { id: Date.now(), animalId:f.get('animalId'), fecha:f.get('fecha'), tipo:f.get('tipo'), descripcion:f.get('descripcion') };
  await db.put('salud', row);
  // Push entry into animal history
  const a = await db.get('animales', row.animalId);
  if(a){
    a.history = a.history || [];
    a.history.push({fecha: row.fecha, texto: `${row.tipo}: ${row.descripcion}`});
    await db.put('animales', a);
  }
  await log(`Registro sanitario para ${row.animalId}`);
  saludForm.reset();
  loadSalud();
  if(a) renderAnimalHistory(a);
});
$('#saludReset').onclick=()=>saludForm.reset();
$('#saludTable').addEventListener('click', async (e)=>{
  const id = e.target.dataset.delSalud;
  if(id){ await db.del('salud', Number(id)); loadSalud(); }
});

// ------- Export/Import por sección y global (RF10, RF11, RF12, RF13) --------
function download(filename, data){
  const url = URL.createObjectURL(new Blob([data], {type:'application/json'}));
  const a = document.createElement('a'); a.href=url; a.download=filename; a.click();
  URL.revokeObjectURL(url);
}

function setupSectionExport(btnId, store, file){
  $(btnId).addEventListener('click', async ()=>{
    const payload = await db.all(store);
    download(file, JSON.stringify(payload, null, 2));
  });
}
function setupSectionImport(inputId, store){
  $(inputId).addEventListener('change', async (ev)=>{
    const f = ev.target.files[0];
    if(!f) return;
    const text = await f.text();
    const arr = JSON.parse(text);
    await db.clear(store);
    for(const item of arr){ await db.put(store, item); }
    if(store==='finanzas') loadFin();
    if(store==='animales') loadAnimales();
    if(store==='lotes') loadLotes();
    if(store==='salud') loadSalud();
  });
}
setupSectionExport('#exportAnimales','animales','animales.json');
setupSectionExport('#exportFin','finanzas','finanzas.json');
setupSectionExport('#exportLotes','lotes','lotes.json');
setupSectionExport('#exportSalud','salud','salud.json');
setupSectionImport('#importAnimales','animales');
setupSectionImport('#importFin','finanzas');
setupSectionImport('#importLotes','lotes');
setupSectionImport('#importSalud','salud');

$('#exportTodo').addEventListener('click', async ()=>{
  const payload = await db.exportAll();
  download('agriwave_respaldo.json', JSON.stringify(payload, null, 2));
});
$('#importTodo').addEventListener('change', async (ev)=>{
  const f = ev.target.files[0]; if(!f) return;
  await db.importAll(JSON.parse(await f.text()));
  loadAnimales(); loadFin(); loadLotes(); loadSalud(); renderReportes();
});

// ------- Reportes (RF12, RF13) --------
async function renderReportes(){
  const animales = await db.all('animales');
  const fin = await db.all('finanzas');
  const salud = await db.all('salud');
  const lotes = await db.all('lotes');

  // Resumen
  const ingresos = fin.filter(f=>f.tipo==='ingreso').reduce((a,b)=>a+Number(b.monto||0),0);
  const egresos = fin.filter(f=>f.tipo==='egreso').reduce((a,b)=>a+Number(b.monto||0),0);
  const balance = ingresos-egresos;

  $('#resumenList').innerHTML = `
    <li><b>Animales:</b> ${animales.length}</li>
    <li><b>Ingresos:</b> ${ingresos.toFixed(2)}</li>
    <li><b>Egresos:</b> ${egresos.toFixed(2)}</li>
    <li><b>Balance:</b> ${balance.toFixed(2)}</li>
    <li><b>Registros Sanitarios:</b> ${salud.length}</li>
    <li><b>Movimientos a Lotes:</b> ${lotes.length}</li>
  `;

  // Chart finanzas por mes
  const byMonth = {};
  fin.forEach(r=>{
    const m = (r.fecha||'').slice(0,7)||'N/A';
    byMonth[m] = byMonth[m] || {ingreso:0, egreso:0};
    byMonth[m][r.tipo] += Number(r.monto||0);
  });
  const labels = Object.keys(byMonth).sort();
  const ing = labels.map(l=>byMonth[l].ingreso);
  const egr = labels.map(l=>byMonth[l].egreso);
  drawChart('chartFin', labels, [
    {label:'Ingresos', data:ing},
    {label:'Egresos', data:egr}
  ]);

  // Chart salud por animal
  const saludCount = {};
  salud.forEach(s=>{ saludCount[s.animalId]=(saludCount[s.animalId]||0)+1; });
  drawChart('chartSalud', Object.keys(saludCount), [{label:'Eventos', data:Object.values(saludCount)}]);

  // Chart lotes por animal
  const lotCount = {};
  lotes.forEach(l=>{ lotCount[l.animalId]=(lotCount[l.animalId]||0)+1; });
  drawChart('chartLotes', Object.keys(lotCount), [{label:'Usos', data:Object.values(lotCount)}]);
}

const chartRefs = {};
function drawChart(canvasId, labels, datasets){
  const ctx = document.getElementById(canvasId).getContext('2d');
  if(chartRefs[canvasId]) chartRefs[canvasId].destroy();
  chartRefs[canvasId] = new Chart(ctx, {
    type: 'bar',
    data: { labels, datasets },
    options: { responsive:true, plugins:{legend:{position:'bottom'}}, scales:{y:{beginAtZero:true}} }
  });
}

// ------- Sync (dummy endpoints + logs) (RF11) --------
const syncUrl = $('#syncUrl');
const syncLog = $('#syncLog');

$('#btnSyncUpload').addEventListener('click', async ()=>{
  const url = syncUrl.value.trim();
  const payload = await db.exportAll();
  if(!url){ log('Configura un endpoint para subir datos', 'warn'); download('agriwave_respaldo.json', JSON.stringify(payload, null, 2)); return; }
  try{
    const res = await fetch(url,{method:'POST', headers:{'Content-Type':'application/json'}, body:JSON.stringify(payload)});
    log('Respuesta del servidor: '+res.status);
  }catch(err){ log('Error al conectar: '+err.message, 'error'); }
});

$('#btnSyncDownload').addEventListener('click', async ()=>{
  const url = syncUrl.value.trim();
  if(!url){ log('Debes ingresar un endpoint que devuelva el JSON exportado', 'warn'); return; }
  try{
    const res = await fetch(url);
    const data = await res.json();
    await db.importAll(data);
    loadAnimales(); loadFin(); loadLotes(); loadSalud(); renderReportes();
    log('Datos importados desde el servidor.');
  }catch(err){ log('Error al descargar: '+err.message, 'error'); }
});

async function log(text, level='info'){
  const ts = new Date().toISOString();
  syncLog.textContent += `\n[${ts}] ${text}`;
  const item = { id: Date.now(), ts, level, text };
  await db.put('logs', item);
}

// Cargar datos al inicio
loadAnimales(); loadFin(); loadLotes(); loadSalud(); renderReportes();
