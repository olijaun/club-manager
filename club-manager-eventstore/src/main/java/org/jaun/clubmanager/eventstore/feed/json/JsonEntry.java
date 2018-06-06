package org.jaun.clubmanager.eventstore.feed.json;

import java.net.URI;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder = {"title", "id", "updated", "author", "summary", "links"})
public class JsonEntry {
    private String title;
    private String id;
    private String updated;
    private JsonAuthor author;
    private String summary;
    private List<JsonLink> links = new ArrayList<>();

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getId() {
        return id;
    }

    public void setId(URI id) {
        this.id = id.toASCIIString();
    }

    public String getUpdated() {
        return updated;
    }

    public void setUpdated(ZonedDateTime date) {
        this.updated = date.format(DateTimeFormatter.ISO_DATE_TIME);
    }

    public JsonAuthor getAuthor() {
        return author;
    }

    public void setAuthor(JsonAuthor author) {
        this.author = author;
    }

    public void setAuthor(String author) {
        this.author = new JsonAuthor(author);
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    @XmlElement(name = "link")
    public List<JsonLink> getLinks() {
        return links;
    }

    public void addLink(URI uri, String relation) {
        links.add(new JsonLink(uri, relation));
    }
}
