package com.doktorthe2nd.min.web;

import androidx.annotation.NonNull;

import java.util.HashMap;
import java.util.Map;

public class Packet {

    abstract static class CmdType {
        static final int request = 0;
        static final int push = 0;
        static final int ok = 1;
        static final int notFound = 2;
        static final int error = 3;
    }

// Распакованный бинарный пакет
//
// Формат заголовка (10 байт):
// ```
// [0]      ver       — версия протокола (uint8) (по умолчанию 10)
// [1]   cmd       — тип команды (uint8) (при отправке от клиента равно 0)
// [2..3]      seq       — порядковый номер (uint16 BE)
// [4..5]   opcode    — код операции (uint16 BE)
// [6..9]   packedLen — флаг сжатия [6] + длина payload [7..9] (uint32 BE)
// [10..]   payload   — данные в MsgPack, опционально сжатые LZ4
// ```

    public int api = 10; // 10 - default
    public int cmd = 0;
    public int seq = 0;
    public int opcode = 0;
    public Map<String, Object> payload = new HashMap<>();

    Packet() {}

    Packet(int _api, int _cmd, int _seq, int _opcode) {
        this(_api, _cmd, _seq, _opcode, null);
    }

    Packet(int _api, int _cmd, int _seq, int _opcode, Map<String, Object> _payload) {
        api = _api;
        cmd = _cmd;
        seq = _seq;
        opcode = _opcode;
        if (_payload != null) payload = _payload;
    }

    public boolean isOk() { return cmd == CmdType.ok; }
    public boolean isError() { return cmd == CmdType.error; }
    public boolean isPush() { return cmd == CmdType.push; }

    @NonNull
    public String toString() {
        return "Packet(ver="+api+" cmd="+cmd+" seq="+seq+" opcode="+opcode+" payload="+payload.toString()+")";
    }
}