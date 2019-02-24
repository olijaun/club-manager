package org.jaun.clubmanager.eventstore;

import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class StreamRevisionTest {

    @Test
    void from_ok() {
        StreamRevision revision = StreamRevision.from(42L);

        assertThat(revision.getValue(), equalTo(42L));
    }

    @Test
    void from_ok_null() {
        StreamRevision revision = StreamRevision.from((Integer)null);

        assertThat(revision, equalTo(StreamRevision.UNSPECIFIED));
    }

    @Test
    void from_ok_newStream() {
        StreamRevision revision = StreamRevision.from(-1L);

        assertThat(revision, equalTo(StreamRevision.NEW_STREAM));
    }

    @Test
    void from_ok_initial() {
        StreamRevision revision = StreamRevision.from(0L);

        assertThat(revision, equalTo(StreamRevision.INITIAL));
    }

    @Test
    void from_ok_max() {
        StreamRevision revision = StreamRevision.from(Long.MAX_VALUE);

        assertThat(revision, equalTo(StreamRevision.MAXIMUM));
    }

    @Test
    void from_nok_tooSmall() {
        Executable e = () -> StreamRevision.from(-3L);

        assertThrows(IllegalArgumentException.class, e);
    }

    @Test
    void next() {
        StreamRevision revision = StreamRevision.from(42L);

        StreamRevision next = revision.next();

        assertThat(next.getValue(), equalTo(43L));
    }

    @Test
    void previous() {
        StreamRevision revision = StreamRevision.from(42L);

        StreamRevision previous = revision.previous();

        assertThat(previous.getValue(), equalTo(41L));
    }


}