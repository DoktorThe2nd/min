package com.doktorthe2nd.min.modules.session;

import android.content.Context;
import android.content.SharedPreferences;

import com.doktorthe2nd.min.MainActivity;

public class SessionData {
    public String token;
    public String deviceId;
    public String phone;
    public String mt_instanceid;
    //sync sync state...

    public static void saveSession(SessionData session) {
        MainActivity.appContext
                .getSharedPreferences("session_data", Context.MODE_PRIVATE).edit()
                .putString("token", session.token)
                .putString("deviceId", session.deviceId)
                .putString("phone", session.phone)
                .putString("mt_instanceid", session.mt_instanceid)
                .apply();
    }

    public static SessionData loadSession() {
        SharedPreferences sharedPref = MainActivity.appContext
                .getSharedPreferences("session_data", Context.MODE_PRIVATE);
        SessionData session = new SessionData();
        session.token = sharedPref.getString("token", null);
        session.deviceId = sharedPref.getString("deviceId", null);
        session.phone = sharedPref.getString("phone", null);
        session.mt_instanceid = sharedPref.getString("mt_instanceid", null);
        return session;
    }
}
