package org.jaun.clubmanager.eventstore.feed.json;

import java.net.URI;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
@XmlType(propOrder = {"eventId", "eventType", "eventNumber", "data", "streamId", "json", "metaData", "linkMetaData", "positionEventNumber",
        "positionStreamId", "title", "id", "updated", "author", "summary", "links"})
public class JsonEntry {
    private String eventId;
    private String eventType;
    private Integer eventNumber;
    private String streamId;
    private Boolean isJson;
    private Boolean isMetaData;
    private Boolean isLinkMetaData;
    private Integer positionEventNumber;
    private String positionStreamId;
    private String data;
    private String title;
    private String id;
    private String updated;
    private JsonAuthor author;
    private String summary;
    private List<JsonLink> links = new ArrayList<>();

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

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

    public void setUpdated(Instant timestamp) {
        this.updated = timestamp.atZone(ZoneId.of("Z")).format(DateTimeFormatter.ISO_DATE_TIME);
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

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public Integer getEventNumber() {
        return eventNumber;
    }

    public void setEventNumber(Integer eventNumber) {
        this.eventNumber = eventNumber;
    }

    public String getStreamId() {
        return streamId;
    }

    public void setStreamId(String streamId) {
        this.streamId = streamId;
    }

    public Boolean getJson() {
        return isJson;
    }

    public void setJson(Boolean json) {
        isJson = json;
    }

    public Boolean getMetaData() {
        return isMetaData;
    }

    public void setMetaData(Boolean metaData) {
        isMetaData = metaData;
    }

    public Boolean getLinkMetaData() {
        return isLinkMetaData;
    }

    public void setLinkMetaData(Boolean linkMetaData) {
        isLinkMetaData = linkMetaData;
    }

    public Integer getPositionEventNumber() {
        return positionEventNumber;
    }

    public void setPositionEventNumber(Integer positionEventNumber) {
        this.positionEventNumber = positionEventNumber;
    }

    public String getPositionStreamId() {
        return positionStreamId;
    }

    public void setPositionStreamId(String positionStreamId) {
        this.positionStreamId = positionStreamId;
    }
}
