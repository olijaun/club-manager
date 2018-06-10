package org.jaun.clubmanager.eventstore.redis;

import static java.util.Collections.singletonList;
import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.PreDestroy;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;

import org.jaun.clubmanager.domain.model.commons.Id;
import org.jaun.clubmanager.eventstore.Category;
import org.jaun.clubmanager.eventstore.ConcurrencyException;
import org.jaun.clubmanager.eventstore.EventData;
import org.jaun.clubmanager.eventstore.EventId;
import org.jaun.clubmanager.eventstore.EventStore;
import org.jaun.clubmanager.eventstore.EventType;
import org.jaun.clubmanager.eventstore.StoredEventData;
import org.jaun.clubmanager.eventstore.StoredEvents;
import org.jaun.clubmanager.eventstore.StreamId;
import org.jaun.clubmanager.eventstore.StreamRevision;

import com.google.common.base.Charsets;
import com.google.common.io.ByteSource;
import com.google.gson.Gson;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * This is based on ideas from https://tech.zilverline.com/2012/07/30/simple-event-sourcing-redis-event-store-part-3 and
 * https://medium.com/lcom-techblog/scalable-microservices-with-event-sourcing-and-redis-6aa245574db0
 */
public class RedisEventStore implements EventStore {

    private final JedisPool jedisPool;
    private final String eventsKey;
    private final String allKey;
    private final String keyPrefix;
    private final String streamKeyPrefix;
    private final String tryCommitScriptId;

    private final Gson gson = new Gson();

    private static final String TRY_COMMIT_SCRIPT;

    static {
        InputStream inputStream = RedisEventStore.class.getResourceAsStream("/writeEvent.lua");
        ByteSource byteSource = new ByteSource() {
            @Override
            public InputStream openStream() throws IOException {
                return inputStream;
            }
        };

        try {
            String text = byteSource.asCharSource(Charsets.UTF_8).read();
            TRY_COMMIT_SCRIPT = text;
        } catch (IOException e) {
            throw new IllegalStateException("failed to read stream", e);
        }
    }

    public RedisEventStore(String name) {
        requireNonNull(name);
        keyPrefix = name + ":";
        eventsKey = keyPrefix + "events";
        streamKeyPrefix = keyPrefix + "stream:";
        allKey = streamKeyPrefix + "$all";

        jedisPool = new JedisPool(buildPoolConfig(), "localhost", 6379);

        try (Jedis jedis = jedisPool.getResource()) {
            tryCommitScriptId = jedis.scriptLoad(TRY_COMMIT_SCRIPT);
        }

    }

    public static class DummyId extends Id {
        protected DummyId(String value) {
            super(value);
        }
    }

    public static void main(String[] args) throws ConcurrencyException {
        System.out.println(TRY_COMMIT_SCRIPT);
        RedisEventStore testStore = new RedisEventStore("teststore");
        StreamId streamId = new StreamId(new DummyId("1"), new Category("cat1"));
        EventData eventData1 = new EventData(EventId.generate(), new EventType("MyEventType1"), "{ \"name\": \"o\" }",
                "{ \"mymeta\": \"mymeta1\" }");
        EventData eventData2 = new EventData(EventId.generate(), new EventType("MyEventType2"), "{ \"name\": \"o\" }",
                "{ \"mymeta\": \"mymeta2\" }");
        EventData eventData3 = new EventData(EventId.generate(), new EventType("MyEventType3"), "{ \"name\": \"o\" }",
                "{ \"mymeta\": \"mymeta3\" }");

        StreamRevision returnedRevision1 = testStore.append(streamId, singletonList(eventData1), StreamRevision.UNSPECIFIED);
        StreamRevision returnedRevision2 = testStore.append(streamId, singletonList(eventData2), returnedRevision1);
        StreamRevision returnedRevision3 = testStore.append(streamId, singletonList(eventData3), returnedRevision2);

        StoredEvents storedEvents = testStore.read(streamId);
        //List<StoredEventData> storedEvents = testStore.read(streamId, new StreamRevision(1));
        for (StoredEventData storedEventData : storedEvents) {
            System.out.println("event: " + storedEventData);
        }
    }

    public static void varargs(Object... objects) {
        for (Object o : objects) {
            System.out.println(o);
        }
    }

    private JedisPoolConfig buildPoolConfig() {
        final JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(128);
        poolConfig.setMaxIdle(128);
        poolConfig.setMinIdle(16);
        poolConfig.setTestOnBorrow(true);
        poolConfig.setTestOnReturn(true);
        poolConfig.setTestWhileIdle(true);
        poolConfig.setMinEvictableIdleTimeMillis(Duration.ofSeconds(60).toMillis());
        poolConfig.setTimeBetweenEvictionRunsMillis(Duration.ofSeconds(30).toMillis());
        poolConfig.setNumTestsPerEvictionRun(3);
        poolConfig.setBlockWhenExhausted(true);
        return poolConfig;
    }

