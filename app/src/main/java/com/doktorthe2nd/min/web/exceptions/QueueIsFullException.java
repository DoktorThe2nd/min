package com.doktorthe2nd.min.web.exceptions;

public class QueueIsFullException extends RuntimeException {
    public QueueIsFullException(String queue, int max_size) {
        super("Queue "+queue+" is full (with max size of "+max_size+")");
    }
}
