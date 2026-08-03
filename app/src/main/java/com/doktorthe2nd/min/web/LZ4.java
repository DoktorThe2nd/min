package com.doktorthe2nd.min.web;

import net.jpountz.lz4.LZ4BlockInputStream;
import net.jpountz.lz4.LZ4Compressor;
import net.jpountz.lz4.LZ4Factory;
import net.jpountz.lz4.LZ4FastDecompressor;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class LZ4 {
    public static final int MAX_COMPRESSED_SIZE = 32*1024*1024; // 32 MB

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
        return decompress(compressed, MAX_COMPRESSED_SIZE);
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

    public static byte[] decompressBlock(byte[] compressed) throws IOException {
        return decompressBlock(compressed, MAX_COMPRESSED_SIZE);
    }
    public static byte[] decompressBlock(byte[] compressed, int max_size) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (LZ4BlockInputStream in = LZ4BlockInputStream.newBuilder().build(new ByteArrayInputStream(compressed))) {
            byte[] buffer = new byte[max_size];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                baos.write(buffer, 0, bytesRead);
            }
        }
        return baos.toByteArray();
    }
}
