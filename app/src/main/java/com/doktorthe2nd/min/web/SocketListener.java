package com.doktorthe2nd.min.web;

import com.doktorthe2nd.min.Consts;
import com.doktorthe2nd.min.web.exceptions.QueueIsFullException;
import com.doktorthe2nd.min.web.exceptions.SocketException;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class SocketListener {
    private final Socket socket;
    private final AtomicBoolean running = new AtomicBoolean(true);
    private Thread readerThread;

    public SocketListener(Socket socket) {
        this.socket = socket;
    }

    public void start() {
        readerThread = new Thread(() -> {
            try (InputStream in = socket.getInputStream()) {
                System.out.println("SocketListener started");
                byte[] buffer = new byte[Consts.max_compressed_size];
                int bytesRead;
                while (running.get() && (bytesRead = in.read(buffer)) != -1) {
                    byte[] data = new byte[bytesRead];
                    System.arraycopy(buffer, 0, data, 0, bytesRead);
                    Packet packet = PacketProcess.unpackPacket(data);
                    System.out.println(packet);
                    Connection.getFromMap(packet.seq).apply(packet);
                }
            } catch (IOException e) {
                if (running.get()) {
                    throw new RuntimeException("IOException in SocketListener: "+e.getMessage());
                }
            } finally {
                running.set(false);
                System.out.println("SocketListener stopped");
            }
        });
        readerThread.setDaemon(true);
        readerThread.start();
    }

    public void stop() {
        running.set(false);
        if (readerThread != null) {
            readerThread.interrupt();
        }
    }

    public boolean isRunning() {
        return running.get();
    }
}
