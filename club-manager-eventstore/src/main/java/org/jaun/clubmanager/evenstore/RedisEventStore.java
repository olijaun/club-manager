package org.jaun.clubmanager.evenstore;

import static java.util.Objects.requireNonNull;

import java.time.Duration;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import javax.annotation.PreDestroy;

import org.jaun.clubmanager.domain.model.commons.Id;

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

    private final String TRY_COMMIT_SCRIPT = //
            "local commitsKey = KEYS[1]\n" //
            + "local streamKey = KEYS[2]\n" //
            + "local timestamp = tonumber(ARGV[1])\n" //
            + "local streamId = ARGV[2]\n" //
            + "local expected = tonumber(ARGV[3])\n" //
            + "local events = ARGV[4]\n" //
            + "\n" //
            + "local actual = tonumber(redis.call('llen', streamKey))\n" //
            + "if actual ~= expected then \n" //
            + "return {'conflict', tostring(actual)}\n" //
            + "end\n" //
            + "\n" //
            + "local storeRevision = tonumber(redis.call('hlen', commitsKey))\n" //
            + "local commitId = storeRevision + 1 \n" //
            + "local commitData = string.format('{\"storeRevision\":%d,\"timestamp\":%d,\"streamId\":%s,\"streamRevision\":%d,\"events\":%s}',"
            + "commitId, timestamp, cjson.encode(streamId), actual + 1, events)\n" //
            + "\n" //
            + "redis.call('hset', commitsKey, commitId, commitData)\n" //
            + "redis.call('rpush', streamKey, commitId)\n" //
            + "redis.call('publish', commitsKey, commitData)\n" //
            + "\n" //
            + "return {'commit', tostring(commitId)}\n";

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
        StreamId streamId = new StreamId(new DummyId("1"), new Category("gugus"));
//        EventData eventData = new EventData(EventId.generate(), "payload2", "metadatabla", 0);
//        testStore.append(streamId, eventData, StreamRevision.INITIAL.next());
        List<EventData> read = testStore.read(streamId);
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
                    /* ARGV */ String.valueOf(timestamp), streamId.getValue(), expectedVersion.getValue().toString(),
                    eventData.getPayload());

            if (response instanceof List) {
                System.out.println(((List) response).get(0));
            }
        }
    }

    @Override
    public List<EventData> read(StreamId streamId) {
        return read(streamId, StreamRevision.INITIAL, StreamRevision.MAXIMUM);
    }

    private List<EventData> read(StreamId streamId, StreamRevision fromRevision, StreamRevision toRevision) {

        try (Jedis jedis = jedisPool.getResource()) {

            List<String> commitIds = jedis.lrange(keyForStream(streamId), fromRevision.getValue(), toRevision.getValue());

            AtomicLong i = new AtomicLong(fromRevision.getValue());
            List<EventData> eventDataAsString = jedis.hmget(commitsKey, commitIds.toArray(new String[commitIds.size()]))
                    .stream()
                    .map(s -> new EventData(new EventId(UUID.randomUUID()), s, "meta", i.getAndIncrement()))
                    .collect(Collectors.toList());

            return eventDataAsString;
        }
    }

    private String keyForStream(StreamId streamId) {
        return streamKeyPrefix + streamId.getValue();
    }

    @Override
    public List<EventData> read(StreamId streamId, Integer versionGreaterThan) {
        return null;
    }

    @PreDestroy
    public void close() {
        jedisPool.close();
    }
}
