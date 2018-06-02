package org.jaun.clubmanager.evenstore;

import static java.util.Objects.requireNonNull;

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

import com.google.gson.Gson;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

// https://tech.zilverline.com/2012/07/30/simple-event-sourcing-redis-event-store-part-3
// https://medium.com/lcom-techblog/scalable-microservices-with-event-sourcing-and-redis-6aa245574db0
public class RedisEventStore implements EventStore {

    private final JedisPool jedisPool;
    private final String name;
    private final String commitsKey;
    private final String keyPrefix;
    private final String streamKeyPrefix;
    private final String tryCommitScriptId;

    private final Gson gson = new Gson();

    private static final String TRY_COMMIT_SCRIPT = //
            "local commitsKey = KEYS[1]\n" //
            + "local streamKey = KEYS[2]\n" //
            + "local timestamp = tonumber(ARGV[1])\n" //
            + "local streamId = ARGV[2]\n" //
            + "local eventId = ARGV[3]\n" //
            + "local eventType = ARGV[4]\n" //
            + "local expected = tonumber(ARGV[5])\n" //
            + "local event = ARGV[6]\n" //
            + "local metadata = ARGV[7]\n" //
            + "\n" //
            + "local actual = tonumber(redis.call('llen', streamKey))\n" //
            + "if actual ~= expected then \n" //
            + "return {'conflict', tostring(actual)}\n" //
            + "end\n" //
            + "\n" //
            + "local storeRevision = tonumber(redis.call('hlen', commitsKey))\n" //
            //+ "local commitId = storeRevision + 1 \n" //
            + "local commitData = string.format('{\"eventId\":%s,\"timestamp\":%d,\"streamId\":%s,\"eventType\":%s,\"streamRevision\":%d,\"event\":%s,\"metadata\":%s}',"
            + "cjson.encode(eventId), timestamp, cjson.encode(streamId), cjson.encode(eventType), actual + 1, cjson.encode(event), cjson.encode(metadata))\n"
            //
            + "\n" //
            + "redis.call('hset', commitsKey, eventId, commitData)\n" //
            + "redis.call('rpush', streamKey, eventId)\n" //
            + "redis.call('publish', commitsKey, commitData)\n" //
            + "\n" //
            + "return {'commit', tostring(eventId)}\n";

    public RedisEventStore(String name) {
        System.out.println(TRY_COMMIT_SCRIPT);
        this.name = requireNonNull(name);
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
        RedisEventStore testStore = new RedisEventStore("testStore");
        StreamId streamId = new StreamId(new DummyId("1"), new Category("mycat7"));
        EventData eventData1 = new EventData(EventId.generate(), new EventType("MyEventType1"), "my payload1", "my metadata1");
        EventData eventData2 = new EventData(EventId.generate(), new EventType("MyEventType2"), "my payload2", "my metadata2");
        EventData eventData3 = new EventData(EventId.generate(), new EventType("MyEventType3"), "{ \"name\": \"oliver\" }", "");

//        StreamRevision revision = StreamRevision.INITIAL;
//        testStore.append(streamId, eventData1, revision);
//        testStore.append(streamId, eventData2, (revision = revision.next()));
//        testStore.append(streamId, eventData3, (revision = revision.next()));

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
    public void append(StreamId streamId, EventData eventData, StreamRevision expectedVersion) throws ConcurrencyException {

        long timestamp = System.currentTimeMillis();

        try (Jedis jedis = jedisPool.getResource()) {

            Object response = jedis.evalsha(tryCommitScriptId, 2,
                    /* KEYS */ commitsKey, keyForStream(streamId),
                    /* ARGV */ String.valueOf(timestamp), streamId.getValue(), eventData.getEventId().getUuid().toString(),
                    eventData.getEventType().getValue(), expectedVersion.getValue().toString(), eventData.getPayload(),
                    eventData.getMetadata().orElse(""));

            if (response instanceof List) {
                System.out.println(((List) response).get(0));
            }
        }
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
        System.out.println(jsonString);
        JsonReader reader = Json.createReader(new StringReader(jsonString));

        JsonObject jsonObject = reader.readObject();
        EventType eventType = new EventType(jsonObject.getString("eventType"));
        EventId eventId = new EventId(UUID.fromString(jsonObject.getString("eventId")));
        String payload = jsonObject.getString("event");
        String metadata = jsonObject.getString("metadata", null);
        long streamRevision = (long) jsonObject.getInt("streamRevision"); // TODO: use string in order to store long value?

        return new StoredEventData(eventId, eventType, payload, metadata, streamRevision);
    }

    private String keyForStream(StreamId streamId) {
        return streamKeyPrefix + streamId.getValue();
    }


    @PreDestroy
    public void close() {
        jedisPool.close();
    }
}
