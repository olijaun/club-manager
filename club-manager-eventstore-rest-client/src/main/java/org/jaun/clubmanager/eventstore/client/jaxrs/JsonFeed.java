package org.jaun.clubmanager.eventstore.client.jaxrs;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonIgnoreProperties(ignoreUnknown = true)
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

    public void setId(String id) {
        this.id = id;
    }

    public String getUpdated() {
        return updated;
    }

    public void setUpdated(String updated) {
        this.updated = updated;
    }

    public String getStreamId() {
        return streamId;
    }

    public void setStreamId(String streamId) {
        this.streamId = streamId;
    }

    public JsonAuthor getAuthor() {
        return author;
    }

    public void setAuthor(JsonAuthor author) {
        this.author = author;
    }

    public Boolean getHeadOfStream() {
        return headOfStream;
    }

    public void setHeadOfStream(Boolean headOfStream) {
        this.headOfStream = headOfStream;
    }

    public String getSelfUrl() {
        return selfUrl;
    }

    public void setSelfUrl(String selfUrl) {
        this.selfUrl = selfUrl;
    }

    public String geteTag() {
        return eTag;
    }

    public void seteTag(String eTag) {
        this.eTag = eTag;
    }

    public List<JsonLink> getLinks() {
        return links;
    }

    public void setLinks(List<JsonLink> links) {
        this.links = links;
    }

    public List<JsonEntry> getEntries() {
        return entries;
    }

    public void setEntries(List<JsonEntry> entries) {
        this.entries = entries;
    }
}
