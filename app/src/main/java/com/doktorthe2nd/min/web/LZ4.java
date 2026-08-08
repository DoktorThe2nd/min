package com.doktorthe2nd.min.web;

import com.doktorthe2nd.min.Consts;

import net.jpountz.lz4.LZ4BlockInputStream;
import net.jpountz.lz4.LZ4Compressor;
import net.jpountz.lz4.LZ4Factory;
import net.jpountz.lz4.LZ4FastDecompressor;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LZ4 {
    public static final LZ4Factory factory = LZ4Factory.fastestInstance();
    public static final LZ4Compressor compressor = factory.fastCompressor();
    public static final LZ4FastDecompressor decompressor = factory.fastDecompressor();

    public static byte[] compress(byte[] raw) {
        int maxCompressedLength = compressor.maxCompressedLength(raw.length);
        byte[] compressedData = new byte[maxCompressedLength];
        compressor.compress(
                raw, 0, raw.length,
                compressedData, 0, maxCompressedLength
        );
        return compressedData;
    }

    public static byte[] decompress(byte[] compressed) {
        return decompress(compressed, Consts.max_compressed_size);
    }
    public static byte[] decompress(byte[] compressed, int max_size) {
        byte[] decompressedData = new byte[max_size];
        int bytesRead = decompressor.decompress(
                compressed, 0,
                decompressedData, 0,
                max_size
        );
        byte[] smallerData = new byte[bytesRead];
        System.arraycopy(decompressedData, 0, smallerData, 0, bytesRead);
        return smallerData;
    }

    public static byte[] decompressBlock(byte[] src) throws IOException {
        return decompressBlock(src, Consts.max_compressed_size);
    }
    public static byte[] decompressBlock(byte[] src, int maxOutput) throws IOException {
        List<Byte> dst = new ArrayList<>();
        int pos = 0;
        int srcLen = src.length;

        while (pos < srcLen) {
            int token = src[pos] & 0xFF;
            pos++;

            // Длина литерала
            int litLen = token >>> 4;
            if (litLen == 15) {
                while (pos < srcLen) {
                    int b = src[pos] & 0xFF;
                    pos++;
                    litLen += b;
                    if (b != 255) break;
                }
            }

            // Копируем литералы
            if (litLen > 0) {
                if (pos + litLen > srcLen) {
                    throw new IllegalArgumentException("LZ4: literal length out of bounds");
                }
                // Проверка на превышение maxOutput
                if (dst.size() + litLen > maxOutput) {
                    throw new IllegalArgumentException("LZ4: output too large");
                }
                for (int i = 0; i < litLen; i++) {
                    dst.add(src[pos + i]);
                }
                pos += litLen;
            }

            if (pos >= srcLen) {
                break;
            }

            // Смещение
            if (pos + 1 >= srcLen) {
                throw new IllegalArgumentException("LZ4: incomplete offset");
            }
            int offset = (src[pos] & 0xFF) | ((src[pos + 1] & 0xFF) << 8);
            pos += 2;

            if (offset == 0) {
                throw new IllegalArgumentException("LZ4: zero offset");
            }

            // Длина совпадения
            int matchLen = (token & 0x0F) + 4;
            if ((token & 0x0F) == 0x0F) {
                while (pos < srcLen) {
                    int b = src[pos] & 0xFF;
                    pos++;
                    matchLen += b;
                    if (b != 255) break;
                }
            }

            int matchPos = dst.size() - offset;

            if (matchPos < 0) {
                throw new IllegalArgumentException("LZ4: match out of bounds");
            }

            // Копируем совпадение
            if (dst.size() + matchLen > maxOutput) {
                throw new IllegalArgumentException("LZ4: output too large");
            }
            for (int i = 0; i < matchLen; i++) {
                dst.add(dst.get(matchPos + (i % offset)));
            }
        }

        // Преобразуем List<Byte> в byte[]
        byte[] result = new byte[dst.size()];
        for (int i = 0; i < result.length; i++) {
            result[i] = dst.get(i);
        }
        return result;
    }
}
