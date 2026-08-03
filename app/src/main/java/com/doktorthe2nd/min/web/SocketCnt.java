package com.doktorthe2nd.min.web;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Map;

import javax.net.ssl.SSLSocketFactory;

public class SocketCnt {
    private static Socket SOCKET;
    private static OutputStream SOCKET_OS;
    private static SocketListener SOCKET_LISTENER;

    public static void connect(String host, int port) throws SocketException {
        if (SOCKET.isConnected()) {
            System.out.println("SocketCnt.connect(host,port) - already connected, skip");
            return;
        }
        try {
            disconnect();
            SOCKET = SSLSocketFactory.getDefault().createSocket(host, port);
            SOCKET_OS = SOCKET.getOutputStream();
            startListener();
        } catch (IOException e) {
            throw new SocketException("Connection (connect) IOException: "+e.getMessage());
        }
    }

    public static void disconnect() throws SocketException {
        if (!isConnected()) {
            System.out.println("SocketCnt.disconnect() - already disconnected, skip");
            return;
        }
        try {
            stopListener();
            SOCKET_OS = null;
            SOCKET.close();
        } catch (IOException e) {
            throw new SocketException("Connection (disconnect) IOException: "+e.getMessage());
        }
    }

    public static void startListener() {
        stopListener();
        SOCKET_LISTENER = new SocketListener(SOCKET, SocketCnt::listenData, SocketCnt::listenError, SocketCnt::listenDone);
        SOCKET_LISTENER.start();
    }

    public static void stopListener() {
        if (SOCKET_LISTENER != null && SOCKET_LISTENER.isRunning()) SOCKET_LISTENER.stop();
    }

    public static boolean isConnected() {
        return SOCKET != null && SOCKET.isConnected();
    }

    private static int c_seq = 0;
    private static int nextSeq() {
        c_seq = (c_seq + 1) % 65536;
        return c_seq;
    }
    public static int currentSeq() {
        return c_seq;
    }

    /**
     * Send packet to server
     * @param opcode opcode from OpcodeTable
     * @param payload unprocessed payload
     * @return next seq
     * @throws SocketException if not connected
     */
    public static int send(int opcode, Map<String, String> payload) throws SocketException {
        if (!isConnected()) throw new SocketException("Not connected");
        int seq = nextSeq();
        byte[] data;
        try {
            data = PacketProcess.packPacket(opcode, payload, seq);
            SOCKET_OS.write(data);
        } catch (IOException e) {
            throw new SocketException("Send packet IOException: "+e.getMessage());
        }
        return seq;
    }

    public static void listenData(byte[] data) {
        System.out.println(PacketProcess.unpackPacket(data));
    }

    public static void listenError(Throwable throwable) {
        System.out.println("SocketListener throws: "+throwable.getMessage());
        System.out.println("Init restart");
        startListener();
    }

    public static void listenDone() {
        System.out.println("SocketListener done");
    }
}
