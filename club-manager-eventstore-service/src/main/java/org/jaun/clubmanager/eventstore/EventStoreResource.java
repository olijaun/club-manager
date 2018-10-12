package org.jaun.clubmanager.eventstore;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonStructure;
import javax.json.JsonValue;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.RedirectionException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import org.jaun.clubmanager.domain.model.commons.EventId;
import org.jaun.clubmanager.eventstore.feed.json.JsonAuthor;
import org.jaun.clubmanager.eventstore.feed.json.JsonEntry;
import org.jaun.clubmanager.eventstore.feed.json.JsonFeed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Path("/streams")
public class EventStoreResource {

    private static final int DEFAULT_PAGE_SIZE = 20;

    private static enum EmbedOption {
        NONE, RICH, BODY
    }

    @Autowired
    private EventStore eventStore;

    @POST
    @Consumes("application/vnd.eventstore.events+json")
    @Path("/{stream-id}")
    public Response addEvents(@Context UriInfo uriInfo, @PathParam("stream-id") String streamId,
            @HeaderParam("ES-ExpectedVersion") Long expectedVersion, InputStream inputStream) {

        return addEvent(uriInfo, null, null, streamId, expectedVersion, inputStream);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{stream-id}/incoming/{event-id}")
    public Response addEventWithIdInPath(@Context UriInfo uriInfo, @PathParam("stream-id") String streamIdAsString,
            @PathParam("event-id") String eventIdAsString, @HeaderParam("ES-EventType") String eventTypeAsString,
            @HeaderParam("ES-ExpectedVersion") Long expectedVersionAsLong, InputStream inputStream) {

        return addEvent(uriInfo, eventIdAsString, eventTypeAsString, streamIdAsString, expectedVersionAsLong, inputStream);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{stream-id}")
    public Response addEventWithIdInHeader(@Context UriInfo uriInfo, @HeaderParam("ES-EventId") String eventIdAsString,
            @HeaderParam("ES-EventType") String eventTypeAsString, @HeaderParam("ES-ExpectedVersion") Long expectedVersionAsLong,
            @PathParam("stream-id") String streamId, InputStream inputStream) {

        if (eventIdAsString == null) {
            URI redirectLocation = uriInfo.getAbsolutePathBuilder().path("incoming").path(UUID.randomUUID().toString()).build();
            throw new RedirectionException(Response.Status.TEMPORARY_REDIRECT, redirectLocation);
        }

        return addEvent(uriInfo, eventIdAsString, eventTypeAsString, streamId, expectedVersionAsLong, inputStream);
    }

    private Response addEvent(UriInfo uriInfo, String eventIdAsString, String eventTypeAsString, String streamIdAsString,
            Long expectedVersionAsLong, InputStream inputStream) {

        StreamId streamId = StreamId.parse(streamIdAsString);
        StreamRevision expectedVersion = StreamRevision.from(expectedVersionAsLong);

        JsonReader reader = Json.createReader(new InputStreamReader(inputStream));

        JsonStructure jsonStructure = reader.read();
        if (jsonStructure.getValueType().equals(JsonValue.ValueType.OBJECT)) {
            JsonObject jsonObject = (JsonObject) jsonStructure;


            if (eventTypeAsString == null) {
                throw new BadRequestException(
                        "Must include an event type with the request either in body or as ES-EventType header.");
            }

            EventId eventId = new EventId(UUID.fromString(eventIdAsString));
            EventType eventType = new EventType(eventTypeAsString);
            EventData eventData = new EventData(eventId, eventType, jsonObject.toString(), null);

            try {
                StreamRevision newRevision = eventStore.append(streamId, Collections.singletonList(eventData), expectedVersion);

                UriBuilder path = uriInfo.getAbsolutePathBuilder()
                        .replacePath(uriInfo.getPath().replaceAll("/incoming.+", ""))
                        .path(newRevision.getValue().toString().replaceAll("\\D+", ""));
                return Response.created(path.build()).build();

            } catch (ConcurrencyException e) {
                throw new BadRequestException("Wrong expected EventNumber", e);
            }

        } else if (jsonStructure.getValueType().equals(JsonValue.ValueType.ARRAY)) {

            JsonArray jsonArray = (JsonArray) jsonStructure;

            if (jsonArray.isEmpty()) {
                // request must contain at least one event
                throw new BadRequestException("Write request body invalid.");
            }

            List<JsonObject> jsonObjectList = new ArrayList<>(jsonArray.size());
            for (int i = 0; i < jsonArray.size(); i++) {
                JsonObject jsonObject = jsonArray.getJsonObject(i);
                jsonObjectList.add(jsonObject);
            }

            List<EventData> eventDataList = jsonObjectList.stream().map(this::toEventData).collect(Collectors.toList());

            try {
                StreamRevision newRevision = eventStore.append(streamId, eventDataList, expectedVersion);

                // event store returns location to first revision that was added (and not latest as one might think)
                UriBuilder path = uriInfo.getAbsolutePathBuilder()
                        .replacePath("/" + streamIdAsString)
                        .path(newRevision.getValue().toString().replaceAll("\\D+", ""));
                return Response.created(path.build()).build();
            } catch (ConcurrencyException e) {
                throw new BadRequestException("Wrong expected EventNumber", e);
            }
        } else {
            throw new BadRequestException("Write request body invalid.");
        }
    }

    private EventData toEventData(JsonObject jsonObject) {

        String eventIdAsString = jsonObject.getString("eventId");
        String eventTypeAsString = jsonObject.getString("eventType");
        String data = jsonObject.getJsonObject("data").toString();

        if (eventIdAsString == null) {
            throw new BadRequestException("Empty eventId provided.");
        }

        if (eventTypeAsString == null) {
            throw new BadRequestException("Empty eventType provided.");
        }

        EventId eventId = new EventId(UUID.fromString(eventIdAsString));
        EventType eventType = new EventType(eventTypeAsString);
        return new EventData(eventId, eventType, data, null);
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON})
    @Path("/{stream-id}")
    public Response getEventFeedAsJson(@Context UriInfo uriInfo, @QueryParam("embed") String embedOptionAsString,
            @PathParam("stream-id") String streamIdAsString) {

        StreamId streamId = StreamId.parse(streamIdAsString);
        EmbedOption embedOption = parseEmbedOption(embedOptionAsString);
        long totalStreamLength = eventStore.length(streamId);

        StreamRevision streamRevision = totalStreamLength > 0 ? StreamRevision.from(totalStreamLength - 1) : StreamRevision.from(0);

        // get the "first" page. Means: the page with the highest revision
        StoredEvents storedEvents =
                getEventsBackward(streamId, totalStreamLength, DEFAULT_PAGE_SIZE, StreamRevision.from(totalStreamLength - 1));

        JsonFeed jsonFeed = toJsonFeed(uriInfo, streamId, storedEvents, totalStreamLength, DEFAULT_PAGE_SIZE, embedOption);

        return Response.ok(jsonFeed, MediaType.APPLICATION_JSON).build();
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON})
    @Path("/{stream-id}/{stream-revision}/{direction}/{page-size}")
    public Response getEventFeedAsJsonWithDirections(@Context UriInfo uriInfo, @QueryParam("embed") String embedOptionAsString,
            @PathParam("stream-revision") String streamRevisionAsString, @PathParam("stream-id") String streamIdAsString,
            @PathParam("direction") String direction, @PathParam("page-size") Integer pageSize) {

        StreamId streamId = StreamId.parse(streamIdAsString);
        boolean forward = parseIsForward(direction);
        EmbedOption embedOption = parseEmbedOption(embedOptionAsString);
        long totalStreamLength = eventStore.length(streamId);
        StreamRevision streamRevision =
                streamRevisionAsString.equals("head") ? StreamRevision.from(totalStreamLength - 1) : StreamRevision.from(
                        Long.parseLong(streamRevisionAsString));

        StoredEvents storedEvents = forward ? //
                getEventsForward(streamId, totalStreamLength, pageSize, streamRevision) //
                : getEventsBackward(streamId, totalStreamLength, pageSize, streamRevision);

        JsonFeed jsonFeed = toJsonFeed(uriInfo, streamId, storedEvents, totalStreamLength, pageSize, embedOption);

        return Response.ok(jsonFeed, MediaType.APPLICATION_JSON).build();
    }

