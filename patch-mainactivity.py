import re

path = "android/app/src/main/java/com/macrosfit/app/MainActivity.java"
with open(path, "r", encoding="utf-8") as f:
    content = f.read()

if "MediaStoreSaver" not in content:
    if "import android.os.Bundle;" not in content:
        content = content.replace(
            "import com.getcapacitor.BridgeActivity;",
            "import android.os.Bundle;\nimport com.getcapacitor.BridgeActivity;",
            1,
        )

    match = re.search(r"public class MainActivity extends BridgeActivity\s*\{", content)
    if not match:
        raise SystemExit("No se encontró la clase MainActivity para parchear (revisa el formato generado por Capacitor).")

    insert_pos = match.end()
    method = (
        "\n    @Override\n"
        "    public void onCreate(Bundle savedInstanceState) {\n"
        "        registerPlugin(MediaStoreSaver.class);\n"
        "        super.onCreate(savedInstanceState);\n"
        "    }\n"
    )
    content = content[:insert_pos] + method + content[insert_pos:]

    with open(path, "w", encoding="utf-8") as f:
        f.write(content)
    print("MainActivity.java parcheado correctamente.")
else:
    print("MainActivity.java ya tenía el plugin registrado.")
