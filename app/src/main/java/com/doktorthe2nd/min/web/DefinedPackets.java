package com.doktorthe2nd.min.web;

import com.doktorthe2nd.min.Consts;

import java.util.HashMap;
import java.util.Map;

public abstract class DefinedPackets {
    public static void sessionInit() {
        Map<String, Object> payload = new HashMap<>();
        payload.put("mt_instance_id", Consts.instanceId);
        payload.put("userAgent", Consts.userAgent);
        payload.put("client_session_id", Consts.clientSessionId);
        payload.put("deviceId", Consts.deviceId);
        SocketCnt.send(OpcodeTable.sessionInit, payload);
    }
}
