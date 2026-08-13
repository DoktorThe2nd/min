package com.doktorthe2nd.min.modules.session;

import android.content.Context;
import android.content.SharedPreferences;

import com.doktorthe2nd.min.Consts;
import com.doktorthe2nd.min.MainActivity;
import com.doktorthe2nd.min.modules.sync.SyncState;

public class SessionData {
    public String token;
    public String deviceId;
    public String phone;
    public String mt_instanceid;
    public SyncState sync = new SyncState();

    public static void saveSession(SessionData session) {
        SharedPreferences.Editor sharedPref = MainActivity.appContext
                .getSharedPreferences("session_data", Context.MODE_PRIVATE).edit();
        sharedPref.putString("token", session.token);
        sharedPref.putString("deviceId", session.deviceId);
        sharedPref.putString("phone", session.phone);
        sharedPref.putString("mt_instanceid", session.mt_instanceid);
        sharedPref.putInt("sync-chats_sync", session.sync.chats_sync);
        sharedPref.putInt("sync-contacts_sync", session.sync.contacts_sync);
        sharedPref.putInt("sync-drafts_sync", session.sync.drafts_sync);
        sharedPref.putInt("sync-presence_sync", session.sync.presence_sync);
        sharedPref.putString("sync-config_hash", session.sync.config_hash);
        sharedPref.apply();
    }

    public static boolean isSessionSaved() {
        SharedPreferences sharedPref = MainActivity.appContext
                .getSharedPreferences("session_data", Context.MODE_PRIVATE);
        return sharedPref.contains("token");
    }

    public static SessionData loadSession() {
        SharedPreferences sharedPref = MainActivity.appContext
                .getSharedPreferences("session_data", Context.MODE_PRIVATE);
        SessionData session = new SessionData();
        session.token = sharedPref.getString("token", "NULL_TOKEN");
        session.deviceId = sharedPref.getString("deviceId", Consts.deviceId);
        session.phone = sharedPref.getString("phone", "NULL_PHONE");
        session.mt_instanceid = sharedPref.getString("mt_instanceid", Consts.instanceId);
        session.sync.chats_sync = sharedPref.getInt("sync-chats_sync", -1);
        session.sync.contacts_sync = sharedPref.getInt("sync-contacts_sync", -1);
        session.sync.drafts_sync = sharedPref.getInt("sync-drafts_sync", -1);
        session.sync.presence_sync = sharedPref.getInt("sync-presence_sync", -1);
        session.sync.config_hash = sharedPref.getString("sync-config_hash", "NULL_HASH");
        return session;
    }
}
