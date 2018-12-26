package org.jaun.clubmanager.domain.model.commons;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNot.not;

class EntityTest {

    class IdA extends Id {
        IdA(String value) {
            super(value);
        }
    }

    class EntityA extends Entity<IdA> {

        private IdA id;
        private String state;

        EntityA(IdA id) {
            this.id = id;
        }

        public IdA getId() {
            return id;
        }

        public void setState(String state) {
            this.state = state;
        }
    }

    @Test
    void testEquals_sameObject() {

        EntityA a1 = new EntityA(new IdA("a1"));

        assertThat(a1, equalTo(a1));
    }

    @Test
    void testEquals_otherObjectSameId() {

        EntityA a1 = new EntityA(new IdA("a1"));
        EntityA a11 = new EntityA(new IdA("a1"));

        assertThat(a1, equalTo(a11));
    }

    @Test
    void testEquals_otherObjectSameId_differentStateSameHashCode() {
        EntityA a1 = new EntityA(new IdA("a1"));
        EntityA a11 = new EntityA(new IdA("a1"));

        a1.setState("blabli");

        assertThat(a1, equalTo(a11));
    }

    @Test
    void testEquals_otherObjectDifferentId() {

        EntityA a1 = new EntityA(new IdA("a1"));
        EntityA a2 = new EntityA(new IdA("s2"));

        assertThat(a1, not(equalTo(a2)));
    }

    @Test
    void testHashCode_otherObjectSameId() {
        EntityA a1 = new EntityA(new IdA("a1"));
        EntityA a11 = new EntityA(new IdA("a1"));

        assertThat(a1.hashCode(), equalTo(a11.hashCode()));
    }

    @Test
    void testHashCode_otherObjectSameId_differentStateSameHashCode() {
        EntityA a1 = new EntityA(new IdA("a1"));
        EntityA a11 = new EntityA(new IdA("a1"));

        a1.setState("blabli");

        assertThat(a1.hashCode(), equalTo(a11.hashCode()));
    }

    @Test
    void testHashCode_otherObjectDifferentId() {

        EntityA a1 = new EntityA(new IdA("a1"));
        EntityA a2 = new EntityA(new IdA("s2"));

        assertThat(a1.hashCode(), not(equalTo(a2.hashCode())));
    }
}