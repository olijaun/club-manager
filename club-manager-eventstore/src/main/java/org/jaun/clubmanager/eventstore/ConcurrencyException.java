package org.jaun.clubmanager.eventstore;

public class ConcurrencyException extends Exception {
    public ConcurrencyException(String msg) {
        super(msg);
    }
}
