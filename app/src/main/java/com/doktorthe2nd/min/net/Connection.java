package com.doktorthe2nd.min.net;

import android.util.Pair;

import com.doktorthe2nd.min.Consts;
import com.doktorthe2nd.min.net.exceptions.QueueIsFullException;
import com.doktorthe2nd.min.net.exceptions.SocketException;

import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicBoolean;

public class Connection {
    private static final BlockingQueue<Pair<Pair<Integer, Map<String, Object>>, OnReply>> out_queue = new ArrayBlockingQueue<>(Consts.max_queue_length);
    private static final AtomicBoolean running = new AtomicBoolean(true);
    private static Thread thread;

    private static final ConcurrentMap<Integer, OnReply> onRequestMap = new ConcurrentHashMap<>();

    protected static OnReply getFromMap(int seq) {
        OnReply lambda = onRequestMap.get(seq);
        if (lambda == null) return DefinedReplies::process;
        return lambda;
    }

    public static void start() {
        thread = new Thread(() -> {
            System.out.println("Connection thread started");
            try {
                SocketCnt.connect(Consts.server.first, Consts.server.second);
                while (running.get()) {
                    Pair<Pair<Integer, Map<String, Object>>, OnReply> pair = out_queue.take();
                    int seq = SocketCnt.send(pair.first.first, pair.first.second);
                    if (pair.second != null) onRequestMap.put(seq, pair.second);
                }
            } catch (InterruptedException e) {
                throw new SocketException("InterruptedException: "+e.getMessage());
            } finally {
                SocketCnt.disconnect();
                System.out.println("Connection thread stopped");
            }
        });
        thread.setDaemon(true);
        thread.start();
    }

    public static void stop() {
        running.set(false);
        if (thread != null) {
            thread.interrupt();
        }
    }

    public boolean isRunning() {
        return running.get();
    }

    @FunctionalInterface
    public interface OnReply {
        void apply(Packet packet);
    }

    /**
     * Send packet and call lambda on reply
     * @param pair pair of opcode and payload
     * @param lambda lambda to call on reply
     */
    public static void sendRequest(Pair<Integer, Map<String, Object>> pair, OnReply lambda) {
        try {
            out_queue.add(Pair.create(pair, lambda));
        } catch (IllegalStateException e) {
            throw new QueueIsFullException("sendRequest", Consts.max_queue_length);
        }
    }
    /**
     * Send packet and call lambda on reply
     * @param opcode packet opcode
     * @param payload packet payload
     * @param lambda lambda to call on reply
     */
    public static void sendRequest(int opcode, Map<String, Object> payload, OnReply lambda) {
        sendRequest(Pair.create(opcode, payload), lambda);
    }
    /**
     * Send packet and do nothing
     * @param pair pair of opcode and payload
     */
    public static void sendNoReply(Pair<Integer, Map<String, Object>> pair) {
        sendRequest(pair, null);
    }
    /**
     * Send packet and do nothing
     * @param opcode packet opcode
     * @param payload packet payload
     */
    public static void sendNoReply(int opcode, Map<String, Object> payload) {
        sendRequest(Pair.create(opcode, payload), null);
    }
    /**
     * Send packet and process reply in DefinedReplies::process
     * @param pair pair of opcode and payload
     */
    public static void sendAutoReply(Pair<Integer, Map<String, Object>> pair) {
        sendRequest(pair, DefinedReplies::process);
    }
    /**
     * Send packet and process reply in DefinedReplies::process
     * @param opcode packet opcode
     * @param payload packet payload
     */
    public static void sendAutoReply(int opcode, Map<String, Object> payload) {
        sendRequest(Pair.create(opcode, payload), DefinedReplies::process);
    }
}
