package org.jaun.clubmanager.member.domain.model.contact;

import static java.util.Objects.requireNonNull;

import java.util.Objects;
import java.util.Optional;

import org.jaun.clubmanager.domain.model.commons.ValueObject;

public class CompanyName extends Name {
    private final String name;

    public CompanyName(String name) {
        this.name = requireNonNull(name);
    }

    public String getName() {
        return name;
    }

    @Override
    public String getNameLine() {
        return name;
    }
}
