package com.doktorthe2nd.min.web.exceptions;

public class SessionExpiredException extends PacketException {
    public SessionExpiredException(String message) {
        super(message);
    }
}
