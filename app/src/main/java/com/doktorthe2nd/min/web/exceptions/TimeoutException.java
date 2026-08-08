package com.doktorthe2nd.min.web.exceptions;

public class TimeoutException extends RuntimeException {
    public TimeoutException(String message) {
        super(message);
    }
}
