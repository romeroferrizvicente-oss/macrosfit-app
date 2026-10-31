package com.macrosfit.app;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;

import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

/**
 * Guarda archivos directo en la carpeta pública "Descargas" del teléfono.
 *
 * - save(): guarda todo en una sola llamada (para archivos chicos).
 * - begin() / append() / finish() / abort(): guarda en pedazos, para
 *   archivos pesados (copias de seguridad con fotos). Mandar un archivo
 *   enorme de golpe por el puente de Capacitor puede cerrar el WebView.
 */
@CapacitorPlugin(name = "MediaStoreSaver")
public class MediaStoreSaver extends Plugin {

    private OutputStream stream;
    private Uri pendingUri;

    private void closeQuietly() {
        try {
            if (stream != null) stream.close();
        } catch (Exception ignored) {
        }
        stream = null;
    }

    private void openFile(String filename, String mime) throws Exception {
        closeQuietly();
        pendingUri = null;
        ContentResolver resolver = getContext().getContentResolver();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ContentValues values = new ContentValues();
            values.put(MediaStore.MediaColumns.DISPLAY_NAME, filename);
            values.put(MediaStore.MediaColumns.MIME_TYPE, mime);
            values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS);
            values.put(MediaStore.MediaColumns.IS_PENDING, 1);
            Uri uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values);
            if (uri == null) throw new Exception("No se pudo crear el archivo en Descargas");
            pendingUri = uri;
            stream = resolver.openOutputStream(uri, "w");
            if (stream == null) throw new Exception("No se pudo abrir el archivo para escribir");
        } else {
            File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            if (!dir.exists()) dir.mkdirs();
            stream = new FileOutputStream(new File(dir, filename), false);
        }
    }

    private void closeFile() throws Exception {
        if (stream != null) {
            stream.flush();
            stream.close();
            stream = null;
        }
        if (pendingUri != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ContentValues values = new ContentValues();
            values.put(MediaStore.MediaColumns.IS_PENDING, 0);
            getContext().getContentResolver().update(pendingUri, values, null, null);
        }
        pendingUri = null;
    }

    private void discardFile() {
        closeQuietly();
        if (pendingUri != null) {
            try {
                getContext().getContentResolver().delete(pendingUri, null, null);
            } catch (Exception ignored) {
            }
            pendingUri = null;
        }
    }

    @PluginMethod
    public void save(PluginCall call) {
        String filename = call.getString("filename");
        String data = call.getString("data");
        String mime = call.getString("mimeType", "application/json");
        if (filename == null || data == null) {
            call.reject("Faltan filename o data");
            return;
        }
        try {
            openFile(filename, mime);
            stream.write(data.getBytes(StandardCharsets.UTF_8));
            closeFile();
            call.resolve();
        } catch (Exception e) {
            discardFile();
            call.reject("save: " + e.getMessage(), e);
        }
    }

    @PluginMethod
    public void begin(PluginCall call) {
        String filename = call.getString("filename");
        String mime = call.getString("mimeType", "application/json");
        if (filename == null) {
            call.reject("Falta filename");
            return;
        }
        try {
            openFile(filename, mime);
            call.resolve();
        } catch (Exception e) {
            discardFile();
            call.reject("begin: " + e.getMessage(), e);
        }
    }

    @PluginMethod
    public void append(PluginCall call) {
        String data = call.getString("data");
        if (stream == null || data == null) {
            call.reject("No hay un archivo abierto para escribir");
            return;
        }
        try {
            stream.write(data.getBytes(StandardCharsets.UTF_8));
            call.resolve();
        } catch (Exception e) {
            discardFile();
            call.reject("append: " + e.getMessage(), e);
        }
    }

    @PluginMethod
    public void finish(PluginCall call) {
        try {
            closeFile();
            call.resolve();
        } catch (Exception e) {
            discardFile();
            call.reject("finish: " + e.getMessage(), e);
        }
    }

    @PluginMethod
    public void abort(PluginCall call) {
        discardFile();
        call.resolve();
    }
}
