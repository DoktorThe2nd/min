package com.doktorthe2nd.min.web;

import java.util.Map;
import java.util.Set;

public class SessionManager {
    public String messageFromErrorPayload(Map<String, String> payload) {
        if (payload == null) return "Unknown error (payload null)";
        var msg = payload.get("message");
        if (msg != null && (msg.equals("FAIL_WRONG_PASSWORD") || msg.equals("FAIL_LOGIN_TOKEN"))) {
            return "Login error, try again";
        }
        for (var key : Set.of("localizedMessage", "message", "title")) {
            var out = payload.get(key);
            if (out != null) return out;
        }

        return "Unknown error";
    }

    public boolean isSessionExpiredPayload(Map<String, String> payload) {
        return payload != null && payload.get("message") != null &&
        (payload.get("message").equals("FAIL_WRONG_PASSWORD") ||
                payload.get("message").equals("FAIL_LOGIN_TOKEN"));
    }
}
