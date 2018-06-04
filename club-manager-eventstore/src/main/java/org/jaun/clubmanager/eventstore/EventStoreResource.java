package org.jaun.clubmanager.eventstore;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
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

import org.jaun.clubmanager.eventstore.feed.xml.Author;
import org.jaun.clubmanager.eventstore.feed.xml.Entry;
import org.jaun.clubmanager.eventstore.feed.xml.Link;
import org.jaun.clubmanager.eventstore.feed.xml.XmlFeed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Path("/streams")
public class EventStoreResource {

    @Autowired
    private EventStore eventStore;

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
        feed.setId(selfBuilder.build().toASCIIString());
        feed.setUpdated(new Date());

        Author author = new Author();
        author.setName("EventStore");
        feed.setAuthor(author);

        Link selfLink = new Link();
        selfLink.setRel("self");
        selfLink.setHref(selfBuilder.build().toString());

        Link firstLink = new Link();
        firstLink.setRel("first");
        firstLink.setHref(selfBuilder.path("head/backward/20").build().toString());

        Link previousLink = new Link();
        previousLink.setRel("previous");
        previousLink.setHref(selfBuilder.path("8/forward/20").build().toString());

        Link metadataLink = new Link();
        metadataLink.setRel("metadata");
        metadataLink.setHref(selfBuilder.path("metadata").build().toString());

        feed.setLinks(Arrays.asList(selfLink, firstLink, previousLink, metadataLink));

        Entry entry = new Entry();
        entry.setTitle("0@" + streamId);
        entry.setId(selfBuilder.path("0").build().toString());
        entry.setUpdated(new Date());
        entry.setAuthor(author);
        entry.setSummary("eventType");

        feed.setEntries(Arrays.asList(entry));

        Link editLink = new Link();
        editLink.setRel("edit");
        editLink.setHref(selfBuilder.path("0").build().toString());

        Link alternateLink = new Link();
        alternateLink.setRel("alternate");
        alternateLink.setHref(selfBuilder.path("head/backward/20").build().toString());

        entry.setLinks(Arrays.asList(editLink, alternateLink));


        return Response.ok(feed, "application/atom+xml").build();
    }


    @GET
    @Produces({MediaType.APPLICATION_JSON})
    @Path("/{stream-id}")
    public Response getEventFeedAsJson(@Context UriInfo uriInfo, @QueryParam("embed") String embedOption,
            @PathParam("stream-id") String streamId) {

        boolean rich = false;
        boolean body = false;

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

//        SyndFeed feed = new SyndFeedImpl();
//        feed.setFeedType("atom_1.0");
//
//        feed.setTitle("Event stream " + streamId);
//        feed.setUri(uriInfo.getAbsolutePathBuilder().build().toASCIIString());
//        feed.setPublishedDate(new Date());
//        feed.setAuthor("EventStore");
//        feed.setDocs("eventType");

//        feed.setLink("http://rome.dev.java.net");
//        feed.setDescription("This feed has been created using ROME (Java syndication utilities");


//        StreamingOutput stream = new StreamingOutput() {
//            @Override
//            public void write(OutputStream os) throws IOException, WebApplicationException {
//                Writer writer = new BufferedWriter(new OutputStreamWriter(os));
//
//                for (org.neo4j.graphdb.Path path : paths) {
//                    writer.write(path.toString() + "\n");
//                }
//                writer.flush();
//            }
//        };

        return Response.ok(null, MediaType.APPLICATION_JSON).build();

//        SyndFeedOutput output = new SyndFeedOutput();
//        try(StringWriter writer = new StringWriter()) {
//            output.output(feed, writer);
//            writer.close();
//
//            return Response.ok(writer.toString(), "application/atom+xml").build();
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        } catch (FeedException e) {
//            throw new RuntimeException(e);
//        }
    }
}
