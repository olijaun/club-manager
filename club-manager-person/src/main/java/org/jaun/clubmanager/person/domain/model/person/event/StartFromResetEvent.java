package org.jaun.clubmanager.person.domain.model.person.event;

public class StartFromResetEvent extends PersonIdRegistryEvent {
    private final int newStartFrom;

    public StartFromResetEvent(int newStartFrom) {
        this.newStartFrom = newStartFrom;
    }

    public int getNewStartFrom() {
        return newStartFrom;
    }
}
