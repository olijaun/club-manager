package org.jaun.clubmanager.eventstore;

public class ConcurrencyException extends Exception {

    private final long expectedVersion;
    private final long actualVersion;

    public ConcurrencyException(long expectedVersion, long actualVersion) {
        super("expected version " + expectedVersion + " does not match current stream version " + actualVersion);
        this.expectedVersion = expectedVersion;
        this.actualVersion = actualVersion;
    }

    public long getExpectedVersion() {
        return expectedVersion;
    }

    public long getActualVersion() {
        return actualVersion;
    }
}
