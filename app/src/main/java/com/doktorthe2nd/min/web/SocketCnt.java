package com.doktorthe2nd.min.web;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Map;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

public class SocketCnt {
    private static SSLSocket SOCKET;
    private static SocketListener SOCKET_LISTENER;

    public static void connect(String host, int port) throws SocketException {
        System.out.println("attempting SocketCnt.connect(host,port)");
        if (isConnected()) {
            System.out.println("SocketCnt.connect(host,port) - already connected, skip");
            return;
        }
        try {
            if (isConnected()) disconnect();
            SOCKET = (SSLSocket)SSLSocketFactory.getDefault().createSocket(host, port);
            SOCKET.startHandshake();
            startListener();
        } catch (IOException e) {
            throw new SocketException("Connection (connect) IOException: "+e.getMessage());
        }
    }

    public static void disconnect() throws SocketException {
        System.out.println("SocketCnt.disconnect()");
        if (!isConnected()) {
            System.out.println("SocketCnt.disconnect() - already disconnected, skip");
            return;
        }
        try {
            stopListener();
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
     * @return seq of this packet
     * @throws SocketException if not connected
     */
    public static int send(int opcode, Map<String, Object> payload) throws SocketException {
        System.out.println("Sending packet opcode="+opcode+" payload="+payload.toString());
        if (!isConnected()) throw new SocketException("Not connected");
        int seq = nextSeq();
        byte[] data;
        try {
            data = PacketProcess.packPacket(opcode, payload, seq);
            SOCKET.getOutputStream().write(data);
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
