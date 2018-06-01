package org.jaun.clubmanager.evenstore;

import static java.util.Objects.requireNonNull;

import org.jaun.clubmanager.domain.model.commons.ValueObject;

import com.google.common.base.Strings;

public class Category extends ValueObject {

    private final String name;

    public Category(String name) {
        this.name = requireNonNull(Strings.emptyToNull(name));
    }

    public String getName() {
        return name;
    }
}
