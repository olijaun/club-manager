package org.jaun.clubmanager.evenstore;

import java.util.List;

// https://tech.zilverline.com/2012/07/30/simple-event-sourcing-redis-event-store-part-3
// https://medium.com/lcom-techblog/scalable-microservices-with-event-sourcing-and-redis-6aa245574db0
public class RedisEvenStore implements EventStore {

    public static void main(String[] args) {

//        JedisPool jedisPool = new JedisPool(config, host, port);
//
//        Jedis jedis = new Jedis("localhost");
//        jedis.set("name", "oliver");
//        String value = jedis.get("foo");
//        System.out.println(value);
    }

    @Override
    public void append(StreamId streamId, List<EventData> eventList, Integer expectedVersion)
            throws ConcurrencyException {

    }

    @Override
    public List<EventData> read(StreamId streamId) {
        return null;
    }

    @Override
    public List<EventData> read(StreamId streamId, Integer versionGreaterThan) {
        return null;
    }
}
