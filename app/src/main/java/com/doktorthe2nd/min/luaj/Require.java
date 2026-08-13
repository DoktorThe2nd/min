package com.doktorthe2nd.min.luaj;

import android.util.Pair;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;

import java.io.InputStream;
import java.util.ArrayDeque;
import java.util.List;
import java.util.NoSuchElementException;

public class Require extends OneArgFunction {
    private final LuaTable loaded = new LuaTable();
    private final ArrayDeque<Pair<List<String>, Boolean>> allowed_stack = new ArrayDeque<>();
    private final Globals sandbox;
    private final Globals trusted;

    private static final LuaFromTrustedLoader TRUSTED_LOADER = new LuaFromTrustedLoader();
    private static final LuaFromImportedLoader IMPORTED_LOADER = new LuaFromImportedLoader();

    public Require(Globals sandbox, Globals trusted) {
        this.sandbox = sandbox;
        this.trusted = trusted;
    }

    public void allowedPush(List<String> list, boolean restricted) {
        allowed_stack.push(Pair.create(list, restricted));
    }
    public void allowedPop() {
        allowed_stack.pop();
    }

    @Override
    public LuaValue call(LuaValue arg) {
        String moduleName = arg.checkjstring();
        Pair<List<String>, Boolean> allowed = allowed_stack.peek();
        if (allowed == null) allowed = Pair.create(List.of(), true);
        if (allowed.second && !allowed.first.contains(moduleName))
            return LuaValue.error("Module '"+moduleName+"' is not allowed (you forgot to add it to metadata?)");
        LuaValue cached = loaded.get(moduleName);
        if (!cached.isnil()) return cached;
        String relativePath = moduleName.replace('.', '/') + ".lua";

        InputStream sandbox_fis = IMPORTED_LOADER.findResource(relativePath);
        if (sandbox_fis != null) {
            Script script = new Script(ScriptEngine.readInputStream(sandbox_fis));
            LuaValue result = script.loadAndCall(sandbox);
            if (result.isnil()) result = LuaValue.TRUE;
            loaded.set(moduleName, result);
            return result;
        }

        InputStream trusted_fis = TRUSTED_LOADER.findResource(relativePath);
        if (trusted_fis != null) {
            Script script = new Script(ScriptEngine.readInputStream(trusted_fis), true);
            LuaValue result = script.loadAndCall(trusted);
            if (result.isnil()) result = LuaValue.TRUE;
            loaded.set(moduleName, result);
            return result;
        }

        return LuaValue.error("Module '"+moduleName+"' not found");
    }
}
