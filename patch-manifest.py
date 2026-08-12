import re

path = "android/app/src/main/AndroidManifest.xml"
with open(path, "r", encoding="utf-8") as f:
    content = f.read()

PERMISSION = '    <uses-permission android:name="android.permission.SCHEDULE_EXACT_ALARM" />\n'

if "SCHEDULE_EXACT_ALARM" not in content:
    match = re.search(r"<manifest[^>]*>", content)
    if not match:
        raise SystemExit("No se encontró la etiqueta <manifest> para parchear.")

    insert_pos = match.end()
    content = content[:insert_pos] + "\n" + PERMISSION + content[insert_pos:]

    with open(path, "w", encoding="utf-8") as f:
        f.write(content)
    print("AndroidManifest.xml parcheado: se agregó SCHEDULE_EXACT_ALARM.")
else:
    print("AndroidManifest.xml ya tenía el permiso de alarma exacta.")
