package org.jaun.clubmanager.eventstore.feed.xml;

import java.net.URI;

import javax.xml.bind.annotation.XmlAttribute;

public class XmlLink {
    private String href;
    private String rel;

    public XmlLink(URI href, String rel) {
        this.href = href.toString();
        this.rel = rel;
    }

    @XmlAttribute
    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    @XmlAttribute
    public String getRel() {
        return rel;
    }

    public void setRel(String rel) {
        this.rel = rel;
    }
}