    @Override
    public StreamRevision append(StreamId streamId, List<EventData> eventDataList, StreamRevision expectedVersion)
            throws ConcurrencyException {

        long timestamp = System.currentTimeMillis();

        UUID commitId = eventDataList.size() == 1 ? eventDataList.get(0).getEventId().getUuid() : UUID.randomUUID();

        try (Jedis jedis = jedisPool.getResource()) {

            if (expectedVersion.equals(StreamRevision.UNSPECIFIED)) {
                // TODO: should be handled on redis side. but hey it works normally.
                expectedVersion = StreamRevision.from(jedis.llen(keyForStream(streamId)) - 1L);
            }

            String categoryStreamKey =
                    streamId.getCategory().isPresent() ? streamKeyPrefix + "$ce-" + streamId.getCategory().get().getName() : "";

            Stream<String> keyStream = Stream.of(
                    /* KEYS */ eventsKey, keyForStream(streamId), allKey, categoryStreamKey);

            Stream<String> constantValuesStream = Stream.of( //
                    streamId.getValue(), // streamId
                    expectedVersion.getValue().toString(),  // expectedVersion
                    String.valueOf(eventDataList.size())); // numberOfEvents

            AtomicLong revisionOfNewEvent;
            if (expectedVersion.equals(StreamRevision.NEW_STREAM)) {
                revisionOfNewEvent = new AtomicLong(0);
            } else {
                revisionOfNewEvent = new AtomicLong(expectedVersion.add(1).getValue());
            }

            Stream<String> serializedEventDataStream = eventDataList.stream()
                    .map(eventData -> toRedisEventString(streamId, eventData,
                            StreamRevision.from(revisionOfNewEvent.getAndIncrement()), commitId, timestamp));

            String[] varargs =
                    Stream.concat(Stream.concat(keyStream, constantValuesStream), serializedEventDataStream).toArray(String[]::new);

            Object response = jedis.evalsha(tryCommitScriptId, 4, varargs);

            if (response instanceof List) {
                List<Object> responseList = (List) response;
                System.out.println("lua response: " + responseList);
                if (responseList.get(0).equals("commit")) {
                    return StreamRevision.from(Long.parseLong(responseList.get(1).toString()));
                } else if (responseList.get(0).equals("streamExistsAlready")) {
                    throw new ConcurrencyException("stream exists already");
                } else if (responseList.get(0).equals("conflict")) {
                    throw new ConcurrencyException("version conflict: " + responseList);
                } else {
                    throw new IllegalStateException("unknown response from lua script: " + responseList);
                }
            }
            throw new IllegalStateException("unexpected response from lua script: " + response);
        }
    }

    private String toRedisEventString(StreamId streamId, EventData eventData, StreamRevision streamRevision, UUID commitId,
            long timestamp) {

        requireNonNull(eventData);
        requireNonNull(streamRevision);
        requireNonNull(commitId);
        requireNonNull(timestamp);

        JsonObject jsonPayload = Json.createReader(new StringReader(eventData.getPayload())).readObject();

        JsonObjectBuilder jsonObjectBuilder = Json.createObjectBuilder()
                .add("eventId", eventData.getEventId().getUuid().toString())
                .add("streamId", streamId.getValue())
                .add("commitId", commitId.toString())
                .add("eventType", eventData.getEventType().getValue())
                .add("timestamp", String.valueOf(timestamp))
                .add("streamRevision", streamRevision.getValue())
                .add("data", jsonPayload);
        if (eventData.getMetadata().isPresent()) {
            jsonObjectBuilder.add("metadata", eventData.getMetadata().get());
        } else {
            jsonObjectBuilder.addNull("metadata");
        }

        return jsonObjectBuilder.build().toString();
    }

    @Override
    public StoredEvents read(StreamId streamId) {
        return read(streamId, StreamRevision.INITIAL, StreamRevision.MAXIMUM);
    }

    @Override
    public StoredEvents read(StreamId streamId, StreamRevision versionGreaterThan) {
        return read(streamId, versionGreaterThan, StreamRevision.MAXIMUM);
    }

    @Override
    public StoredEvents read(StreamId streamId, StreamRevision fromRevision, StreamRevision toRevision) {

        try (Jedis jedis = jedisPool.getResource()) {

            List<String> commitIds = jedis.lrange(keyForStream(streamId), fromRevision.getValue(), toRevision.getValue());

            if (commitIds.isEmpty()) {
                return new StoredEvents(Collections.emptyList());
            }

            List<StoredEventData> eventDataList = jedis.hmget(eventsKey, commitIds.toArray(new String[commitIds.size()]))
                    .stream()
                    .map(this::toEventData)
                    .collect(Collectors.toList());

            return new StoredEvents(eventDataList);
        }
    }

    @Override
    public long length(StreamId streamId) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.llen(keyForStream(streamId));
        }
    }

    private StoredEventData toEventData(String jsonString) {
        System.out.println("json string: " + jsonString);
        JsonReader reader = Json.createReader(new StringReader(jsonString));

        JsonObject jsonObject = reader.readObject();
        EventId eventId = new EventId(UUID.fromString(jsonObject.getString("eventId")));
        EventType eventType = new EventType(jsonObject.getString("eventType"));
        JsonObject payload = jsonObject.getJsonObject("data");
        String metadata = jsonObject.getString("metadata", null);
        StreamRevision streamRevision =
                StreamRevision.from((long) jsonObject.getInt("streamRevision")); // TODO: use string in order to store long value?

        long timestamp = Long.valueOf(jsonObject.getString("timestamp"));
        StreamId streamId = StreamId.parse(jsonObject.getString("streamId"));

        return new StoredEventData(streamId, eventId, eventType, payload.toString(), metadata, streamRevision,
                Instant.ofEpochMilli(timestamp));
    }

    private String keyForStream(StreamId streamId) {
        return streamKeyPrefix + streamId.getValue();
    }


    @PreDestroy
    public void close() {
        jedisPool.close();
    }
}
