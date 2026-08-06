package com.doktorthe2nd.min.web;

import com.github.luben.zstd.Zstd;

import org.msgpack.core.MessageBufferPacker;
import org.msgpack.core.MessagePack;
import org.msgpack.core.MessageUnpacker;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class PacketProcess {
    private static final int COMPRESSION_THRESHOLD = 32;
    private static final int ISOLATE_DECODE_THRESHOLD = 4096;
    private static final int HEADER_SIZE = 10;

    public static String messageFromErrorPayload(Map<String, Object> payload) {
        if (payload == null) return "Unknown error (payload null)";
        var msg = payload.get("message");
        if (msg != null && (msg.equals("FAIL_WRONG_PASSWORD") || msg.equals("FAIL_LOGIN_TOKEN"))) {
            return "Login error, try again";
        }
        for (var key : Set.of("localizedMessage", "message", "title")) {
            var out = payload.get(key);
            if (out != null) return out.toString();
        }

        return "Unknown error";
    }

    public static boolean isSessionExpiredPayload(Map<String, Object> payload) {
        return payload != null &&
        (Objects.equals(payload.get("message"), "FAIL_WRONG_PASSWORD") ||
                Objects.equals(payload.get("message"), "FAIL_LOGIN_TOKEN"));
    }

    public static void throwIfPacketError(Packet packet) {
        if (!packet.isError()) return;
        if (isSessionExpiredPayload(packet.payload))
            throw new SessionExpiredException(messageFromErrorPayload(packet.payload));
        throw new PacketException(messageFromErrorPayload(packet.payload));
    }

    public static boolean isSessionStateError(PacketException exception) {
        if (exception instanceof SessionExpiredException) return true;
        if (exception.getMessage() == null) return false;
        var text = exception.getMessage().toLowerCase();
        return text.contains("состояние сессии") ||
                text.contains("сессия не найдена") ||
                text.contains("авторизационная сессия") ||
                text.contains("сессия не онлайн");
    }

    public static byte[] packPacket(int opcode, Map<String, Object> payload) throws IOException {
        return packPacket(opcode, payload, 0);
    }
    public static byte[] packPacket(int opcode, Map<String, Object> payload, int seq) throws IOException {
        byte[] raw = MessagePackSerializer.serializeMap(payload);
        byte[] body;
        int flag;
        if (raw.length < COMPRESSION_THRESHOLD) {
            body = raw;
            flag = 0;
        }
        else {
            body = LZ4.compress(raw);
            flag = (raw.length / body.length) + 1;
        }

        byte[] out = new byte[HEADER_SIZE + body.length];
        ByteBuffer header = ByteBuffer.wrap(out, 0, HEADER_SIZE);
        header.order(ByteOrder.BIG_ENDIAN);

        header.put(0, (byte)10); // const
        header.put(1, (byte)Packet.CmdType.request);
        header.putShort(2, (short)seq);
        header.putShort(4, (short)opcode);
        int value = ((flag & 0xFF) << 24) | (body.length & 0xFFFFFF);
        header.putInt(6, value);
        System.arraycopy(body, 0, out, HEADER_SIZE, body.length);

        return out;
    }

    public static Packet unpackPacket(byte[] packet) {
        ByteBuffer header = ByteBuffer.wrap(packet, 0, HEADER_SIZE);
        int api = header.get(0) & 0xFF;
        int cmd = header.get(1) & 0xFF;
        int seq = header.getShort(2) & 0xFFFF;
        int opcode = header.getShort(4) & 0xFFFF;
        int packedLen = header.getInt(6);
        int compFlag = packedLen >> 24;
        int payloadLength = packedLen & 0xFFFFFF;

        if (payloadLength == 0) {
            return new Packet(api, cmd, seq, opcode);
        }

        if (HEADER_SIZE + payloadLength > packet.length) {
            throw new PacketException("Packet payload length "+payloadLength+" exceeds buffer");
        }
        ByteBuffer slice = ByteBuffer.wrap(packet, HEADER_SIZE, payloadLength);

        Map<String, Object> payload;
        if (true || compFlag == 0 && payloadLength < ISOLATE_DECODE_THRESHOLD) {
            payload = deserializePayload(slice.array(), compFlag);
        } else {
            //final owned = Uint8List.fromList(slice);
            //payload = await Isolate.run(() => _deserializePayload(owned, compFlag));
        }

        return new Packet(api, cmd, seq, opcode, payload);
    }

    public static Map<String, Object> deserializePayload(byte[] payloadBytes, int compFlag) {
        byte[] bytes = new byte[payloadBytes.length];
        if (compFlag != 0) {
            bytes = decompressPayload(bytes);
        }
        if (bytes.length == 0) return null;
        try {
            return MessagePackSerializer.deserializeMap(bytes);
        } catch (Exception e) {
            throw new PacketException("MsgPack deserialization error: "+e.getMessage());
        }
    }

    public static byte[] decompressPayload(byte[] src) {
        // Zstandard: magic 28 B5 2F FD (little-endian)
        if (src.length >= 4 &&
                src[0] == (byte)0x28 &&
                src[1] == (byte)0xB5 &&
                src[2] == (byte)0x2F &&
                src[3] == (byte)0xFD) {
            try {
                byte[] dest = new byte[LZ4.MAX_COMPRESSED_SIZE];
                Zstd.decompress(dest, src);
                return dest;
            } catch (Exception e) {
                throw new RuntimeException("Zstd decompression error: "+e.getMessage());
            }
        }

        // LZ4 frame: magic 04 22 4D 18
        if (src.length >= 4 &&
                src[0] == (byte)0x04 &&
                src[1] == (byte)0x22 &&
                src[2] == (byte)0x4D &&
                src[3] == (byte)0x18) {
            try {
                return LZ4.decompress(src);
            } catch (Exception e) {
                throw new RuntimeException("LZ4 frame decompression error: "+e.getMessage());
            }
        }

        // По умолчанию — LZ4 block (без magic)
        try {
            return LZ4.decompressBlock(src);
        } catch (Exception e) {
            throw new RuntimeException("LZ4 block decompression error: "+e.getMessage());
        }
    }
}
