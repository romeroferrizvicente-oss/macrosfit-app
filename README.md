# MacrosFit — Compilar el APK desde el celular (sin PC)

Este proyecto ya viene listo para que GitHub compile el APK por ti, en la nube.
Solo necesitas subir esta carpeta a un repositorio de GitHub.

## Paso a paso (todo desde el navegador del celular)

1. Entra a **github.com** y crea una cuenta si no tienes una.
2. Crea un repositorio nuevo (botón "New repository"). Ponle de nombre, por ejemplo, `macrosfit-app`. No importa si es público o privado.
3. Dentro del repositorio recién creado, busca la opción para **subir archivos** ("Add file" → "Upload files").
4. Arrastra o selecciona **todo el contenido** de esta carpeta (`macrosfit-capacitor`), incluyendo la carpeta oculta `.github` con el archivo `build.yml` adentro. Asegúrate de que `package.json` quede en la raíz del repositorio (no dentro de otra carpeta).

   ⚠️ Importante: muchos navegadores de celular ocultan las carpetas que empiezan con un punto (como `.github`) al seleccionar archivos para subir. Si tu navegador no te deja seleccionar esa carpeta, usa la app oficial de GitHub o "Add file → Create new file" y escribe manualmente la ruta `.github/workflows/build.yml`, pegando el contenido de ese archivo.

5. Confirma la subida ("Commit changes"). En cuanto termine, GitHub empieza a compilar solo.
6. Ve a la pestaña **"Actions"** de tu repositorio. Verás un proceso corriendo (círculo amarillo). Espera unos 5-10 minutos hasta que se ponga en verde (✅).
7. Entra a ese proceso terminado y baja hasta la sección **"Artifacts"**. Ahí vas a ver **`macrosfit-apk`** — tócalo para descargarlo (te vas a bajar un .zip).
8. Abre ese .zip descargado y extrae el archivo `app-debug.apk`.
9. En tu Android, activa **"Instalar apps de origen desconocido"** (Ajustes → Seguridad, el nombre exacto varía según el celular).
10. Abre el APK descargado y instálalo.

## Para actualizar la app más adelante

Cada vez que quieras subir una corrección:
1. Ve a tu repositorio en GitHub.
2. Sube el nuevo `app.jsx` (u otros archivos que hayan cambiado) a la carpeta `www/`, reemplazando el anterior.
3. GitHub vuelve a compilar solo. Repite los pasos 6-10 para descargar el nuevo APK.

## ¿Qué incluye este proyecto?

- `www/` — el código de tu app (HTML + JSX + ícono), igual al que ya tenías funcionando en Netlify.
- `package.json` y `capacitor.config.json` — la configuración de Capacitor, incluyendo el plugin de **notificaciones locales nativas** (para que las alarmas suenen aunque cierres la app o bloquees el celular).
- `resources/icon.png` — tu logo, usado para generar automáticamente el ícono real de la app instalada.
- `.github/workflows/build.yml` — la receta que le dice a GitHub cómo compilar el APK solo.

## Nota sobre este primer APK

Esta primera versión genera un **APK de depuración (debug)** — perfecto para instalar y probar en tu propio celular. Si más adelante quieres publicarlo en Google Play, se necesita un paso adicional (firmarlo con una clave propia), que podemos preparar cuando llegue el momento.
