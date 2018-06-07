package org.jaun.clubmanager.eventstore;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
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

import org.jaun.clubmanager.eventstore.feed.json.JsonAuthor;
import org.jaun.clubmanager.eventstore.feed.json.JsonEntry;
import org.jaun.clubmanager.eventstore.feed.json.JsonFeed;
import org.jaun.clubmanager.eventstore.feed.xml.XmlAuthor;
import org.jaun.clubmanager.eventstore.feed.xml.XmlEntry;
import org.jaun.clubmanager.eventstore.feed.xml.XmlFeed;
import org.jaun.clubmanager.eventstore.feed.xml.XmlLink;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Path("/streams")
public class EventStoreResource {

    @Autowired
    private EventStore eventStore;

    public static final int PAGE_SIZE = 20;

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


    @POST
    @Consumes("application/vnd.eventstore.events+json")
    @Path("/{stream-id}")
    public Response addEvents(@Context UriInfo uriInfo, @PathParam("stream-id") String streamId,
            @HeaderParam("ES-ExpectedVersion") Long expectedVersion, InputStream inputStream) {

        return addEvent(uriInfo, null, null, streamId, expectedVersion, inputStream);
    }

    @GET
    @Produces({"application/atom+xml"})
    @Path("/{stream-id}")
    public Response getEventFeedAsXml(@Context UriInfo uriInfo, @PathParam("stream-id") String streamId) {

        UriBuilder selfBuilder = uriInfo.getAbsolutePathBuilder();

        XmlFeed feed = new XmlFeed();
        feed.setTitle("EventStream '" + streamId + "'");
        feed.setId(selfBuilder.build());
        feed.setUpdated(ZonedDateTime.now(ZoneId.of("UTC")));

        feed.setAuthor(new XmlAuthor("EventStore"));

        XmlLink selfLink = new XmlLink(selfBuilder.build(), "self");
        XmlLink firstLink = new XmlLink(selfBuilder.path("head/backward/20").build(), "first");
        XmlLink metadataLink = new XmlLink(selfBuilder.path("metadata").build(), "metadata");

        feed.setLinks(Arrays.asList(selfLink, firstLink, metadataLink));

        XmlEntry entry = new XmlEntry();
        entry.setTitle("0@" + streamId);
        entry.setId(selfBuilder.path("0").build().toString());
        entry.setUpdated(new Date());
        entry.setAuthor(new XmlAuthor("EventStore"));
        entry.setSummary("eventType");

        feed.setEntries(Arrays.asList(entry));
//
//        XmlLink editLink = new XmlLink();
//        editLink.setRel("edit");
//        editLink.setHref(selfBuilder.path("0").build().toString());
//
//        XmlLink alternateLink = new XmlLink();
//        alternateLink.setRel("alternate");
//        alternateLink.setHref(selfBuilder.path("head/backward/20").build().toString());

//        entry.setLinks(Arrays.asList(editLink, alternateLink));


        return Response.ok(feed, "application/atom+xml").build();
    }


