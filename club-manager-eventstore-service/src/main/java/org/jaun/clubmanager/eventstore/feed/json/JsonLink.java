package org.jaun.clubmanager.eventstore.feed.json;

import java.net.URI;

import javax.xml.bind.annotation.XmlAttribute;

public class JsonLink {
    private String uri;
    private String relation;

    public JsonLink(URI uri, String relation) {
        this.uri = uri.toString();
        this.relation = relation;
    }

    @XmlAttribute
    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    @XmlAttribute
    public String getRelation() {
        return relation;
    }

    public void setRelation(String relation) {
        this.relation = relation;
    }
}
