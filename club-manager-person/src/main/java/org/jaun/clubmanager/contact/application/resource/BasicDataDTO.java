package org.jaun.clubmanager.contact.application.resource;

import java.time.LocalDate;

public class BasicDataDTO extends DTO {
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
