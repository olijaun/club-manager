package org.jaun.clubmanager.eventstore.feed.xml;

import java.net.URI;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.google.common.collect.ImmutableList;

@XmlRootElement(name = "feed", namespace = "http://www.w3.org/2005/Atom")
@XmlType(propOrder = { "title", "id", "updated", "author", "links", "entries" })
public class XmlFeed {

    private String title;
    private String id;
    private String updated;
    private XmlAuthor author;
    private List<XmlLink> links = Collections.emptyList();
    private List<XmlEntry> entries = Collections.emptyList();

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

    public XmlAuthor getAuthor() {
        return author;
    }

    public void setAuthor(XmlAuthor author) {
        this.author = author;
    }

    @XmlElement(name = "link")
    public List<XmlLink> getLinks() {
        return links;
    }

    public void setLinks(List<XmlLink> links) {
        this.links = links;
    }

    @XmlElement(name = "entry")
    public List<XmlEntry> getEntries() {
        return entries;
    }

    public void setEntries(List<XmlEntry> entries) {
        if (entries == null) {
            this.entries = Collections.emptyList();
        } else {
            this.entries = ImmutableList.copyOf(entries);
        }
    }
}
