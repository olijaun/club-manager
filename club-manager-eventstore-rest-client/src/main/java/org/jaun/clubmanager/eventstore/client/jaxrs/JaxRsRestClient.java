package org.jaun.clubmanager.eventstore.client.jaxrs;


import static java.util.Objects.requireNonNull;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;

import org.jaun.clubmanager.eventstore.ConcurrencyException;
import org.jaun.clubmanager.eventstore.EventData;
import org.jaun.clubmanager.eventstore.EventId;
import org.jaun.clubmanager.eventstore.EventStoreClient;
import org.jaun.clubmanager.eventstore.EventType;
import org.jaun.clubmanager.eventstore.StoredEvents;
import org.jaun.clubmanager.eventstore.StreamId;
import org.jaun.clubmanager.eventstore.StreamRevision;

public class JaxRsRestClient implements EventStoreClient {

    private final Client client;
    private final URI target;

    public JaxRsRestClient(String targetUri) {
        this(ClientBuilder.newClient(), JaxRsRestClient.toURI(targetUri));
    }

    public JaxRsRestClient(URI target) {
        this(ClientBuilder.newClient(), target);
    }

    public JaxRsRestClient(Client client, URI target) {
        this.client = requireNonNull(client);
        this.target = requireNonNull(target);
    }

    @Override
    public void append(StreamId streamId, List<EventData> evenDataList, StreamRevision expectedVersion)
            throws ConcurrencyException {

        if (evenDataList.isEmpty()) {
            return;
        }

        if (evenDataList.size() > 1) {
            throw new IllegalStateException("not implemented yet");
        } else {

            EventData eventData = evenDataList.get(0);

            client.target(target)
                    .path("streams")
                    .path(streamId.getValue())
                    .request(MediaType.APPLICATION_JSON_TYPE)
                    .header("ES-EventId", eventData.getEventId().getUuid().toString())
                    .header("ES-EventType", eventData.getEventType().getValue())
                    .post(Entity.json(eventData.getPayload()), String.class);
        }
    }

    @Override
    public StoredEvents read(StreamId streamId) {
        return null;
    }

    @Override
    public StoredEvents read(StreamId streamId, StreamRevision versionGreaterThan) {
        return null;
    }

    @Override
    public StoredEvents read(StreamId streamId, StreamRevision fromRevision, StreamRevision toRevision) {
        return null;
    }


    public static void main(String[] args) throws Exception {

        StreamId jaxRsStream = StreamId.parse("jax-1");

        EventData eventData = new EventData(EventId.generate(), new EventType("abc"), "{ \"jax\": \"rs\" }", null);

        JaxRsRestClient client = new JaxRsRestClient("http://localhost:9001/api");

        client.append(jaxRsStream, Collections.singletonList(eventData), StreamRevision.NEW_STREAM);
    }

    private static URI toURI(String stringUri) {
        try {
            return new URI(stringUri);
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("cannot create uri: " + stringUri);
        }
    }
}
