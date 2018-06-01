package org.jaun.clubmanager.evenstore;

public class ConcurrencyException extends Exception {
    public ConcurrencyException(String msg) {
        super(msg);
    }
}
