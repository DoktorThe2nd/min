package com.doktorthe2nd.min.luaj;

import android.content.res.AssetManager;

import com.doktorthe2nd.min.Consts;
import com.doktorthe2nd.min.MainActivity;

import org.luaj.vm2.lib.ResourceFinder;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class LuaFromAssetsLoader implements ResourceFinder {
    private static final String rootPath = Consts.luaBuiltInScripts;

    @Override
    public InputStream findResource(String filename) {
        try {
            return MainActivity.appContext.getAssets().open(rootPath+File.separator+filename);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public InputStream findResourceNoRoot(String filename) {
        try {
            return MainActivity.appContext.getAssets().open(filename);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<String> walk() {
        return walk(rootPath);
    }
    public static List<String> walk(String rootPath) {
        List<String> fileList = new ArrayList<>();
        AssetManager am = MainActivity.appContext.getAssets();
        try {
            String[] items = am.list(rootPath);
            if (items == null) return fileList;
            for (String item : items) {
                String fullPath = rootPath.isEmpty() ? item : rootPath + File.separator + item;
                // Проверяем, является ли папкой: пытаемся открыть как файл, если ошибка — значит папка
                try (InputStream is = am.open(fullPath)) {
                    // Если открылось — это файл
                    fileList.add(fullPath);
                } catch (IOException e) {
                    // Если не открылось — вероятно, папка, рекурсивно обходим
                    fileList.addAll(walk(fullPath));
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return fileList;
    }
}
