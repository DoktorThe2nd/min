package com.doktorthe2nd.min.web;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

public class FingerprintGenerator {

    private final Map<String, ApkBuildFingerprint> data;

    public FingerprintGenerator(Map<String, ApkBuildFingerprint> data) {
        this.data = data;
    }

    /**
     * Генерирует 96-байтовый fingerprint.
     *
     * @param version   версия (ключ в data)
     * @param deviceId  идентификатор устройства
     * @param callsSeed long-значение seed
     * @param arch      архитектура (по умолчанию "arm64-v8a")
     * @return массив байт или null, если версия/архитектура не найдены
     */
    public byte[] generateFingerprint(String version, String deviceId, long callsSeed, String arch) {
        ApkBuildFingerprint model = data.get(version);
        if (model == null) {
            return null;
        }

        // Упаковываем callsSeed в big-endian long (аналог struct.pack(">q", ...))
        byte[] seedBytes = ByteBuffer.allocate(Long.BYTES)
                .order(ByteOrder.BIG_ENDIAN)
                .putLong(callsSeed)
                .array();

        byte[] deviceBytes = deviceId.getBytes(StandardCharsets.UTF_8);

        try {
            MessageDigest sha256 = MessageDigest.getInstance("SHA-256");

            // h1 = SHA-256(cert_meta_sha256_bytes + seed + device)
            sha256.update(hexStringToByteArray(model.getCertificateMetaSha256()));
            sha256.update(seedBytes);
            sha256.update(deviceBytes);
            byte[] h1 = sha256.digest();

            // h2 = SHA-256(dex_meta_sha256_bytes + seed + device)
            sha256.update(hexStringToByteArray(model.getDexMetaSha256()));
            sha256.update(seedBytes);
            sha256.update(deviceBytes);
            byte[] h2 = sha256.digest();

            // h3 = SHA-256(so_meta_sha256[arch] + seed + device)
            String soHex = model.getSoMetaSha256().get(arch);
            if (soHex == null) {
                return null;   // можно также бросить исключение
            }
            sha256.update(hexStringToByteArray(soHex));
            sha256.update(seedBytes);
            sha256.update(deviceBytes);
            byte[] h3 = sha256.digest();

            // Склеиваем h1 + h2 + h3
            byte[] result = new byte[h1.length + h2.length + h3.length];
            System.arraycopy(h1, 0, result, 0, h1.length);
            System.arraycopy(h2, 0, result, h1.length, h2.length);
            System.arraycopy(h3, 0, result, h1.length + h2.length, h3.length);
            return result;

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 is not available", e);
        }
    }

    /** Версия с архитектурой по умолчанию */
    public byte[] generateFingerprint(String version, String deviceId, long callsSeed) {
        return generateFingerprint(version, deviceId, callsSeed, "arm64-v8a");
    }

    /**
     * Преобразует шестнадцатеричную строку в массив байт.
     * Работает на всех версиях Android, включая API < 34.
     */
    private static byte[] hexStringToByteArray(String hex) {
        int len = hex.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4)
                    + Character.digit(hex.charAt(i + 1), 16));
        }
        return data;
    }
}