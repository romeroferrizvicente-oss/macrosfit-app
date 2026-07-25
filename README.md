# MacrosFit — Compilar el APK desde el celular (sin PC)

Todos los archivos de este proyecto están **sueltos, sin carpetas** (excepto
`.github/workflows/build.yml`, que GitHub exige que esté en esa ruta exacta).
Esto es a propósito, para que puedas subirlos desde el navegador del celular
sin problema. La receta de compilación reconstruye las carpetas que hacen
falta automáticamente, en la nube.

## Archivos que debes subir a la raíz del repositorio

- `app.jsx`
- `index.html`
- `icon.png`
- `manifest.json`
- `package.json`
- `capacitor.config.json`
- `.gitignore`
- `README.md` (este archivo)

Sube estos con "Add file" → "Upload files", seleccionándolos todos juntos.

## El único archivo que hay que crear a mano: `.github/workflows/build.yml`

1. En tu repositorio, toca "Add file" → **"Create new file"**.
2. En el campo del nombre, escribe exactamente: `.github/workflows/build.yml`
   (al escribir las barras "/", GitHub crea las carpetas solo).
3. Pega el contenido del archivo `build.yml` que viene en este mismo ZIP.
4. Toca "Commit changes".

## Después de subir todo

1. Ve a la pestaña **"Actions"** de tu repositorio.
2. Espera a que el proceso se ponga en verde (✅), unos 5-10 minutos.
3. Entra a ese proceso → baja hasta "Artifacts" → descarga **`macrosfit-apk`**.
4. Extrae el .zip descargado y ahí está `app-debug.apk`.
5. Activa "Instalar apps de origen desconocido" en tu Android e instálalo.

## Para actualizar la app más adelante

Sube el `app.jsx` nuevo a la raíz (reemplazando el anterior) y GitHub vuelve
a compilar solo.
