package com.doktorthe2nd.min.web;

import java.util.HashMap;
import java.util.Map;

public class Packet {
    private static final int HEADER_SIZE = 10; // ver(1) + cmd(1) + seq(2) + opcode(2) + packedLen(4) = 10
    private static final int MAX_COMPRESSED_SIZE = 32*1024*1024; // 32 MB

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

    private int api = 10; // 10 - default
    private int cmd = 0;
    private int seq = 0;
    private int opcode = 0;
    private final Map<String, String> payload = new HashMap<>();

    public boolean isOk() { return cmd == CmdType.ok; }
    public boolean isError() { return cmd == CmdType.error; }
    public boolean isPush() { return cmd == CmdType.push; }

    public String to_string() {
        return "Packet(ver="+api+" cmd="+cmd+" seq="+seq+" opcode="+opcode+" payload="+payload.toString()+")";
    }
}