package org.jaun.clubmanager.evenstore;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

// https://tech.zilverline.com/2012/07/30/simple-event-sourcing-redis-event-store-part-3
// https://medium.com/lcom-techblog/scalable-microservices-with-event-sourcing-and-redis-6aa245574db0
public class RedisEvenStore {

    public static void main(String[] args) {


        JedisPool jedisPool = new JedisPool(config, host, port);

        Jedis jedis = new Jedis("localhost");
        jedis.set("name", "oliver");
        String value = jedis.get("foo");
        System.out.println(value);
    }
}
