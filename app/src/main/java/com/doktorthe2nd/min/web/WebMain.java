package com.doktorthe2nd.min.web;

import java.io.IOException;
import java.net.Socket;

import javax.net.ssl.SSLSocketFactory;

public class WebMain {
    private static Socket SOCKET;

    public static boolean connect() {
        try {
            if (SOCKET != null && SOCKET.isConnected()) SOCKET.close();
            SOCKET = SSLSocketFactory.getDefault().createSocket("", 0);
        } catch (IOException e) {
            System.out.println("Socket IO exception: " + e.getMessage());
            return false;
        }
        return true;
    }

    public static void init() {}
}
