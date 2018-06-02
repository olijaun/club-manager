package org.jaun.clubmanager.eventstore;

public class StreamRevision implements Comparable<StreamRevision> {

    public static final StreamRevision INITIAL = new StreamRevision(0);
    public static final StreamRevision MAXIMUM = new StreamRevision(Long.MAX_VALUE);
    private final Long value;

    public StreamRevision(long value) {
        if (value < 0) {
            throw new IllegalStateException("stream revision cannot be negative: " + value);
        }

        this.value = value;
    }

    public Long getValue() {
        return value;
    }

    public StreamRevision next() {
        return new StreamRevision(value + 1);
    }

    public StreamRevision previous() {
        return new StreamRevision(value - 1);
    }

    @Override
    public int compareTo(StreamRevision that) {
        return this.value.compareTo(that.value);
    }
}
