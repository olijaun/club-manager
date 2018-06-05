package org.jaun.clubmanager.eventstore.feed.json;

import java.net.URI;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.google.common.collect.ImmutableList;

@XmlRootElement()
@XmlType(propOrder = { "title", "id", "updated", "author", "links", "entries" })
public class JsonFeed {

    private String title;
    private String id;
    private String updated;
    private JsonAuthor author;
    private Boolean headOfStream;
    private List<JsonLink> links = new ArrayList();
    private List<JsonEntry> entries = new ArrayList<>();

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

    public void setUpdated(ZonedDateTime time) {
        this.updated = time.format(DateTimeFormatter.ISO_DATE_TIME);
    }

    public JsonAuthor getAuthor() {
        return author;
    }

    public void setAuthor(JsonAuthor author) {
        this.author = author;
    }

    public void setUpdated(String updated) {
        this.updated = updated;
    }

    public Boolean getHeadOfStream() {
        return headOfStream;
    }

    public void setHeadOfStream(Boolean headOfStream) {
        this.headOfStream = headOfStream;
    }

    @XmlElement(name = "link")
    public List<JsonLink> getLinks() {
        return links;
    }

    @XmlElement(name = "entry")
    public List<JsonEntry> getEntries() {
        return entries;
    }

    public void setEntries(List<JsonEntry> entries) {
        if (entries == null) {
            this.entries = Collections.emptyList();
        } else {
            this.entries = ImmutableList.copyOf(entries);
        }
    }
}
