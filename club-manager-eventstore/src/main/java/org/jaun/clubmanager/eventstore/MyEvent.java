package org.jaun.clubmanager.eventstore;

public class MyEvent {
    public String clientTest;

    public MyEvent(String clientTest) {
        this.clientTest = clientTest;
    }

    public String getClientTest() {
        return clientTest;
    }

    public void setClientTest(String clientTest) {
        this.clientTest = clientTest;
    }
}

