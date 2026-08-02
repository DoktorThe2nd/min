package com.doktorthe2nd.min.luaj;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LoadState;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.VarArgFunction;
import org.luaj.vm2.lib.ZeroArgFunction;
import org.luaj.vm2.lib.jse.JsePlatform;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Set;

public class ScriptEngine {
    public interface ScriptAPI {
        void showNotification(String text);
    }
    private static final Set<String> RESTRICTED = Set.of(
            "os", "io", "debug", "package", "require", "loadlib", "dofile", "loadfile"
    );
    private static final long DEFAULT_TIMEOUT_MS = 2000L;
    private static final long MAX_SCRIPT_SIZE = 100000L;

    public static String run(File file, ScriptAPI api) {
        return run(file, api, DEFAULT_TIMEOUT_MS);
    }
    public static String run(File file, ScriptAPI api, long timeoutMs) {
        if (file == null || !file.exists() || !file.isFile()) return "File not found";
        if (file.length() > MAX_SCRIPT_SIZE) return "Script is too big";
        LuaValue[] result = new LuaValue[1];
        Throwable[] error = new Throwable[1];

        Thread worker = new Thread(() -> {
            try (InputStream fis = new FileInputStream(file);) {
                Globals globals = createSandboxedGlobals(api);
                byte[] buf = new byte[Math.toIntExact(file.length())];
                fis.read(buf);
                result[0] = globals.load(new String(buf, StandardCharsets.UTF_8)).call();
            } catch (Throwable t) {
                error[0] = t;
            }
        }, "lua-script-worker");
        worker.setDaemon(true);
        worker.start();

        try {
            worker.join(timeoutMs);
        } catch (InterruptedException e) {
            return "Interrupted";
        }

        if (worker.isAlive()) {
            worker.stop();
            return "Timeout";
        }
        if (error[0] != null) return "Error: " + error[0].getMessage();
        return result[0] == null ? "OK" : result[0].toString();
    }

    private static Globals createSandboxedGlobals(ScriptAPI api) {
        Globals globals = JsePlatform.standardGlobals();

        RESTRICTED.forEach(lib -> globals.set(lib, LuaValue.NIL));

        globals.set("print", new VarArgFunction() {
            @Override
            public Varargs invoke(Varargs args) {
                StringBuilder sb = new StringBuilder();
                for (int i = 1; i <= args.narg(); i++) {
                    if (i > 1) sb.append('\t');
                    sb.append(args.checkjstring(i));
                }
                System.out.println("[lua] " + sb);
                return LuaValue.TRUE;
            }
        });

        globals.set("notify", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue arg) {
                if (api != null) api.showNotification(arg.tojstring());
                return LuaValue.TRUE;
            }
        });

        globals.set("getTime", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return LuaValue.valueOf(System.currentTimeMillis());
            }
        });

        return globals;
    }

    private ScriptEngine() {}
}
