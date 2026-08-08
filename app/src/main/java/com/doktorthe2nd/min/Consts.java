package com.doktorthe2nd.min;

import android.util.Pair;

import com.doktorthe2nd.min.web.ApkBuildFingerprint;
import com.doktorthe2nd.min.web.FingerprintGenerator;

import java.time.ZoneId;
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

    // fingerprint https://github.com/MaxApiTeam/PyMax/blob/main/src/pymax/_data/apk_fingerprints.json#L410
    public static final String appVersion = "26.18.4";
    public static final int buildNumber = 6724;

    // auto set
    public static long callsSeed = 0; // из ответа на sessionInit
    public static final int clientSessionId = UUID.randomUUID().hashCode();
    public static final String instanceId = UUID.randomUUID().toString();
    public static final String deviceId = UUID.randomUUID().toString();

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
                "bf5c685810d20e9dde60d142169329f1fafbdc0d4b64de853bf1b45f4e36250c",// dexMetaSha256
                new HashMap<>() {{
                    put("arm64-v8a", "c77b89270f44bd26c218a946c18911f2b156312693ea00b419d169b71c1ed111"); // soMetaSha256 для arm64
                    //put("armeabi-v7a", "deadbeef9999..."); // soMetaSha256 для arm32
                }}
        );
        data.put("26.18.4", modelV1);
        return new FingerprintGenerator(data);
    }
}
