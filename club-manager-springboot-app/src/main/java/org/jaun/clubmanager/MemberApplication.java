package org.jaun.clubmanager;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

import com.hazelcast.config.Config;
import com.hazelcast.config.JoinConfig;
import com.hazelcast.config.NetworkConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;


@SpringBootApplication(scanBasePackages = {"org.jaun.clubmanager"})
public class MemberApplication {

    public static void main(String[] args) {
        SpringApplication.run(MemberApplication.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner(ApplicationContext ctx) {

//        CatchUpSubscription membershipProjection = ctx.getBean(HazelcastMemberProjection.class);
//        membershipProjection.start();
//
//        String database_url = System.getenv("DATABASE_URL");
//        System.out.println("------------------------------------" + database_url);
//
//        CatchUpSubscription personProjection = ctx.getBean(HazelcastPersonProjection.class);
//        personProjection.start();

        //RedisEventStore redisEventStore = ctx.getBean(RedisEventStore.class);

        return args -> {

//            System.out.println("Let's inspect the beans provided by Spring Boot:");
//
//            String[] beanNames = ctx.getBeanDefinitionNames();
//            Arrays.sort(beanNames);
//            for (String beanName : beanNames) {
//                System.out.println(beanName);
//            }
        };
    }

    @Bean
    public HazelcastInstance hazelcastInstance() {
        Config config = new Config();

        NetworkConfig network = config.getNetworkConfig();
        //network.setPort(PORT_NUMBER);

        JoinConfig join = network.getJoin();
        join.getTcpIpConfig().setEnabled(false);
        join.getAwsConfig().setEnabled(false);
        join.getMulticastConfig().setEnabled(false);

        return Hazelcast.newHazelcastInstance(config);
    }

//    @Bean
//    public EventStore eventStore() {
//        return EventStoreBuilder.newBuilder().singleNodeAddress("127.0.0.1", 1113).userCredentials("admin", "changeit").build();
//    }

//    @Bean
//    public EventStoreClient myEventStoreClient(AccessTokenManager accessTokenManager) {
//        Client client = ClientBuilder.newClient().register(new BearerTokenFilter(accessTokenManager));
//        return new JaxRsRestEventStoreClient(client, "http://localhost:8080");
//    }

//    @Bean
//    public EventStoreClient myEventStoreClient(ActorSystem actorSystem) {
//        return new AkkaEventStore(actorSystem);
//    }
//
//    @Bean
//    public ActorSystem actorSystem() {
//        return ActorSystem.create("eventStore");
//    }
//
//    @Bean
//    public ActorMaterializer actorMaterializer(ActorSystem actorSystem) {
//        return ActorMaterializer.create(actorSystem);
//    }

//    @Bean
//    public EventStore myEventStore() {
//        return new RedisEventStore("club-manager-event-store");
//    }

    @Bean
    public Client jaxRsClient() {
        return ClientBuilder.newClient();
    }

//    @Bean
//    public WebTarget clubManagerPersonServiceTarget(AccessTokenManager accessTokenManager) {
//        Client client = ClientBuilder.newClient().register(new BearerTokenFilter(accessTokenManager));
//        return client.target("http://localhost:8080/persons");
//    }
}
