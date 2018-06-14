package org.jaun.clubmanager.eventstore.client.jaxrs;


import static java.util.Objects.requireNonNull;

import java.io.StringReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;

import org.jaun.clubmanager.eventstore.ConcurrencyException;
import org.jaun.clubmanager.eventstore.EventData;
import org.jaun.clubmanager.eventstore.EventId;
import org.jaun.clubmanager.eventstore.EventStoreClient;
import org.jaun.clubmanager.eventstore.EventType;
import org.jaun.clubmanager.eventstore.StoredEventData;
import org.jaun.clubmanager.eventstore.StoredEvents;
import org.jaun.clubmanager.eventstore.StreamId;
import org.jaun.clubmanager.eventstore.StreamRevision;

public class JaxRsRestClient implements EventStoreClient {

    private final Client client;
    private final URI target;
    private static final String EVENTSTORE_JSON_TYPE = "application/vnd.eventstore.events+json";

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
    public void append(StreamId streamId, List<EventData> eventDataList, StreamRevision expectedVersion)
            throws ConcurrencyException {

        if (eventDataList.isEmpty()) {
            return;
        }

        if (eventDataList.size() > 1) {

            JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
            for (EventData eventData : eventDataList) {

                JsonObjectBuilder jsonObjectBuilder = Json.createObjectBuilder();
                jsonObjectBuilder.add("eventId", eventData.getEventId().getUuid().toString());
                jsonObjectBuilder.add("eventType", eventData.getEventType().getValue());

                JsonObject jsonPayload = Json.createReader(new StringReader(eventData.getPayload())).readObject();

                jsonObjectBuilder.add("data", jsonPayload);

                arrayBuilder.add(jsonObjectBuilder);
            }

            client.target(target)
                    .path("streams")
                    .path(streamId.getValue())
                    .request()
                    .post(Entity.entity(arrayBuilder.build().toString(), EVENTSTORE_JSON_TYPE));

        } else {

            EventData eventData = eventDataList.get(0);

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

        JsonFeed jsonFeed = client.target(target)
                .path("streams")
                .path(streamId.getValue())
                .queryParam("embed", "body")
                .request(MediaType.APPLICATION_JSON_TYPE)
                .get(JsonFeed.class);

        List<StoredEventData> storedEventDataList = new ArrayList<>();

        for (JsonEntry entry : jsonFeed.getEntries()) {

            Instant timestamp = Instant.from(DateTimeFormatter.ISO_ZONED_DATE_TIME.parse(entry.getUpdated()));

            StoredEventData storedEventData = new StoredEventData(streamId, new EventId(UUID.fromString(entry.getEventId())),
                    new EventType(entry.getEventType()), entry.getData(), null, StreamRevision.from(entry.getEventNumber()),
                    timestamp, Math.toIntExact(entry.getPositionEventNumber()));

            storedEventDataList.add(storedEventData);
        }

        return new StoredEvents(storedEventDataList);
    }


    public static void main(String[] args) throws Exception {

        StreamId jaxRsStream = StreamId.parse("jax-2");

        EventData eventData1 = new EventData(EventId.generate(), new EventType("abc1"), "{ \"jax\": \"rs1\" }", null);
        EventData eventData2 = new EventData(EventId.generate(), new EventType("abc2"), "{ \"jax\": \"rs2\" }", null);

        JaxRsRestClient client = new JaxRsRestClient("http://localhost:9001/api");

        client.append(jaxRsStream, Arrays.asList(eventData1, eventData2), StreamRevision.NEW_STREAM);


        StoredEvents storedEvents = client.read(jaxRsStream, StreamRevision.INITIAL, StreamRevision.MAXIMUM);

        for (StoredEventData e : storedEvents) {
            System.out.println(e);
        }
    }

    private static URI toURI(String stringUri) {
        try {
            return new URI(stringUri);
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("cannot create uri: " + stringUri);
        }
    }
}
