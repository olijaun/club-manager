package org.jaun.clubmanager.eventstore;

import java.io.Serializable;
import java.util.UUID;

import com.github.msemys.esjc.EventData;
import com.github.msemys.esjc.EventStore;
import com.github.msemys.esjc.EventStoreBuilder;
import com.google.gson.Gson;

public class TestClient {


    public static void main(String[] args) throws Exception {

        Gson gson = new Gson();

        EventStore eventStore =
                EventStoreBuilder.newBuilder().singleNodeAddress("192.168.1.249", 9001).build();

        EventData clientEvent = EventData.newBuilder() //
                .eventId(UUID.randomUUID()) //
                .type("ClientEvent") //
                .jsonData(gson.toJson(new MyEvent("hello"))).build();

        eventStore.appendToStream("clienttest", -1, clientEvent);
    }
}
