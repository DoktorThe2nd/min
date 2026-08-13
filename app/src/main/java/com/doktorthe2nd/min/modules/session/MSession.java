package com.doktorthe2nd.min.modules.session;

import com.doktorthe2nd.min.Consts;
import com.doktorthe2nd.min.modules.MReporter;
import com.doktorthe2nd.min.net.Connection;
import com.doktorthe2nd.min.net.OpcodeTable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class MSession {
    public static void init(Runnable onAnswer) {
        Map<String, Object> payload = new HashMap<>(){{
            put("mt_instanceid", Consts.instanceId);
            put("userAgent", Consts.getUserAgent());
            put("clientSessionId", Consts.clientSessionId);
            put("deviceId", Consts.deviceId);
        }};
        Connection.sendRequest(OpcodeTable.sessionInit, payload, packet -> {
            Consts.callsSeed = (Long)Objects.requireNonNull(packet.payload.get("callsSeed"));
            onAnswer.run();
        });
    }
    public static String normalizePhone(String phone) {
        String normal = phone.replaceAll("[^0-9]", "");
        if (normal.length() != 11) return null;
        return "+"+normal;
    }
    private static String authToken;
    private static String gPhone;
    public static void authRequest(String phone, Runnable onAnswer) {
        gPhone = phone;
        Map<String, Object> payload = new HashMap<>(){{
            put("phone", phone);
            put("type", "START_AUTH");
            put("mode", Consts.getFingerprint());
        }};
        Connection.sendRequest(OpcodeTable.authRequest, payload, packet -> {
            if (MReporter.toastIfError(packet)) return;
            authToken = Objects.requireNonNull(packet.payload.get("token")).toString();
            onAnswer.run();
        });
    }
    private static String authTrackId;
    public static boolean gotPasswordChallenge() {
        return authTrackId != null;
    }
    public static void authSendCode(String code, Runnable onAnswer) {
        Map<String, Object> payload = new HashMap<>(){{
            put("token", authToken);
            put("verifyCode", code);
            put("auth_token_type", "CHECK_CODE");
        }};
        Connection.sendRequest(OpcodeTable.auth, payload, packet -> {
            if (MReporter.toastIfError(packet)) return;
            if (packet.payload.containsKey("passwordChallenge")) {
                authTrackId = ((Map<String, Object>)packet.payload.get("passwordChallenge")).get("trackId").toString();
                onAnswer.run();
                return;
            }
            onAnswer.run();
        });
    }
    public static void authSendPassword(String password, Runnable onAnswer) {
        Map<String, Object> payload = new HashMap<>(){{
            put("trackId", authTrackId);
            put("password", password);
        }};
        Connection.sendRequest(OpcodeTable.authLoginCheckPassword, payload, packet -> {
            if (MReporter.toastIfError(packet)) return;
            SessionData data = new SessionData();
            data.deviceId = Consts.deviceId;
            data.mt_instanceid = Consts.instanceId;
            data.phone = gPhone;
            Map<String, Object> attrs = (Map<String, Object>)packet.payload.get("tokenAttrs");
            Map<String, Object> LOGIN = (Map<String, Object>)attrs.get("LOGIN");
            data.token = LOGIN.get("token").toString();
            SessionData.saveSession(data);
            Consts.currentSession = data;
            onAnswer.run();
        });
    }
}
