package org.jaun.clubmanager.eventstore.feed.json;

import java.net.URI;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.common.collect.ImmutableList;

@JsonInclude(JsonInclude.Include.NON_NULL)
@XmlRootElement()
@XmlType(propOrder = {"title", "id", "updated", "streamId", "author", "headOfStream", "selfUrl", "eTag", "links", "entries"})
public class JsonFeed {
    private String title;
    private String id;
    private String updated;
    private String streamId;
    private JsonAuthor author;
    private Boolean headOfStream;
    private String selfUrl;
    private String eTag;
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

    public String getStreamId() {
        return streamId;
    }

    public void setStreamId(String streamId) {
        this.streamId = streamId;
    }

    public String getUpdated() {
        return updated;
    }

    public void setUpdated(Instant timestamp) {
        this.updated = timestamp.atZone(ZoneId.of("Z")).format(DateTimeFormatter.ISO_DATE_TIME);
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

    public String geteTag() {
        return eTag;
    }

    public void seteTag(String eTag) {
        this.eTag = eTag;
    }

    public String getSelfUrl() {
        return selfUrl;
    }

    public void setSelfUrl(URI selfUrl) {
        this.selfUrl = selfUrl.toASCIIString();
    }

    public List<JsonLink> getLinks() {
        return links;
    }

    public void addLink(URI uri, String relation) {
        links.add(new JsonLink(uri, relation));
    }

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
