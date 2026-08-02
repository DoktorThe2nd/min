package com.doktorthe2nd.min.luaj;

import android.content.Context;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class ScriptInstaller {

    private static final String ASSETS_DIR = "scripts";
    private final Context context;
    private final File targetDir;

    public ScriptInstaller(Context context) {
        this.context = context.getApplicationContext();
        this.targetDir = new File(this.context.getFilesDir(), ASSETS_DIR);
    }

    public int install() {
        if (!targetDir.exists() && !targetDir.mkdirs()) return -1;

        String[] names;
        try {
            names = context.getAssets().list(ASSETS_DIR);
        } catch (IOException e) {
            return -1;
        }
        if (names == null) return 0;

        int installed = 0;
        for (String name : names) {
            File out = new File(targetDir, name);
            if (out.exists()) continue;

            try (InputStream in = context.getAssets().open(ASSETS_DIR + "/" + name);
                 OutputStream os = new FileOutputStream(out)) {
                byte[] buf = new byte[8192];
                int n;
                while ((n = in.read(buf)) > 0) {
                    os.write(buf, 0, n);
                }
                installed++;
            } catch (IOException e) {
                // skip
            }
        }
        return installed;
    }

    public List<File> list() {
        File[] files = targetDir.listFiles();
        if (files == null) return Collections.emptyList();
        List<File> result = new ArrayList<>();
        for (File f : files) {
            if (f.isFile() && f.getName().toLowerCase().endsWith(".lua")) {
                result.add(f);
            }
        }
        return result;
    }

    public File getDir() {
        return targetDir;
    }
}