package com.doktorthe2nd.min.luaj;

import com.doktorthe2nd.min.Consts;
import com.doktorthe2nd.min.MainActivity;

import org.luaj.vm2.lib.ResourceFinder;

import java.io.IOException;
import java.io.InputStream;

public class LuaFromTrustedLoader implements ResourceFinder {
    @Override
    public InputStream findResource(String filename) {
        try {
            return MainActivity.appContext.getAssets().open(Consts.luaApiAssetsDir+"/"+filename);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
