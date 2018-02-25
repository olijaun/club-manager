package org.jaun.clubmanager.domain.model.commons;

public class ConcurrencyException extends Exception {

    public ConcurrencyException(Throwable cause, String message) {
        super(message, cause);
    }
}
