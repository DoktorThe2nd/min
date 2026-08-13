package com.doktorthe2nd.min.net;

import org.msgpack.core.MessageBufferPacker;
import org.msgpack.core.MessageFormat;
import org.msgpack.core.MessagePack;
import org.msgpack.core.MessageUnpacker;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MessagePackSerializer {

    public static byte[] serializeMap(Map<String, Object> map) throws IOException {
        try (MessageBufferPacker packer = MessagePack.newDefaultBufferPacker()) {
            packMap(packer, map);
            return packer.toByteArray();
        }
    }

    // Рекурсивная упаковка Map
    private static void packMap(MessageBufferPacker packer, Map<String, Object> map) throws IOException {
        packer.packMapHeader(map.size());
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            packer.packString(entry.getKey());   // ключ всегда строка
            packValue(packer, entry.getValue()); // упаковываем значение любого типа
        }
    }

    // Универсальная упаковка значения
    private static void packValue(MessageBufferPacker packer, Object value) throws IOException {
        if (value == null) {
            packer.packNil();
        } else if (value instanceof String) {
            packer.packString((String) value);
        } else if (value instanceof Integer) {
            packer.packInt((Integer) value);
        } else if (value instanceof Long) {
            packer.packLong((Long) value);
        } else if (value instanceof Boolean) {
            packer.packBoolean((Boolean) value);
        } else if (value instanceof Float) {
            packer.packFloat((Float) value);
        } else if (value instanceof Double) {
            packer.packDouble((Double) value);
        } else if (value instanceof byte[]) {
            packer.packBinaryHeader(((byte[])value).length);
            packer.writePayload((byte[])value);
        } else if (value instanceof Map) {
            // Вложенный словарь – рекурсивный вызов
            @SuppressWarnings("unchecked")
            Map<String, Object> nestedMap = (Map<String, Object>) value;
            packMap(packer, nestedMap);
        } else if (value instanceof List) {
            // Упаковка списка (массива)
            List<?> list = (List<?>) value;
            packer.packArrayHeader(list.size());
            for (Object item : list) {
                packValue(packer, item); // каждый элемент может быть любого типа
            }
        } else {
            // Если тип не предусмотрен – можно упаковать как строку (toString) или выбросить исключение
            // Рекомендуем выбросить исключение, чтобы не потерять данные
            throw new IllegalArgumentException("Unsupported type: " + value.getClass().getName());
        }
    }

    public static Map<String, Object> deserializeMap(byte[] data) throws IOException {
        try (MessageUnpacker unpacker = MessagePack.newDefaultUnpacker(data)) {
            return unpackMap(unpacker);
        }
    }

    private static Map<String, Object> unpackMap(MessageUnpacker unpacker) throws IOException {
        int size = unpacker.unpackMapHeader();
        Map<String, Object> map = new HashMap<>(size);
        for (int i = 0; i < size; i++) {
            String key = unpacker.unpackString();
            Object value = unpackValue(unpacker);
            map.put(key, value);
        }
        return map;
    }

    private static Object unpackValue(MessageUnpacker unpacker) throws IOException {
        MessageFormat format = unpacker.getNextFormat();
        switch (format.getValueType()) {
            case NIL:
                unpacker.unpackNil();
                return null;
            case BOOLEAN:
                return unpacker.unpackBoolean();
            case INTEGER:
                // Возвращаем long, но при желании можно преобразовать в int, если значение помещается
                return unpacker.unpackLong();
            case FLOAT:
                // Double покрывает и float, и double
                return unpacker.unpackDouble();
            case STRING:
                return unpacker.unpackString();
            case ARRAY:
                int size = unpacker.unpackArrayHeader();
                List<Object> list = new ArrayList<>(size);
                for (int i = 0; i < size; i++) {
                    list.add(unpackValue(unpacker));
                }
                return list;
            case BINARY:
                int b_size = unpacker.unpackBinaryHeader();
                byte[] array = new byte[b_size];
                unpacker.readPayload(array);
                return array;
            case MAP:
                return unpackMap(unpacker);
            default:
                throw new IllegalArgumentException("Unsupported type: " + format.getValueType());
        }
    }
}