package com.doktorthe2nd.min.luaj;

import com.doktorthe2nd.min.MainActivity;

import org.luaj.vm2.lib.ResourceFinder;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

public class LuaFromImportedLoader implements ResourceFinder {
    @Override
    public InputStream findResource(String filename) {
        try {
            String root = MainActivity.appContext.getFilesDir()+File.separator+"imported_scripts";
            File candidate = new File(root, filename);
            // Проверка безопасности: путь не должен выходить за пределы корня
            String canon = candidate.getCanonicalPath();
            String rootCanon = new File(root).getCanonicalPath();
            if (canon.startsWith(rootCanon + File.separator) || canon.equals(rootCanon)) {
                if (candidate.exists() && candidate.isFile()) {
                    return new FileInputStream(candidate);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return null;
    }
}
