package org.jaun.clubmanager.eventstore.feed.json;

public class JsonAuthor {
    private String name;

    public JsonAuthor(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
