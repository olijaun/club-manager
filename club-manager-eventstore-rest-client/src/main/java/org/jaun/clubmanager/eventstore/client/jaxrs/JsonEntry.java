package org.jaun.clubmanager.eventstore.client.jaxrs;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlType;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonIgnoreProperties(ignoreUnknown = true)
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

    public void setId(String id) {
        this.id = id;
    }

    public String getUpdated() {
        return updated;
    }

    public void setUpdated(String updated) {
        this.updated = updated;
    }

    public JsonAuthor getAuthor() {
        return author;
    }

    public void setAuthor(JsonAuthor author) {
        this.author = author;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public List<JsonLink> getLinks() {
        return links;
    }

    public void setLinks(List<JsonLink> links) {
        this.links = links;
    }
}
