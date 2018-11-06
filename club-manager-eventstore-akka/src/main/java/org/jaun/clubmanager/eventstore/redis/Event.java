package org.jaun.clubmanager.eventstore.redis;

import java.io.Serializable;

public class Event implements Serializable {

    private final String id;
    private final String data;
    private final String metaData;

    public Event(String id, String data, String metaData) {
        this.id = id;
        this.data = data;
        this.metaData = metaData;
    }

    public String getId() {
        return id;
    }

    public String getData() {
        return data;
    }

    public String getMetaData() {
        return metaData;
    }
}
