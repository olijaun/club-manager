package org.jaun.clubmanager.eventstore.akka;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.UUID;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;

import org.jaun.clubmanager.domain.model.commons.EventId;
import org.jaun.clubmanager.domain.model.commons.Id;
import org.jaun.clubmanager.eventstore.Category;
import org.jaun.clubmanager.eventstore.EventType;
import org.jaun.clubmanager.eventstore.StreamId;

import akka.serialization.JSerializer;

public class JsonSerializer extends JSerializer {

    // If you need logging here, introduce a constructor that takes an ExtendedActorSystem.
    // public MyOwnSerializer(ExtendedActorSystem actorSystem)
    // Get a logger using:
    // private final LoggingAdapter logger = Logging.getLogger(actorSystem, this);

    // This is whether "fromBinary" requires a "clazz" or not
    @Override
    public boolean includeManifest() {
        return false;
    }

    // Pick a unique identifier for your Serializer,
    // you've got a couple of billions to choose from,
    // 0 - 40 is reserved by Akka itself
    @Override
    public int identifier() {
        return 31415;
    }

    // "toBinary" serializes the given object to an Array of Bytes
    @Override
    public byte[] toBinary(Object obj) {
        // Put the code that serializes the object here

        if (!(obj instanceof EventDataWithStreamId)) {
            throw new IllegalArgumentException("unsupported obj for serialization: " + obj);
        }

        EventDataWithStreamId ev = (EventDataWithStreamId) obj;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        //try (JsonWriter jsonWriter = gson.newJsonWriter(new OutputStreamWriter(bos))) {

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

    // "fromBinary" deserializes the given array,
    // using the type hint (if any, see "includeManifest" above)
    @Override
    public Object fromBinaryJava(byte[] bytes, Class<?> clazz) {
        JsonReader jsonReader = Json.createReader(new InputStreamReader(new ByteArrayInputStream(bytes)));

        JsonObject jsonObject = jsonReader.readObject();

        String eventId = jsonObject.getString("eventId");
        String streamId = jsonObject.getString("streamId");
        String eventType = jsonObject.getString("eventType");
        Optional<JsonObject> metadataJson = Optional.ofNullable(jsonObject.getJsonObject("metadata"));
        JsonObject data = jsonObject.getJsonObject("data");

        return new EventDataWithStreamId(StreamId.parse(streamId), EventId.parse(eventId), new EventType(eventType),
                data.toString(), metadataJson.map(JsonObject::toString).orElseGet(null));
    }

    public static void main(String[] args) {

        Id id = new Id("abc") {
            @Override
            public String getValue() {
                return super.getValue();
            }
        };

//        EventDataWithStreamId e =
//                new EventDataWithStreamId(new StreamId(id, new Category("streamA£")), new EventId(UUID.randomUUID()),
//                        new EventType("type"), "{ \"hello\": \"wo£rld\" }", "{ \"meta\": \"data\" }");
        EventDataWithStreamId e =
                new EventDataWithStreamId(new StreamId(id, new Category("streamA£")), new EventId(UUID.randomUUID()),
                        new EventType("bla"), "{ \"hello\": \"wo£rld\" }", null);
        JsonSerializer gsonSerializer = new JsonSerializer();

        byte[] bytes = gsonSerializer.toBinary(e);

        System.out.println("serialized event: " + new String(gsonSerializer.toBinary(gsonSerializer.fromBinaryJava(bytes, null)),
                StandardCharsets.UTF_8));
    }
}
