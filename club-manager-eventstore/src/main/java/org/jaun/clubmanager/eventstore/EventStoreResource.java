package org.jaun.clubmanager.eventstore;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonStructure;
import javax.json.JsonValue;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.Consumes;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.RedirectionException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Path("/streams")
public class EventStoreResource {

    @Autowired
    private EventStore eventStore;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{stream-id}")
    public Response addEventWithIdInHeader(@Context UriInfo uriInfo, @HeaderParam("ES-EventId") String eventId,
            @HeaderParam("ES-EventType") String eventType, @HeaderParam("ES-ExpectedVersion") Long expectedVersion,
            @PathParam("stream-id") String streamId, InputStream inputStream) {

        addEvent(uriInfo, eventId, eventType, streamId, expectedVersion, inputStream);

        UriBuilder path = uriInfo.getAbsolutePathBuilder().path("/0");
        return Response.created(path.build()).build();
    }

    private Response addEvent(UriInfo uriInfo, String eventIdAsString, String eventTypeAsString, String streamIdAsString,
            Long expectedVersion, InputStream inputStream) {

        StreamId streamId = StreamId.parse(streamIdAsString);
        StreamRevision streamRevision = StreamRevision.from(expectedVersion);

        JsonReader reader = Json.createReader(new InputStreamReader(inputStream));

        JsonStructure jsonStructure = reader.read();
        if (jsonStructure.getValueType().equals(JsonValue.ValueType.OBJECT)) {
            JsonObject jsonObject = (JsonObject) jsonStructure;

            if (eventIdAsString == null) {
                URI redirectLocation = uriInfo.getAbsolutePathBuilder().path("incoming").path(UUID.randomUUID().toString()).build();
                throw new RedirectionException(Response.Status.TEMPORARY_REDIRECT, redirectLocation);
            }

            if (eventTypeAsString == null) {
                throw new BadRequestException(
                        "Must include an event type with the request either in body or as ES-EventType header.");
            }

            EventId eventId = new EventId(UUID.fromString(eventIdAsString));
            EventType eventType = new EventType(eventTypeAsString);
            EventData eventData = new EventData(eventId, eventType, jsonObject.toString(), null);

            try {
                StreamRevision newRevision = eventStore.append(streamId, eventData, streamRevision);

                UriBuilder path = uriInfo.getAbsolutePathBuilder().path(newRevision.getValue().toString().replaceAll("\\D+", ""));
                return Response.created(path.build()).build();

            } catch (ConcurrencyException e) {
                throw new BadRequestException("Wrong expected EventNumber", e);
            }

        } else if (jsonStructure.getValueType().equals(JsonValue.ValueType.ARRAY)) {

            JsonArray jsonArray = (JsonArray) jsonStructure;
            List<StreamRevision> revisions = new ArrayList<>();

            if (jsonArray.isEmpty()) {
                // request must contain at least one event
                throw new BadRequestException("Write request body invalid.");
            }

            for (int i = 0; i < jsonArray.size(); i++) {

                JsonObject jsonObject = jsonArray.getJsonObject(i);
                try {
                    revisions.add(eventStore.append(streamId, toEventData(jsonObject), streamRevision.add(i)));
                } catch (ConcurrencyException e) {
                    // TODO: which return codeto use in this case?
                    // request must contain at least one event
                    throw new BadRequestException("Wrong expected EventNumber", e);
                }
            }

            // event store returns location to first revision that was added (and not latest as one might think)
            UriBuilder path = uriInfo.getAbsolutePathBuilder().path(revisions.get(0).getValue().toString().replaceAll("\\D+", ""));
            return Response.created(path.build()).build();
        } else {
            throw new BadRequestException("Write request body invalid.");
        }
    }

    private EventData toEventData(JsonObject jsonObject) {

        String eventIdAsString = jsonObject.getString("eventId");
        String eventTypeAsString = jsonObject.getString("eventType");
        String data = jsonObject.getString("data");

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
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{stream-id}/incoming/{event-id}")
    public Response addEventWithIdInPath(@Context UriInfo uriInfo, @HeaderParam("event-id") String eventId,
            @HeaderParam("ES-EventType") String eventType, @PathParam("stream-id") String streamId,
            @HeaderParam("ES-ExpectedVersion") Long expectedVersion, InputStream inputStream) {

        return addEvent(uriInfo, eventId, eventType, streamId, expectedVersion, inputStream);
    }

    @POST
    @Consumes("application/vnd.eventstore.events+json")
    @Path("/{stream-id}")
    public Response addEvents(@Context UriInfo uriInfo, @PathParam("stream-id") String streamId,
            @HeaderParam("ES-ExpectedVersion") Long expectedVersion, InputStream inputStream) {

        return addEvent(uriInfo, null, null, streamId, expectedVersion, inputStream);
    }
}
