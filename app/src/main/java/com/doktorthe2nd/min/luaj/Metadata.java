package com.doktorthe2nd.min.luaj;

import android.util.Pair;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Metadata {
    public String name = "Unnamed";
    public List<String> description = new ArrayList<>();
    public String author = "Unknown";
    public String version = "unknown";
    public List<Pair<Metadata, String>> requires = new ArrayList<>(); // meta of require and its id
    public boolean require_trusted = false;

    public boolean is_system = false;
    public List<String> unsatisfied_requires = new ArrayList<>();
    public boolean is_error = false;

    public static List<Metadata> getMetadataList(List<Pair<Metadata, String>> requires) {
        return requires.stream().map(it->it.first).collect(Collectors.toList());
    }
    public static List<String> getIdList(List<Pair<Metadata, String>> requires) {
        return requires.stream().map(it->it.second).collect(Collectors.toList());
    }

    public static Metadata gather(String fileData) {
        return gather(fileData, 0, false);
    }
    private static Metadata gather(String fileData, int scope, boolean is_system) {
        Metadata metadata = new Metadata();
        if (scope > 16) {
            metadata.is_error = true;
            metadata.description = List.of("Require chain is too long");
            return metadata;
        }
        List<String> lines = new ArrayList<>(List.of(fileData.split("\n")));
        lines.removeIf(item -> !item.startsWith("--"));
        boolean meta_open = false;
        for (String rLine : lines) {
            String line = rLine.replaceFirst("--", "").strip();
            List<String> words = new ArrayList<>(List.of(line.split(" ")));
            if (words.isEmpty()) {
                metadata.is_error = true;
                metadata.description = List.of("Line \"" + line + "\" is empty");
                return metadata;
            }
            String tag = words.get(0).toLowerCase();
            if (meta_open) {
                switch (tag) {
                    case "name":
                        metadata.name = line.replaceFirst("(?i)name", "").strip();
                        break;
                    case "description":
                        metadata.description.add(line.replaceFirst("(?i)description", "").strip());
                        break;
                    case "desc":
                        metadata.description.add(line.replaceFirst("(?i)desc", "").strip());
                        break;
                    case "author":
                        metadata.author = line.replaceFirst("(?i)author", "").strip();
                        break;
                    case "version":
                        metadata.version = line.replaceFirst("(?i)version", "").strip();
                        break;
                    case "require-trusted":
                        metadata.require_trusted = true;
                        break;
                    case "require":
                        if (words.size() < 2) {
                            metadata.is_error = true;
                            metadata.description = List.of("Line \"" + line + "\" is less than 2 words");
                            return metadata;
                        }
                        for (String id : words.subList(1, words.size())) {
                            Pair<String, Boolean> pair = findAndGiveRequire(id);
                            if (pair == null) {
                                metadata.unsatisfied_requires.add(id);
                                metadata.is_error = true;
                                continue;
                            }
                            Metadata metadata_req = gather(pair.first, scope+1, pair.second);
                            if (metadata_req.is_error) {
                                if (metadata_req.unsatisfied_requires.isEmpty()) {
                                    metadata.description = metadata_req.description;
                                    metadata.is_error = true;
                                    return metadata;
                                }
                                metadata.unsatisfied_requires.addAll(metadata_req.unsatisfied_requires);
                                metadata.is_error = true;
                                continue;
                            }
                            metadata.requires.add(Pair.create(metadata_req, id));
                        }
                        break;
                }
            }
            if (tag.equals("metadata"))
                meta_open = !meta_open;
        }
        if (is_system) {
            metadata.is_system = true;
            metadata.require_trusted = true;
        }
        return metadata;
    }

    private static Pair<String, Boolean> findAndGiveRequire(String require) {
        String relativePath = require.replace('.', '/') + ".lua";
        InputStream fis = new LuaFromImportedLoader().findResource(relativePath);
        if (fis != null) return Pair.create(ScriptEngine.readInputStream(fis), true);
        InputStream fis2 = new LuaFromTrustedLoader().findResource(relativePath);
        if (fis2 != null) return Pair.create(ScriptEngine.readInputStream(fis2), false);
        return null;
    }
}
