package com.doktorthe2nd.min.luaj;

import android.content.res.AssetManager;
import android.util.Pair;

import com.doktorthe2nd.min.MainActivity;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.compiler.LuaC;
import org.luaj.vm2.lib.BaseLib;
import org.luaj.vm2.lib.Bit32Lib;
import org.luaj.vm2.lib.CoroutineLib;
import org.luaj.vm2.lib.MathLib;
import org.luaj.vm2.lib.PackageLib;
import org.luaj.vm2.lib.StringLib;
import org.luaj.vm2.lib.TableLib;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;
import org.luaj.vm2.lib.jse.JsePlatform;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class ScriptEngine {
    private static Globals trustedGlobals;
    private static Globals sandboxGlobals;

    /**
     * Run TRUSTED Lua script ON THIS THREAD
     * @param script Lua script to run
     * @return "OK" or result if succeeded / "ERROR: ..." if stopped with an error
     * @throws RuntimeException if {@link #init} was never called
     */
    public static String runTrusted(Script script) {
        if (trustedGlobals == null) throw new RuntimeException("Call ScriptEngine init first");
        if (script == null) return "ERROR: script is null";
        try {
            LuaValue result = script.loadAndCall(trustedGlobals);
            return result == null ? "OK" : result.tojstring();
        } catch (Throwable throwable) {
            return "ERROR: " + throwable.getMessage();
        }
    }

    /**
     * Run Lua script ON THIS THREAD
     * @param script Lua script to run
     * @return "OK" or result if succeeded / "ERROR: ..." if stopped with an error
     * @throws RuntimeException if {@link #init} was never called
     */
    public static String run(Script script) {
        if (sandboxGlobals == null) throw new RuntimeException("Call ScriptEngine init first");
        if (script == null) return "ERROR: script is null";
        try {
            LuaValue result = script.loadAndCall(sandboxGlobals);
            return result == null ? "OK" : result.tojstring();
        } catch (Throwable throwable) {
            return "ERROR: " + throwable.getMessage();
        }
    }

    /**
     * Run all Lua scripts from assets ON THIS THREAD
     * @return "OK" if all succeeded / "PATH: ERROR: ..." if any stopped with an error
     * @throws RuntimeException if {@link #init} was never called
     */
    public static String runAllAssets() {
        if (sandboxGlobals == null) throw new RuntimeException("Call ScriptEngine init first");
        List<String> paths = LuaFromAssetsLoader.walk();
        for (String path : paths) {
            try (InputStream is = new LuaFromAssetsLoader().findResourceNoRoot(path)) {
                if (is == null) continue;
                Script script = new Script(readInputStream(is));
                String answer;
                if (script.isTrusted()) answer = runTrusted(script);
                else answer = run(script);
                if (answer.startsWith("ERROR:")) return path + ": " + answer;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return "OK";
    }

    /**
     * Generates trusted env and sandbox. Should be called before any other interactions with Lua
     * @param api ScriptAPI for trusted scripts
     * @throws RuntimeException if api is null
     */
    public static void init(ScriptAPI api) {
        if (api == null) throw new RuntimeException("api is null");
        sandboxGlobals = new Globals();

        sandboxGlobals.load(new BaseLib());
        sandboxGlobals.load(new PackageLib());
        sandboxGlobals.load(new Bit32Lib());
        sandboxGlobals.load(new TableLib());
        sandboxGlobals.load(new StringLib());
        sandboxGlobals.load(new MathLib());
        sandboxGlobals.load(new CoroutineLib());

        sandboxGlobals.set("os", LuaValue.NIL);
        sandboxGlobals.set("io", LuaValue.NIL);
        sandboxGlobals.set("debug", LuaValue.NIL);
        sandboxGlobals.set("package", LuaValue.NIL);
        sandboxGlobals.set("loadlib", LuaValue.NIL);
        sandboxGlobals.set("dofile", LuaValue.NIL);
        sandboxGlobals.set("loadfile", LuaValue.NIL);

        trustedGlobals = JsePlatform.standardGlobals();
        trustedGlobals.set("api", CoerceJavaToLua.coerce(api));

        Require req = new Require(sandboxGlobals, trustedGlobals);

        trustedGlobals.set("require", req);
        sandboxGlobals.set("require", req);

        sandboxGlobals.set("_G", sandboxGlobals);
        LuaC.install(sandboxGlobals);
    }

    private ScriptEngine() {}

    public static List<String> pathsToIds(List<String> paths) {
        List<String> ret = new ArrayList<>();
        for (String str : paths) {
            String str3;
            if (str.endsWith(".lua")) str3 = str.substring(0, str.length()-4);
            else str3 = str;
            String str2 = str3.replace(File.separatorChar, '.');
            ret.add(str2);
        }
        return ret;
    }

    public static String readInputStream(InputStream in) {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] buffer = new byte[4096];
            int len;
            while ((len = in.read(buffer)) != -1) {
                out.write(buffer, 0, len);
            }
            return new String(out.toByteArray(), StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
