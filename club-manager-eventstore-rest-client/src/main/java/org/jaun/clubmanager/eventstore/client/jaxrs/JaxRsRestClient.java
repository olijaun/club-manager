package org.jaun.clubmanager.eventstore.client.jaxrs;


import static java.util.Objects.requireNonNull;

import java.io.StringReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;

import org.jaun.clubmanager.domain.model.commons.EventId;
import org.jaun.clubmanager.eventstore.CatchUpSubscription;
import org.jaun.clubmanager.eventstore.CatchUpSubscriptionListener;
import org.jaun.clubmanager.eventstore.ConcurrencyException;
import org.jaun.clubmanager.eventstore.EventData;
import org.jaun.clubmanager.eventstore.EventStoreClient;
import org.jaun.clubmanager.eventstore.EventType;
import org.jaun.clubmanager.eventstore.StoredEventData;
import org.jaun.clubmanager.eventstore.StoredEvents;
import org.jaun.clubmanager.eventstore.StreamId;
import org.jaun.clubmanager.eventstore.StreamNotFoundException;
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
                    .header("ES-ExpectedVersion", expectedVersion.getValue().intValue())
                    .post(Entity.entity(arrayBuilder.build().toString(), EVENTSTORE_JSON_TYPE), String.class);

        } else {

            EventData eventData = eventDataList.get(0);

            client.target(target)
                    .path("streams")
                    .path(streamId.getValue())
                    .request(MediaType.APPLICATION_JSON_TYPE)
                    .header("ES-EventId", eventData.getEventId().getUuid().toString())
                    .header("ES-EventType", eventData.getEventType().getValue())
                    .header("ES-ExpectedVersion", expectedVersion.getValue().intValue())
                    .post(Entity.json(eventData.getPayload()), String.class);
        }
    }

    @Override
    public StoredEvents read(StreamId streamId) throws StreamNotFoundException {
        return read(streamId, StreamRevision.INITIAL, StreamRevision.MAXIMUM);
    }

    @Override
    public StoredEvents read(StreamId streamId, StreamRevision fromRevision) throws StreamNotFoundException {
        return read(streamId, fromRevision, StreamRevision.MAXIMUM);
    }

    @Override
    public StoredEvents read(StreamId streamId, StreamRevision fromRevision, StreamRevision toRevision)
            throws StreamNotFoundException {

        // query the head of stream
        JsonFeed jsonFeed;

        try {
            jsonFeed = client.target(target)
                    .path("streams")
                    .path(streamId.getValue())
                    .queryParam("embed", "body")
                    .request(MediaType.APPLICATION_JSON_TYPE)
                    .get(JsonFeed.class);
        } catch (NotFoundException e) {
            throw new StreamNotFoundException(streamId);
        }

        long from = fromRevision.getValue();

        if (jsonFeed.getEntries().isEmpty()) {
            return new StoredEvents(Collections.emptyList());
        }

        long to = (long) jsonFeed.getEntries().get(0).getPositionEventNumber();

        if (from > to || to == 0) {
            return new StoredEvents(Collections.emptyList());
        }

        int arraySize = Math.toIntExact(to - from + 1);

        if (arraySize == 0) {
            return new StoredEvents(Collections.emptyList());
        }

        StoredEventData[] storedEventDataArray = new StoredEventData[arraySize];
        readEvents(streamId, StreamRevision.from(from), StreamRevision.from(to), jsonFeed, storedEventDataArray);

        while (getNextLink(jsonFeed).isPresent()) {

            jsonFeed = client.target(getNextLink(jsonFeed).get())
                    .queryParam("embed", "body")
                    .request(MediaType.APPLICATION_JSON_TYPE)
                    .get(JsonFeed.class);

            readEvents(streamId, fromRevision, toRevision, jsonFeed, storedEventDataArray);

            if (storedEventDataArray[0] != null && storedEventDataArray[arraySize - 1] != null) {
                break;
            }
        }

        return new StoredEvents(Arrays.asList(storedEventDataArray));
    }

    private void readEvents(StreamId streamId, StreamRevision fromRevision, StreamRevision toRevision, JsonFeed jsonFeed,
            StoredEventData[] storedEventDataArray) {

        for (JsonEntry entry : jsonFeed.getEntries()) {

            Instant timestamp = Instant.from(DateTimeFormatter.ISO_ZONED_DATE_TIME.parse(entry.getUpdated()));

            StoredEventData storedEventData = new StoredEventData(streamId, new EventId(UUID.fromString(entry.getEventId())),
                    new EventType(entry.getEventType()), entry.getData(), null, StreamRevision.from(entry.getEventNumber()),
                    timestamp, Math.toIntExact(entry.getPositionEventNumber()));

            if (storedEventData.getPosition() <= toRevision.getValue()
                && storedEventData.getPosition() >= fromRevision.getValue()) {

                int arrayOffset = -1 * fromRevision.getValue().intValue();
                int arrayPosition = Math.toIntExact(storedEventData.getPosition() + arrayOffset);
                storedEventDataArray[arrayPosition] = storedEventData;
            }
        }
    }

    private Optional<URI> getNextLink(JsonFeed feed) {

        return getLink(feed, "next");
    }

    private Optional<URI> getPreviousLink(JsonFeed feed) {

        return getLink(feed, "previous");
    }

    private Optional<URI> getLink(JsonFeed feed, String relation) {

        List<JsonLink> links = feed.getLinks();

        Optional<JsonLink> link = links.stream().filter(l -> l.getRelation().equals(relation)).findFirst();

        return link.map(p -> {
            try {
                return new URI(p.getUri());
            } catch (URISyntaxException e) {
                throw new IllegalArgumentException("not a valid uri: " + p.getUri());
            }
        });
    }


    public static void main(String[] args) throws Exception {

        StreamId jaxRsStream = StreamId.parse("jax-4");
        EventData eventData1 = new EventData(EventId.generate(), new EventType("abc1"), "{ \"jax\": \"rs1\" }", null);
        EventData eventData2 = new EventData(EventId.generate(), new EventType("abc2"), "{ \"jax\": \"rs2\" }", null);

        JaxRsRestClient client = new JaxRsRestClient("http://localhost:9001/api");
        client.append(jaxRsStream, Arrays.asList(eventData1, eventData2), StreamRevision.UNSPECIFIED);

        PollingCatchUpSubscription subscription =
                new PollingCatchUpSubscription(client, jaxRsStream, StreamRevision.INITIAL, 2 * 1000,
                        new CatchUpSubscriptionListener() {
                            @Override
                            public void onEvent(CatchUpSubscription subscription, StoredEventData eventData) {
                                System.out.println("got event: " + eventData);
                            }

                            @Override
                            public void onClose(CatchUpSubscription subscription, Optional<Exception> e) {
                                System.out.println("closed subscription");
                            }
                        });
        subscription.start();
    }

    private static URI toURI(String stringUri) {
        try {
            return new URI(stringUri);
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("cannot create uri: " + stringUri);
        }
    }
}