    @GET
    @Produces({MediaType.APPLICATION_JSON})
    @Path("/{stream-id}/{stream-revision}/{direction}/{page-size}")
    public Response getEvents(@Context UriInfo uriInfo, @PathParam("stream-revision") String streamRevisionAsString,
            @PathParam("stream-id") String streamIdAsString, @PathParam("direction") String direction,
            @PathParam("page-size") Integer pageSize) {

        StreamId streamId = StreamId.parse(streamIdAsString);

        boolean forward;
        if (direction.equals("forward")) {
            forward = true;
        } else if (direction.equals("backward")) {
            forward = false;
        } else {
            throw new NotFoundException("not found " + direction);
        }

        long totalStreamLength = eventStore.length(streamId);

        StreamRevision streamRevision =
                streamRevisionAsString.equals("head") ? StreamRevision.from(totalStreamLength - 1) : StreamRevision.from(
                        Long.parseLong(streamRevisionAsString));

        long begin;
        long end;
        if (forward) {
            begin = streamRevision.getValue();
            end = streamRevision.getValue() + pageSize - 1;
        } else {
            long tmpBegin = streamRevision.getValue() - PAGE_SIZE + 1;
            begin = tmpBegin < 0 ? 0 : tmpBegin;
            long tmpEnd = streamRevision.getValue();
            end = tmpEnd > (totalStreamLength - 1) ? (totalStreamLength - 1) : tmpEnd;
        }

        StoredEvents eventDataList = eventStore.read(streamId, StreamRevision.from(begin), StreamRevision.from(end));

        JsonFeed jsonFeed = toJsonFeed(uriInfo, streamId, eventDataList, totalStreamLength);

        return Response.ok(jsonFeed, MediaType.APPLICATION_JSON).build();
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON})
    @Path("/{stream-id}/{stream-revision}")
    public Response getEvent(@Context UriInfo uriInfo, @PathParam("stream-revision") String streamRevisionAsString,
            @PathParam("stream-id") String streamIdAsString) {

        StreamId streamId = StreamId.parse(streamIdAsString);

        StreamRevision streamRevision = toStreamRevision(streamId, streamRevisionAsString);

        StoredEvents storedEvents = eventStore.read(streamId, streamRevision, streamRevision);

        if (storedEvents.isEmpty()) {
            throw new NotFoundException("Event with revision " + streamIdAsString + " does not exist");
        }

        return Response.ok(storedEvents.iterator().next().getPayload(), MediaType.APPLICATION_JSON_TYPE).build();
    }

    private StreamRevision toStreamRevision(StreamId streamId, String streamRevisionAsString) {
        return streamRevisionAsString.equals("head") ? StreamRevision.from(eventStore.length(streamId) - 1) : StreamRevision.from(
                Long.parseLong(streamRevisionAsString));
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON})
    @Path("/{stream-id}")
    public Response getEventFeedAsJson(@Context UriInfo uriInfo, @QueryParam("embed") String embedOption,
            @PathParam("stream-id") String streamIdAsString) {

        boolean rich = false;
        boolean body = false;

        if (embedOption != null) {

            switch (embedOption) {
                case "rich":
                    rich = true;
                    break;
                case "body":
                case "PrettyBody":
                case "TryHarder":
                    rich = true;
                    body = true;
                default:
                    throw new IllegalArgumentException("unknown embed option" + embedOption);
            }
        }

        StreamId streamId = StreamId.parse(streamIdAsString);

        long totalStreamLength = eventStore.length(streamId);

        long tmpBegin = totalStreamLength - PAGE_SIZE;
        long begin = tmpBegin < 0 ? 0 : tmpBegin;
        long end = totalStreamLength - 1;

        StoredEvents eventDataList = eventStore.read(streamId, StreamRevision.from(begin), StreamRevision.from(end));

        JsonFeed jsonFeed = toJsonFeed(uriInfo, streamId, eventDataList, totalStreamLength);

        return Response.ok(jsonFeed, MediaType.APPLICATION_JSON).build();

    }

    private JsonFeed toJsonFeed(@Context UriInfo uriInfo, StreamId streamId, StoredEvents storedEvents, long totalStreamLength) {

        JsonFeed jsonFeed = new JsonFeed();

        jsonFeed.setAuthor(new JsonAuthor("EventStore"));
        jsonFeed.setId(uriInfo.getAbsolutePath());
        jsonFeed.setUpdated(storedEvents.highestRevisionEvent().getTimestamp());
        jsonFeed.setStreamId(streamId.getValue());
        jsonFeed.setTitle("EventStream '" + streamId.getValue() + "'");

        jsonFeed.addLink(uriInfo.getBaseUriBuilder().path("streams").path(streamId.getValue()).build(), "self");
        jsonFeed.addLink(
                uriInfo.getBaseUriBuilder().path("streams").path(streamId.getValue()).path("head/backward/" + PAGE_SIZE).build(),
                "first");

        if (totalStreamLength > PAGE_SIZE) {
            // only exists if there is more than one page
            jsonFeed.addLink(
                    uriInfo.getBaseUriBuilder().path("streams").path(streamId.getValue()).path("0/forward/" + PAGE_SIZE).build(),
                    "last");
        }

        if (storedEvents.lowestRevision().getValue() > 0) {
            jsonFeed.addLink(uriInfo.getBaseUriBuilder()
                    .path("streams")
                    .path(streamId.getValue())
                    .path(storedEvents.highestRevision().getValue() - PAGE_SIZE + "/backward/" + PAGE_SIZE)
                    .build(), "next");
        }

        // always exist and can be used to read future events
        jsonFeed.addLink(uriInfo.getBaseUriBuilder()
                .path("streams")
                .path(streamId.getValue())
                .path(storedEvents.highestRevision().add(1).getValue() + "/forward/" + PAGE_SIZE)
                .build(), "previous");

        boolean headOfStream = (totalStreamLength - 1) == storedEvents.highestRevision().getValue();
        jsonFeed.setHeadOfStream(headOfStream);

        if (headOfStream) {
            jsonFeed.setSelfUrl(uriInfo.getAbsolutePathBuilder().build());
        }

        jsonFeed.addLink(uriInfo.getBaseUriBuilder().path("streams").path(streamId.getValue()).path("metadata").build(),
                "metadata");

        for (StoredEventData eventData : storedEvents.newestFirstList()) {
            JsonEntry entry = new JsonEntry();
            entry.setTitle(eventData.getStreamRevision().getValue() + "@" + streamId.getValue());
            entry.setId(uriInfo.getAbsolutePathBuilder().path(eventData.getStreamRevision().getValue().toString()).build());
            entry.setUpdated(eventData.getTimestamp());
            entry.setAuthor("EventStore");
            entry.setSummary(eventData.getEventType().getValue());
            entry.addLink(uriInfo.getAbsolutePathBuilder().path(eventData.getStreamRevision().getValue().toString()).build(),
                    "self");
            entry.addLink(uriInfo.getAbsolutePathBuilder().path(eventData.getStreamRevision().getValue().toString()).build(),
                    "alternate");

            jsonFeed.getEntries().add(entry);
        }

        jsonFeed.seteTag(storedEvents.highestRevision().getValue() + ";" + storedEvents.size()); // TODO

        return jsonFeed;

    }
}
