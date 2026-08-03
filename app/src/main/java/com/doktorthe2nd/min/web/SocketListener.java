package com.doktorthe2nd.min.web;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;

public class SocketListener {
    @FunctionalInterface
    public interface OnData {
        void accept(byte[] data);
    }

    @FunctionalInterface
    public interface OnError {
        void accept(Throwable error);
    }

    @FunctionalInterface
    public interface OnDone {
        void run();
    }

    private final Socket socket;
    private final OnData onData;
    private final OnError onError;
    private final OnDone onDone;
    private final AtomicBoolean running = new AtomicBoolean(true);
    private Thread readerThread;

    public SocketListener(Socket socket, OnData onData, OnError onError, OnDone onDone) {
        this.socket = socket;
        this.onData = onData;
        this.onError = onError;
        this.onDone = onDone;
    }

    public void start() {
        readerThread = new Thread(() -> {
            try (InputStream in = socket.getInputStream()) {
                byte[] buffer = new byte[LZ4.MAX_COMPRESSED_SIZE];
                int bytesRead;
                while (running.get() && (bytesRead = in.read(buffer)) != -1) {
                    // Извлекаем только реально прочитанные байты
                    byte[] data = new byte[bytesRead];
                    System.arraycopy(buffer, 0, data, 0, bytesRead);
                    onData.accept(data);
                }
            } catch (IOException e) {
                if (running.get()) {
                    onError.accept(e);
                }
            } finally {
                running.set(false);
                onDone.run();
            }
        });
        readerThread.setDaemon(true);
        readerThread.start();
    }

    public void stop() {
        running.set(false);
        if (readerThread != null) {
            readerThread.stop(); // на случай, если поток заблокирован на чтении
        }
    }

    public boolean isRunning() {
        return running.get();
    }
}
