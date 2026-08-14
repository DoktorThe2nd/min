package com.doktorthe2nd.min.modules.sync;

import android.os.SystemClock;

import com.doktorthe2nd.min.Consts;
import com.doktorthe2nd.min.net.Connection;
import com.doktorthe2nd.min.net.OpcodeTable;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class MSync {
    private static Map<String, Object> genPayload() {
        return new HashMap<>(){{
            put("userAgent", Consts.getUserAgent());
            put("token", Consts.currentSession.token);
            put("chatCacheFingerprint", Consts.getFingerprint());
            put("chatsSync", Consts.currentSession.sync.chats_sync);
            put("contactsSync", Consts.currentSession.sync.contacts_sync);
            put("draftsSync", Consts.currentSession.sync.drafts_sync);
            put("interactive", true);
            put("presenceSync", Consts.currentSession.sync.presence_sync);
            put("exp", new HashMap<>(){{
                put("chatsCountGroups", new byte[]{10, 50});
            }});
            put("configHash", Consts.currentSession.sync.config_hash);
        }};
    }

    public static void sendLogin() {
        Connection.sendRequest(OpcodeTable.login, genPayload(), packet -> {

        });
    }
}
