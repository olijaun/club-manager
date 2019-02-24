package org.jaun.clubmanager.eventstore;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CategoryTest {

    @Test
    void nullName() {

        Executable e = () -> new Category(null);

        assertThrows(NullPointerException.class, e);
    }

    @Test
    void getName() {
        String catName = "testCat";

        Category category = new Category(catName);

        assertThat(category.getName(), equalTo(catName));
    }
}