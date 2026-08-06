package com.doktorthe2nd.min;

import android.content.Context;
import android.content.SharedPreferences;

import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public abstract class Consts {
    public static final String appVersion = "26.22.2";
    public static final int buildNumber = 6773;
    public static final String osVersion = "Android 67";
    public static final String deviceName = "Redmi SIXSEVEN67";

    // auto set
    public static String deviceId = "12345678ABCDEF90";
    public static final Map<String, Object> userAgent = new HashMap<>();
    public static final int clientSessionId = UUID.randomUUID().hashCode();
    public static final String instanceId = UUID.randomUUID().toString();

    public static void set(Context context) {
        //deviceId = getStringDeviceId(context);

        userAgent.put("deviceType", "ANDROID");
        userAgent.put("appVersion", Consts.appVersion);
        userAgent.put("osVersion", Consts.osVersion);
        userAgent.put("timezone", ZoneId.systemDefault().getId());
        userAgent.put("screen", "420dpi 420dpi 1080x2340");
        userAgent.put("pushDeviceType", "GCM");
        userAgent.put("arch", "arm64");
        userAgent.put("locale", "ru");
        userAgent.put("buildNumber", Consts.buildNumber);
        userAgent.put("deviceName", Consts.deviceName);
        userAgent.put("deviceLocale", "Ru");
    }

    private static String getStringDeviceId(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("device_identity", Context.MODE_PRIVATE);
        String deviceId = prefs.getString("device_id", null);

        if (deviceId == null) {
            // Generate a random UUID (just like the Dart plugin does)
            deviceId = UUID.randomUUID().toString();
            // Persist it so it remains stable for this app installation
            prefs.edit().putString("device_id", deviceId).apply();
        }

        return deviceId;
    }
}
