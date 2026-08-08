package com.doktorthe2nd.min.modules.session;

import com.doktorthe2nd.min.Consts;
import com.doktorthe2nd.min.modules.MReporter;
import com.doktorthe2nd.min.web.Connection;
import com.doktorthe2nd.min.web.OpcodeTable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class MSession {
    public static void init() {
        Map<String, Object> payload = new HashMap<>(){{
            put("mt_instanceid", Consts.instanceId);
            put("userAgent", Consts.getUserAgent());
            put("clientSessionId", Consts.clientSessionId);
            put("deviceId", Consts.deviceId);
        }};
        Connection.sendRequest(OpcodeTable.sessionInit, payload, packet ->
                Consts.callsSeed = (Long)Objects.requireNonNull(packet.payload.get("callsSeed")));
    }
    private static String authToken;
    public static void authRequest(String phone) {
        Map<String, Object> payload = new HashMap<>(){{
            put("phone", phone);
            put("type", "START_AUTH");
            put("mode", Consts.getFingerprint());
        }};
        Connection.sendRequest(OpcodeTable.authRequest, payload, packet -> {
            if (MReporter.toastIfError(packet)) return;
            MReporter.toast("Wait for code");
            authToken = Objects.requireNonNull(packet.payload.get("token")).toString();
        });
    }
    private static String authTrackId;
    public static void authSendCode(String code) {
        Map<String, Object> payload = new HashMap<>(){{
            put("token", authToken);
            put("verifyCode", code);
            put("auth_token_type", "CHECK_CODE");
        }};
        Connection.sendRequest(OpcodeTable.auth, payload, packet -> {
            if (MReporter.toastIfError(packet)) return;
            if (packet.payload.containsKey("passwordChallenge")) {
                MReporter.toast("Needs password");
                authTrackId = ((Map<String, Object>)packet.payload.get("passwordChallenge")).get("trackId").toString();
                return;
            }
            MReporter.toast("Got it!");
        });
    }
    public static void authSendPassword(String password) {
        Map<String, Object> payload = new HashMap<>(){{
            put("trackId", authTrackId);
            put("password", password);
        }};
        Connection.sendRequest(OpcodeTable.authLoginCheckPassword, payload, packet -> {
            if (MReporter.toastIfError(packet)) return;
            MReporter.toast("Got it!");
        });
    }
}
