package org.jaun.clubmanager.eventstore.feed.xml;

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
    private Date updated;
    private Author author;
    private List<Link> links = Collections.emptyList();
    private List<Entry> entries = Collections.emptyList();

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

    public Author getAuthor() {
        return author;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }

    @XmlElement(name = "link")
    public List<Link> getLinks() {
        return links;
    }

    public void setLinks(List<Link> links) {
        this.links = links;
    }

    @XmlElement(name = "entry")
    public List<Entry> getEntries() {
        return entries;
    }

    public void setEntries(List<Entry> entries) {
        if (entries == null) {
            this.entries = Collections.emptyList();
        } else {
            this.entries = ImmutableList.copyOf(entries);
        }
    }
}
