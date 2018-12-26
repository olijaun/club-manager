package org.jaun.clubmanager.domain.model.commons;

import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class ValueObjectTest {

    class ValueObjectA extends ValueObject {
        private final int a;
        private final int b;

        public ValueObjectA(int a, int b) {
            this.a = a;
            this.b = b;
        }

        public int getA() {
            return a;
        }

        public int getB() {
            return b;
        }
    }

    class ValueObjectB extends ValueObject {
        private final int a;
        private final int b;

        public ValueObjectB(int a, int b) {
            this.a = a;
            this.b = b;
        }

        public int getA() {
            return a;
        }

        public int getB() {
            return b;
        }
    }

    @Test
    void testEquals_sameObject() {
        ValueObjectA a1 = new ValueObjectA(1, 2);

        assertThat(a1, equalTo(a1));
    }

    @Test
    void testEquals_differentObject_sameState() {
        ValueObjectA a1 = new ValueObjectA(1, 2);
        ValueObjectA a2 = new ValueObjectA(1, 2);

        assertThat(a1, equalTo(a2));
        assertThat(a1.hashCode(), equalTo(a2.hashCode()));
    }

    @Test
    void testEquals_differentObject_differentState() {
        ValueObjectA a1 = new ValueObjectA(1, 2);
        ValueObjectA a2 = new ValueObjectA(2, 3);

        assertThat(a1, not(equalTo(a2)));
        assertThat(a1.hashCode(), not(equalTo(a2.hashCode())));
    }

    @Test
    void testEquals_differentClass_sameState() {
        ValueObjectA a1 = new ValueObjectA(1, 2);
        ValueObjectB b1 = new ValueObjectB(1, 2);

        assertThat(a1, not(equalTo(b1)));
    }
}