package org.jaun.clubmanager.person.domain.model.person.event;

import org.jaun.clubmanager.person.domain.model.person.PersonIdRegistryId;

public class StartFromResetEvent extends PersonIdRegistryEvent {
    private final int newStartFrom;

    public StartFromResetEvent(int newStartFrom) {
        this.newStartFrom = newStartFrom;
    }

    public int getNewStartFrom() {
        return newStartFrom;
    }
}
