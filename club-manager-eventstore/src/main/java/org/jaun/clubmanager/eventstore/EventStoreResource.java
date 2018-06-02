package org.jaun.clubmanager.eventstore;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.ws.rs.Consumes;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.springframework.stereotype.Component;

@Component
@Path("/streams")
public class EventStoreResource {

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/")
    public Response addEvent(@HeaderParam("ES-EventId") String eventId, @HeaderParam("ES-EventType") String eventType,
            InputStream singleEvenInputStream) {

        JsonReader reader = Json.createReader(new InputStreamReader(singleEvenInputStream));

        JsonObject jsonObject = reader.readObject();

        return Response.ok(jsonObject.toString(), MediaType.APPLICATION_JSON).build();
    }

    @POST
    @Consumes("application/vnd.eventstore.events")
    @Path("/")
    public Response addEvents(InputStream multiEventInputStream) {

        JsonReader reader = Json.createReader(new InputStreamReader(multiEventInputStream));

        JsonArray jsonArray = reader.readArray();
        List<JsonObject> events = new ArrayList<>();
        for(int i = 0; i < jsonArray.size(); i++) {
            events.add(jsonArray.getJsonObject(i));
        }

        return Response.ok(events.get(0).getJsonObject("data").toString(), MediaType.APPLICATION_JSON).build();
    }
}
