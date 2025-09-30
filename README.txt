# AgriWave PWA (Instrucciones)
Para que todas las páginas (tabs) funcionen y se guarde la data:

1) **Servir por HTTP** (requerido para Service Worker y ES Modules). Opciones:
   - Python: `python -m http.server 8000` y abrir `http://localhost:8000/index.html`
   - VS Code: extensión **Live Server** (recomendado).

2) Abrir `index.html` desde `http://localhost:...` (no `file:///`).

3) Si no ves los animales guardados:
   - Abre la consola del navegador (F12), recarga, y verifica que no haya errores.
   - Prueba a limpiar caché de la PWA: Ajustes del sitio → Almacenamiento → Borrar.

La app es **Single Page Application**: todas las secciones están dentro de `index.html` como pestañas.
