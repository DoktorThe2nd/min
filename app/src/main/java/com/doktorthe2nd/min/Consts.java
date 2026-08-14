package com.doktorthe2nd.min;

import android.util.Pair;

import com.doktorthe2nd.min.modules.session.SessionData;
import com.doktorthe2nd.min.net.ApkBuildFingerprint;
import com.doktorthe2nd.min.net.FingerprintGenerator;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Consts {
    public static final Pair<String, Integer> server = Pair.create("api.oneme.ru", 443);
    public static final String osVersion = "Android 14";
    public static final String deviceName = "Redmi Note 12";

    public static final int max_queue_length = 64;
    public static final int max_compressed_size = 32*1024*1024;
    public static final int compression_threshold = 512;

    public static final String luaApiAssetsDir = "lua_modules";
    public static final String luaBuiltInScripts = "lua_scripts";
    public static final int luaMaxLength = 100000; // does not affect luaApiAssetsDir/*

    // fingerprint https://github.com/MaxApiTeam/PyMax/blob/main/src/pymax/_data/apk_fingerprints.json#L410
    public static final String appVersion = "26.19.1";
    public static final int buildNumber = 6729;

    // auto set
    public static SessionData currentSession; // из MSession::run
    public static long callsSeed = 0; // из ответа на sessionInit
    public static final int clientSessionId = UUID.randomUUID().hashCode();
    public static String instanceId = UUID.randomUUID().toString();
    public static String deviceId = UUID.randomUUID().toString();

    public static Map<String, Object> getUserAgent() {
        Map<String, Object> userAgent = new HashMap<>();
        userAgent.put("deviceType", "ANDROID");
        userAgent.put("appVersion", Consts.appVersion);
        userAgent.put("osVersion", Consts.osVersion);
        userAgent.put("timezone", "Europe/Moscow");
        userAgent.put("screen", "420dpi 420dpi 1080x2340");
        userAgent.put("pushDeviceType", "GCM");
        userAgent.put("arch", "arm64-v8a");
        userAgent.put("locale", "ru");
        userAgent.put("buildNumber", Consts.buildNumber);
        userAgent.put("deviceName", Consts.deviceName);
        userAgent.put("deviceLocale", "ru");
        return userAgent;
    }

    public static byte[] getFingerprint() {
        FingerprintGenerator fingerprintGenerator = getFingerprintGenerator();
        byte[] fingerprint = fingerprintGenerator.generateFingerprint(appVersion, deviceId, callsSeed);
        if (fingerprint == null) throw new RuntimeException("Unable to generate fingerprint");
        return fingerprint;
    }
    private static FingerprintGenerator getFingerprintGenerator() {
        Map<String, ApkBuildFingerprint> data = new HashMap<>();
        ApkBuildFingerprint modelV1 = new ApkBuildFingerprint(
                "1684414033eb263e2c615f8b7df5ed8793850a07656304997fbf07e9e21e1e93",// certificateMetaSha256
                "0fefb0ece6b4d59f0b7a25ccbc403dfa1492e73b13b3f94d80f585a2d21f7de4",// dexMetaSha256
                new HashMap<>() {{
                    put("arm64-v8a", "88ba23d1352a2c4c0ec92d6e96c41b3494e7346a1409c97158e494256d0ebbdb"); // soMetaSha256 для arm64
                    //put("armeabi-v7a", "deadbeef9999..."); // soMetaSha256 для arm32
                }}
        );
        data.put(Consts.appVersion, modelV1);
        return new FingerprintGenerator(data);
    }
}
