package com.doktorthe2nd.min;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import com.doktorthe2nd.min.luaj.ScriptEngine;
import com.doktorthe2nd.min.luaj.ScriptInstaller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

public class MainActivity extends Activity {
    private static final int REQ_PICK_SCRIPT = 1001;
    private ScriptInstaller installer;
    private TextView log;
    private ActivityResultLauncher<Intent> picker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        log = findViewById(R.id.log);

        installer = new ScriptInstaller(this);
        int added = installer.install();
        log.append("Installed: " + added + " script(s)\n");

        Button run = findViewById(R.id.run);
        run.setOnClickListener(v -> runAll());

        Button add = findViewById(R.id.add);
        add.setOnClickListener(v -> {
            Intent i = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            i.addCategory(Intent.CATEGORY_OPENABLE);
            i.setType("*/*");
            startActivityForResult(i, REQ_PICK_SCRIPT);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode != REQ_PICK_SCRIPT) return;
        if (resultCode != RESULT_OK || data == null || data.getData() == null) return;

        importScript(data.getData());
    }

    private void importScript(Uri uri) {
        try {
            String name = queryName(uri);
            if (!name.toLowerCase().endsWith(".lua")) name = name + ".lua";
            File out = new File(installer.getDir(), name);

            try (InputStream in = getContentResolver().openInputStream(uri);
                 FileOutputStream os = new FileOutputStream(out)) {
                if (in != null) {
                    byte[] buf = new byte[8192];
                    int n;
                    while ((n = in.read(buf)) > 0) os.write(buf, 0, n);
                }
            }
            Toast.makeText(this, "Imported: " + name, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "Import failed", Toast.LENGTH_SHORT).show();
        }
    }

    private String queryName(Uri uri) {
        String name = "script_" + System.currentTimeMillis() + ".lua";
        try (android.database.Cursor c = getContentResolver().query(uri, null, null, null, null)) {
            if (c != null && c.moveToFirst()) {
                int idx = c.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME);
                if (idx >= 0) name = c.getString(idx);
            }
        }
        return name;
    }

    private void runAll() {
        ScriptEngine.ScriptAPI api = text ->
                Toast.makeText(this, text, Toast.LENGTH_SHORT).show();

        List<File> files = installer.list();
        log.append("Found: " + files.size() + "\n");
        for (File f : files) {
            String res = ScriptEngine.run(f, api);
            log.append(f.getName() + " → " + res + "\n");
        }
    }
}