package org.jaun.clubmanager.member.infra.projection;

import com.github.msemys.esjc.EventStore;
import com.github.msemys.esjc.EventStoreBuilder;
import com.hazelcast.config.Config;
import com.hazelcast.config.JoinConfig;
import com.hazelcast.config.NetworkConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.query.Predicate;
import com.hazelcast.query.Predicates;
import com.hazelcast.query.SqlPredicate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.Collection;

@Service
public class HazelcastMemberProjection {

    private IMap<String, User> users;

    public HazelcastMemberProjection() {
        Config config = new Config();

        NetworkConfig network = config.getNetworkConfig();
        //network.setPort(PORT_NUMBER);

        JoinConfig join = network.getJoin();
        join.getTcpIpConfig().setEnabled(false);
        join.getAwsConfig().setEnabled(false);
        join.getMulticastConfig().setEnabled(false);

//        config.setProperty("hazelcast.local.localAddress", "127.0.0.1");
//        config.setProperty("hazelcast.initial.min.cluster.size","1");
        HazelcastInstance h = Hazelcast.newHazelcastInstance(config);
        users = h.getMap("users");

        // Just generate some random users
        User u1 = new User("userA", 39, true);
        User u2 = new User("userB", 46, true);

        users.put(u1.getUsername(), u1);
        users.put(u2.getUsername(), u2);
    }

    public void update() {
        EventStore eventStore = EventStoreBuilder.newBuilder()
                .singleNodeAddress("127.0.0.1", 1113)
                .userCredentials("admin", "changeit")
                .build();

        //eventStore.subscribeToAll()
    }

    public void query() {
        Predicate sqlQuery = new SqlPredicate("active AND age BETWEEN 18 AND 21)");

        Predicate criteriaQuery = Predicates.and(
                Predicates.equal("active", true),
                Predicates.between("age", 30, 50)
        );

        Collection<User> result1 = users.values(sqlQuery);
        Collection<User> result2 = users.values(criteriaQuery);

        System.out.println(result1);
        System.out.println(result2);
    }

    public static class User implements Serializable {
        private final String username;
        private final int age;
        private final boolean active;

        public User(String username, int age, boolean active) {
            this.username = username;
            this.age = age;
            this.active = active;
        }

        public String getUsername() {
            return username;
        }

        public int getAge() {
            return age;
        }

        public boolean isActive() {
            return active;
        }

        @Override
        public String toString() {
            return "User{" +
                    "username='" + username + '\'' +
                    ", age=" + age +
                    ", active=" + active +
                    '}';
        }
    }
}
