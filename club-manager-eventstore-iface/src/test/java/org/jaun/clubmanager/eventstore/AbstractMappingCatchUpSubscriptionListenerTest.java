package org.jaun.clubmanager.eventstore;

import com.google.gson.Gson;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class AbstractMappingCatchUpSubscriptionListenerTest {


    static class TestListener extends AbstractMappingCatchUpSubscriptionListener {

        @Override
        public Collection<Category> categories() {
            return Arrays.asList(new Category("abc"));
        }
    }

    @Test
    void registerMappingAndOnEvent() {
        // prepare
        AtomicInteger eventCounter = new AtomicInteger(0);
        TestListener listener = new TestListener();
        StoredEventData storedEventData = StoredEventDataFixture.storedEventData().build();

        // run
        listener.registerMapping(new EventType("typeA"), (version, data) -> eventCounter.incrementAndGet());
        listener.onEvent(storedEventData);

        // verify
        assertThat(eventCounter.get(), equalTo(1));
    }

    @Test
    void onClose() {
        // prepare
        TestListener listener = new TestListener();

        // run
        listener.onClose(null, Optional.of(new IllegalArgumentException("test")));

        // verify
        // just prints stack trace
    }

    public class TestDto {
        private String test = "abc";

        public String getTest() {
            return test;
        }

        public void setTest(String test) {
            this.test = test;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            TestDto testDto = (TestDto) o;
            return Objects.equals(test, testDto.test);
        }

        @Override
        public int hashCode() {
            return Objects.hash(test);
        }
    }

    @Test
    void toObject() {
        // prepare
        Gson gson = new Gson();
        TestDto dtoIn = new TestDto();
        String payload = gson.toJson(new TestDto());
        TestListener listener = new TestListener();
        StoredEventData storedEventData = StoredEventDataFixture.storedEventData().payload(payload).build();

        // run
        TestDto dtoOut = listener.toObject(storedEventData, TestDto.class);

        // verify
        assertThat(dtoOut, equalTo(dtoIn));
    }
}