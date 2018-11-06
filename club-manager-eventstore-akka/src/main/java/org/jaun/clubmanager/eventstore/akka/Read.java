package org.jaun.clubmanager.eventstore.akka;

import java.io.Serializable;

public class Read implements Serializable {
    private final int from;
    private final int to;

    public Read(int from, int to) {
        this.from = from;
        this.to = to;
    }

    public int getFromVersion() {
        return from;
    }

    public int getToVersion() {
        return to;
    }
}
