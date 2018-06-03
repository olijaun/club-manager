package org.jaun.clubmanager.eventstore;

import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.time.Duration;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.annotation.PreDestroy;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;

import org.jaun.clubmanager.domain.model.commons.Id;
import org.springframework.stereotype.Service;

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
@Service
public class RedisEventStore implements EventStore {

    private final JedisPool jedisPool;
    private final String commitsKey;
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
        commitsKey = keyPrefix + "commits";
        streamKeyPrefix = keyPrefix + "stream:";

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
        RedisEventStore testStore = new RedisEventStore("testStore4");
        StreamId streamId = new StreamId(new DummyId("1"), new Category("newcatoli8"));
        EventData eventData1 = new EventData(EventId.generate(), new EventType("MyEventType1"), "{ \"name\": \"o\" }",
                "{ \"mymeta\": \"mymeta1\" }");
        EventData eventData2 = new EventData(EventId.generate(), new EventType("MyEventType2"), "{ \"name\": \"o\" }",
                "{ \"mymeta\": \"mymeta2\" }");
        EventData eventData3 = new EventData(EventId.generate(), new EventType("MyEventType3"), "{ \"name\": \"o\" }",
                "{ \"mymeta\": \"mymeta3\" }");

        StreamRevision revision = StreamRevision.NEW_STREAM;
        testStore.append(streamId, eventData1, revision);
        testStore.append(streamId, eventData2, (revision = revision.next()));
        testStore.append(streamId, eventData3, (revision = revision.next()));

        List<StoredEventData> storedEvents = testStore.read(streamId);
        //List<StoredEventData> storedEvents = testStore.read(streamId, new StreamRevision(1));
        for (StoredEventData storedEventData : storedEvents) {
            System.out.println("event: " + storedEventData);
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
    public StreamRevision append(StreamId streamId, EventData eventData, StreamRevision expectedVersion)
            throws ConcurrencyException {

        long timestamp = System.currentTimeMillis();

        try (Jedis jedis = jedisPool.getResource()) {

            Object response = jedis.evalsha(tryCommitScriptId, 2,
                    /* KEYS */ commitsKey, keyForStream(streamId),
                    /* ARGV */ String.valueOf(timestamp), streamId.getValue(), eventData.getEventId().getUuid().toString(),
                    eventData.getEventType().getValue(), expectedVersion.getValue().toString(),
                    toRedisEventString(eventData, expectedVersion.next(), timestamp));

            if (response instanceof List) {
                List<String> responseList = ((List<String>) response);
                if (responseList.get(0).equals("commit")) {
                    return expectedVersion.next();
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

    private String toRedisEventString(EventData eventData, StreamRevision streamRevision, long timestamp) {

        JsonObject jsonPayload = Json.createReader(new StringReader(eventData.getPayload())).readObject();

        return Json.createObjectBuilder()
                .add("eventId", eventData.getEventId().getUuid().toString())
                .add("eventType", eventData.getEventType().getValue())
                .add("timestamp", String.valueOf(timestamp))
                .add("streamRevision", streamRevision.getValue())
                .add("metadata", eventData.getMetadata().orElse(null))
                .add("data", jsonPayload)
                .build()
                .toString();
    }

    @Override
    public List<StoredEventData> read(StreamId streamId) {
        return read(streamId, StreamRevision.INITIAL, StreamRevision.MAXIMUM);
    }

    @Override
    public List<StoredEventData> read(StreamId streamId, StreamRevision versionGreaterThan) {
        return read(streamId, versionGreaterThan, StreamRevision.MAXIMUM);
    }

    private List<StoredEventData> read(StreamId streamId, StreamRevision fromRevision, StreamRevision toRevision) {

        try (Jedis jedis = jedisPool.getResource()) {

            List<String> commitIds = jedis.lrange(keyForStream(streamId), fromRevision.getValue(), toRevision.getValue());

            return jedis.hmget(commitsKey, commitIds.toArray(new String[commitIds.size()]))
                    .stream()
                    .map(this::toEventData)
                    .collect(Collectors.toList());

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

        return new StoredEventData(eventId, eventType, payload.toString(), metadata, streamRevision);
    }

    private String keyForStream(StreamId streamId) {
        return streamKeyPrefix + streamId.getValue();
    }


    @PreDestroy
    public void close() {
        jedisPool.close();
    }
}