    public StoredEvents getEventsForward(StreamId streamId, long totalStreamLength, int pageSize, StreamRevision streamRevision) {

        long begin = streamRevision.getValue();
        long end = streamRevision.getValue() + pageSize - 1;

        try {
            return eventStore.read(streamId, StreamRevision.from(begin), StreamRevision.from(end));
        } catch (StreamNotFoundException e) {
            throw new NotFoundException(e);
        }
    }

    public StoredEvents getEventsBackward(StreamId streamId, long totalStreamLength, int pageSize, StreamRevision streamRevision) {

        long tmpBegin = streamRevision.getValue() - pageSize + 1;
        long begin = tmpBegin < 0 ? 0 : tmpBegin;
        long tmpEnd = streamRevision.getValue();
        long end = tmpEnd > (totalStreamLength - 1) ? (totalStreamLength - 1) : tmpEnd;

        try {
            return eventStore.read(streamId, StreamRevision.from(begin), StreamRevision.from(end));
        } catch (StreamNotFoundException e) {
            throw new NotFoundException(e);
        }
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON})
    @Path("/{stream-id}/{stream-revision}")
    public Response getEvent(@Context UriInfo uriInfo, @PathParam("stream-revision") String streamRevisionAsString,
            @PathParam("stream-id") String streamIdAsString) {

        StreamId streamId = StreamId.parse(streamIdAsString);

        StreamRevision streamRevision = toStreamRevision(streamId, streamRevisionAsString);

        StoredEvents storedEvents = null;
        try {
            storedEvents = eventStore.read(streamId, streamRevision, streamRevision);
        } catch (StreamNotFoundException e) {
            throw new NotFoundException(e);
        }

        if (storedEvents.isEmpty()) {
            throw new NotFoundException("Event with revision " + streamIdAsString + " does not exist");
        }

        return Response.ok(storedEvents.iterator().next().getPayload(), MediaType.APPLICATION_JSON_TYPE).build();
    }

    private StreamRevision toStreamRevision(StreamId streamId, String streamRevisionAsString) {
        return streamRevisionAsString.equals("head") ? StreamRevision.from(eventStore.length(streamId) - 1) : StreamRevision.from(
                Long.parseLong(streamRevisionAsString));
    }

    private JsonFeed toJsonFeed(@Context UriInfo uriInfo, StreamId streamId, StoredEvents storedEvents, long totalStreamLength,
            int pageSize, EmbedOption embedOption) {

        JsonFeed jsonFeed = new JsonFeed();

        jsonFeed.setAuthor(new JsonAuthor("EventStore"));
        jsonFeed.setId(uriInfo.getAbsolutePath());
        jsonFeed.setUpdated(storedEvents.highestPositionEvent().map(StoredEventData::getTimestamp).orElse(null));
        jsonFeed.setStreamId(streamId.getValue());
        jsonFeed.setTitle("EventStream '" + streamId.getValue() + "'");

        jsonFeed.addLink(uriInfo.getBaseUriBuilder().path("streams").path(streamId.getValue()).build(), "self");
        jsonFeed.addLink(
                uriInfo.getBaseUriBuilder().path("streams").path(streamId.getValue()).path("head/backward/" + pageSize).build(),
                "first");

        if (totalStreamLength > pageSize) {
            // only exists if there is more than one page
            jsonFeed.addLink(
                    uriInfo.getBaseUriBuilder().path("streams").path(streamId.getValue()).path("0/forward/" + pageSize).build(),
                    "last");
        }

        if (storedEvents.lowestPosition().isPresent() && storedEvents.lowestPosition().get().getValue() > 0) {
            jsonFeed.addLink(uriInfo.getBaseUriBuilder()
                    .path("streams")
                    .path(streamId.getValue())
                    .path(storedEvents.highestPosition().get().getValue() - pageSize + "/backward/" + pageSize)
                    .build(), "next");
        }

        // always exist and can be used to read future events
        jsonFeed.addLink(uriInfo.getBaseUriBuilder()
                .path("streams")
                .path(streamId.getValue())
                .path(storedEvents.highestPosition().orElseGet(() -> StreamRevision.NEW_STREAM).add(1).getValue() + "/forward/"
                      + pageSize)
                .build(), "previous");

        boolean headOfStream = storedEvents.isEmpty() || (totalStreamLength - 1) == storedEvents.highestPosition().get().getValue();
        jsonFeed.setHeadOfStream(headOfStream);

        jsonFeed.setSelfUrl(uriInfo.getAbsolutePathBuilder().build());

        jsonFeed.addLink(uriInfo.getBaseUriBuilder().path("streams").path(streamId.getValue()).path("metadata").build(),
                "metadata");

        for (StoredEventData eventData : storedEvents.newestFirstList()) {
            JsonEntry entry = new JsonEntry();
            entry.setTitle(eventData.getStreamRevision().getValue() + "@" + eventData.getStreamId().getValue());

            entry.setId(uriInfo.getBaseUriBuilder()
                    .path("streams")
                    .path(eventData.getStreamId().getValue())
                    .path(eventData.getStreamRevision().getValue().toString())
                    .build());
            entry.setUpdated(eventData.getTimestamp());
            entry.setAuthor("EventStore");
            entry.setSummary(eventData.getEventType().getValue());

            entry.addLink(uriInfo.getBaseUriBuilder()
                    .path("streams")
                    .path(eventData.getStreamId().getValue())
                    .path(eventData.getStreamRevision().getValue().toString())
                    .build(), "self");

            entry.addLink(uriInfo.getBaseUriBuilder()
                    .path("streams")
                    .path(eventData.getStreamId().getValue())
                    .path(eventData.getStreamRevision().getValue().toString())
                    .build(), "alternate");

            if (embedOption.equals(EmbedOption.RICH) || embedOption.equals(EmbedOption.BODY)) {
                entry.setEventId(eventData.getEventId().getUuid().toString());
                entry.setEventType(eventData.getEventType().getValue());
                entry.setEventNumber(Math.toIntExact(eventData.getStreamRevision().getValue()));
                entry.setStreamId(eventData.getStreamId().getValue());
                entry.setJson(true);
                entry.setMetaData(false); // TODO
                entry.setLinkMetaData(false); // TODO
                entry.setPositionEventNumber(Math.toIntExact(eventData.getPosition())); // TODO
                entry.setPositionStreamId(streamId.getValue());
            }

            if (embedOption.equals(EmbedOption.BODY)) {
                entry.setData(eventData.getPayload());
            }

            jsonFeed.getEntries().add(entry);
        }

        if (storedEvents.lowestPosition().isPresent()) {
            jsonFeed.seteTag(
                    storedEvents.highestPosition().get().getValue() + "-" + storedEvents.lowestPosition().get().getValue() + ";"
                    + storedEvents.size()); // TODO
        }

        return jsonFeed;
    }

    private boolean parseIsForward(String direction) {

        if (direction.equals("forward")) {
            return true;
        } else if (direction.equals("backward")) {
            return false;
        } else {
            throw new NotFoundException("not found " + direction);
        }
    }

    private EmbedOption parseEmbedOption(String embedOption) {

        if (embedOption == null) {
            return EmbedOption.NONE;
        }

        switch (embedOption) {
            case "rich":
                return EmbedOption.RICH;
            case "body":
                return EmbedOption.BODY;
            default:
                throw new BadRequestException("unknown embed option: " + embedOption);

        }
    }
}