package org.jaun.clubmanager.eventstore;

import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.jaun.clubmanager.domain.model.commons.Id;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.mockito.internal.matchers.Null;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class StreamIdTest {

    class TestId extends Id {

        protected TestId(String value) {
            super(value);
        }
    }

    @Test
    void construct_ok() {
        TestId testId = new TestId("123");
        Category category = new Category("testCat");
        StreamId streamId = new StreamId(testId, category);

        assertThat(streamId.getValue(), equalTo(category.getName() + "-" + testId.getValue()));
        assertThat(streamId.getCategory().get(), equalTo(category));
        assertThat(streamId.getId(), equalTo(testId));
    }

    @Test
    void construct_ok_noCategory() {
        TestId testId = new TestId("123");
        StreamId streamId = new StreamId(testId, null);

        assertThat(streamId.getValue(), equalTo(testId.getValue()));
        assertThat(streamId.getId(), equalTo(testId.getValue()));
    }

    @Test
    void construct_noId() {
        Category category = new Category("testCat");
        Executable e = () -> new StreamId(null, category);

        assertThrows(NullPointerException.class, e);
    }

    @Test
    void parse() {
        StreamId streamId = StreamId.parse("testCat-123");

        assertThat(streamId.getValue(), equalTo("testCat-123"));
        assertThat(streamId.getCategory().get(), equalTo(new Category("testCat")));
        assertThat(streamId.getId(), equalTo("123"));
    }

    @Test
    void parse_noId() {
        Executable e = () -> StreamId.parse("-123");

        assertThrows(NullPointerException.class, e);
    }
}