package com.macrosfit.app;

import android.content.ContentValues;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;

import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

// Guarda un archivo directo en la carpeta pública "Descargas" del
// teléfono, sin abrir ningún selector ni pedir elegir una app. En Android
// 10+ usa la API oficial de MediaStore (la misma que usa Chrome para
// descargar archivos) — no necesita ningún permiso especial. En versiones
// más viejas escribe directo al archivo.
@CapacitorPlugin(name = "MediaStoreSaver")
public class MediaStoreSaver extends Plugin {

    @PluginMethod
    public void save(PluginCall call) {
        String filename = call.getString("filename");
        String data = call.getString("data");
        String mimeType = call.getString("mimeType", "application/json");

        if (filename == null || data == null) {
            call.reject("Falta 'filename' o 'data'");
            return;
        }

        try {
            byte[] bytes = data.getBytes("UTF-8");

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Downloads.DISPLAY_NAME, filename);
                values.put(MediaStore.Downloads.MIME_TYPE, mimeType);
                values.put(MediaStore.Downloads.IS_PENDING, 1);

                Uri collection = MediaStore.Downloads.EXTERNAL_CONTENT_URI;
                Uri item = getContext().getContentResolver().insert(collection, values);

                if (item == null) {
                    call.reject("No se pudo crear el archivo en Descargas");
                    return;
                }

                OutputStream out = getContext().getContentResolver().openOutputStream(item);
                out.write(bytes);
                out.close();

                values.clear();
                values.put(MediaStore.Downloads.IS_PENDING, 0);
                getContext().getContentResolver().update(item, values, null, null);

                JSObject ret = new JSObject();
                ret.put("uri", item.toString());
                call.resolve(ret);
            } else {
                File downloads = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                if (!downloads.exists()) downloads.mkdirs();
                File outFile = new File(downloads, filename);
                FileOutputStream fos = new FileOutputStream(outFile);
                fos.write(bytes);
                fos.close();

                JSObject ret = new JSObject();
                ret.put("uri", outFile.getAbsolutePath());
                call.resolve(ret);
            }
        } catch (Exception e) {
            call.reject("Error guardando archivo: " + e.getMessage(), e);
        }
    }
}
