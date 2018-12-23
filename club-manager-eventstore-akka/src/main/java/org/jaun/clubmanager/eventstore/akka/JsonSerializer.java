package org.jaun.clubmanager.eventstore.akka;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.UUID;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;

import akka.serialization.SerializerWithStringManifest;
import org.jaun.clubmanager.domain.model.commons.EventId;
import org.jaun.clubmanager.domain.model.commons.Id;
import org.jaun.clubmanager.eventstore.Category;
import org.jaun.clubmanager.eventstore.EventType;
import org.jaun.clubmanager.eventstore.StreamId;

import akka.serialization.JSerializer;

public class JsonSerializer extends SerializerWithStringManifest {

    public String manifest(Object obj) {
        if (!(obj instanceof EventDataWithStreamId)) {
            throw new IllegalArgumentException("unsupported obj for serialization: " + obj);
        }
        EventDataWithStreamId ev = (EventDataWithStreamId) obj;
        return ev.getEventType().getValue();
    }

    @Override
    public int identifier() {
        return 31415;
    }

    @Override
    public byte[] toBinary(Object obj) {

        if (!(obj instanceof EventDataWithStreamId)) {
            throw new IllegalArgumentException("unsupported obj for serialization: " + obj);
        }

        EventDataWithStreamId ev = (EventDataWithStreamId) obj;

        JsonObjectBuilder jsonObjectBuilder = Json.createObjectBuilder();
        jsonObjectBuilder.add("eventId", ev.getEventId().getUuid().toString());
        jsonObjectBuilder.add("eventType", ev.getEventType().getValue());
        jsonObjectBuilder.add("streamId", ev.getStreamId().getValue());
        if (ev.getMetadata().isPresent()) {
            JsonObject metadataJson = Json.createReader(new StringReader(ev.getMetadata().get())).readObject();
            jsonObjectBuilder.add("metadata", metadataJson);
        }
        JsonObject jsonPayload = Json.createReader(new StringReader(ev.getPayload())).readObject();
        jsonObjectBuilder.add("data", jsonPayload);

        return jsonObjectBuilder.build().toString().getBytes(StandardCharsets.UTF_8);

    }

    @Override
    public Object fromBinary(byte[] bytes, String var2) throws NotSerializableException {

        JsonReader jsonReader = Json.createReader(new InputStreamReader(new ByteArrayInputStream(bytes)));

        JsonObject jsonObject = jsonReader.readObject();

        String eventId = jsonObject.getString("eventId");
        String streamId = jsonObject.getString("streamId");
        String eventType = jsonObject.getString("eventType");
        String metadataJson = Optional.ofNullable(jsonObject.getJsonObject("metadata")).map(JsonObject::toString).orElse(null);
        String data = jsonObject.getJsonObject("data").toString();

        return new EventDataWithStreamId(StreamId.parse(streamId), EventId.parse(eventId), new EventType(eventType),
                data, metadataJson);
    }

    public static void main(String[] args) throws Exception {

        Id id = new Id("abc") {
            @Override
            public String getValue() {
                return super.getValue();
            }
        };

        EventDataWithStreamId e =
                new EventDataWithStreamId(new StreamId(id, new Category("streamA£")), new EventId(UUID.randomUUID()),
                        new EventType("bla"), "{ \"hello\": \"wo£rld\" }", null);
        JsonSerializer gsonSerializer = new JsonSerializer();

        byte[] bytes = gsonSerializer.toBinary(e);

        System.out.println("serialized event: " + new String(gsonSerializer.toBinary(gsonSerializer.fromBinary(bytes, "")),
                StandardCharsets.UTF_8));
    }
}
