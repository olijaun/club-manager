package org.jaun.clubmanager.eventstore.feed.xml;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.json.JsonValue;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder = { "title", "id", "updated", "author", "summary", "links" })
public class XmlEntry {
    private String title;
    private String id;
    private Date updated;
    private XmlAuthor author;
    private String summary;
    private List<XmlLink> links = Collections.emptyList();

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

    public Date getUpdated() {
        return updated;
    }

    public void setUpdated(Date date) {
        this.updated = updated;
    }

    public XmlAuthor getAuthor() {
        return author;
    }

    public void setAuthor(XmlAuthor author) {
        this.author = author;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    @XmlElement(name = "link")
    public List<XmlLink> getLinks() {
        return links;
    }

    public void setLinks(List<XmlLink> links) {
        this.links = links;
    }
}
