package com.doktorthe2nd.min.luaj;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;

public class Script {
    private final Metadata metadata;
    private final String script;

    public boolean isTrusted() {
        return metadata.require_trusted;
    }
    public boolean isTrustedSafe() {
        return metadata.is_system;
    }

    public Script(String script) {
        this.script = script;
        this.metadata = Metadata.gather(script);
    }

    public Script(String script, boolean is_system) {
        this.script = script;
        this.metadata = Metadata.gather(script);
        this.metadata.is_system = is_system;
        this.metadata.require_trusted = is_system || this.metadata.require_trusted;
    }

    public LuaValue loadAndCall(Globals globals) {
        if (metadata.is_error) {
            if (metadata.unsatisfied_requires.isEmpty())
                return LuaValue.error("Metadata error: "+metadata.description+" - in module "+metadata.name);
            return LuaValue.error("Metadata error: Unsatisfied requires: "+metadata.unsatisfied_requires+" - in module "+metadata.name);
        }
        Require require;
        if (globals.get("require") instanceof Require) {
            require = (Require)globals.get("require");
        } else {
            return LuaValue.error("Require is not \"Require\" class");
        }
        require.allowedPush(Metadata.getIdList(metadata.requires), !metadata.require_trusted);
        LuaValue ret = globals.load(script, metadata.name).call();
        require.allowedPop();
        return ret;
    }
}
