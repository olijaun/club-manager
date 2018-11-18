package org.jaun.clubmanager.person.application.resource;

import java.time.LocalDate;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public class BasicDataDTO extends DTO {
    @NotNull
    @Valid
    private NameDTO name;
    private LocalDate birthDate;
    private String sex;

    public NameDTO getName() {
        return name;
    }

    public BasicDataDTO setName(NameDTO name) {
        this.name = name;
        return this;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public BasicDataDTO setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
        return this;
    }

    public String getSex() {
        return sex;
    }

    public BasicDataDTO setSex(String sex) {
        this.sex = sex;
        return this;
    }
}
