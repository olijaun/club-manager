package org.jaun.clubmanager.eventstore;

public class StreamRevision implements Comparable<StreamRevision> {

    /** states that this write should never conflict and should always succeed */
    public static final StreamRevision UNSPECIFIED = new StreamRevision(-2);
    public static final StreamRevision NEW_STREAM = new StreamRevision(-1);
    public static final StreamRevision INITIAL = new StreamRevision(0);
    public static final StreamRevision MAXIMUM = new StreamRevision(Long.MAX_VALUE);
    private final Long value;

    private StreamRevision(long value) {
        if (value < -2) {
            throw new IllegalStateException("stream revision cannot be less than -2: " + value);
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

    public StreamRevision add(int i) {
        return new StreamRevision(value + i);
    }

    public StreamRevision sub(int i) {
        return new StreamRevision(value - i);
    }

    public static StreamRevision from(Integer revision) {
        if (revision == null) {
            return UNSPECIFIED;
        }
        return from(revision.longValue());
    }

    public static StreamRevision from(Long revision) {
        if (revision == null) {
            return UNSPECIFIED;
        }
        if (revision == NEW_STREAM.getValue()) {
            return NEW_STREAM;
        }
        if (revision == INITIAL.getValue()) {
            return INITIAL;
        }
        return new StreamRevision(revision);
    }

    @Override
    public int compareTo(StreamRevision that) {
        return this.value.compareTo(that.value);
    }

    public boolean equals(Object other) {

        if (this == other) {
            return true;
        }

        if (other == null || !(other instanceof StreamRevision)) {
            return false;
        }

        return this.compareTo((StreamRevision) other) == 0;
    }
}
