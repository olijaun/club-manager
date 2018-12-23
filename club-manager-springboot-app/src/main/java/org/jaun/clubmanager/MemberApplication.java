package org.jaun.clubmanager;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;

import akka.persistence.jdbc.query.javadsl.JdbcReadJournal;
import akka.persistence.query.PersistenceQuery;
import akka.persistence.query.javadsl.EventsByTagQuery;
import org.jaun.clubmanager.eventstore.CatchUpSubscription;
import org.jaun.clubmanager.eventstore.EventStoreClient;
import org.jaun.clubmanager.eventstore.akka.AkkaEventStore;
import org.jaun.clubmanager.member.infra.projection.HazelcastMemberProjection;
import org.jaun.clubmanager.oauth.AccessTokenManager;
import org.jaun.clubmanager.oauth.BearerTokenFilter;
import org.jaun.clubmanager.person.infra.projection.HazelcastPersonProjection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.context.event.ContextStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import com.hazelcast.config.Config;
import com.hazelcast.config.JoinConfig;
import com.hazelcast.config.NetworkConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;

import akka.actor.ActorSystem;
import akka.stream.ActorMaterializer;

@SpringBootApplication(scanBasePackages = {"org.jaun.clubmanager"})
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, jsr250Enabled = true)
//@EnableAutoConfiguration
public class MemberApplication {

    @Value("${machine2machine.url}")
    private String url;
    @Value("${machine2machine.clientId}")
    private String clientId;
    @Value("${machine2machine.clientSecret}")
    private String clientSecret;
    @Value("${machine2machine.audience}")
    private String audience;
    @Value("${machine2machine.grantType}")
    private String grantType;
    @Value("${machine2machine.scope}")
    private String scope;

    @Value("${webServices.personService.url}")
    private String personServiceUrl;

    @Value("${akkaEventStore.actorSystemName}")
    private String actorSystemName;


    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(MemberApplication.class, args);

        context.getBean(HazelcastMemberProjection.class).start();
        context.getBean(HazelcastPersonProjection.class).start();
    }

    @EventListener
    public void handleContextRefreshEvent(ContextStartedEvent ctxStartEvt) {
        System.out.println("Context Start Event received.");
    }

    // https://www.leveluplunch.com/java/tutorials/011-add-servlet-mapping-to-dispatcherservlet-spring-boot/
//    @Bean
//    public DispatcherServlet dispatcherServlet() {
//        return new DispatcherServlet();
//    }
//
//    @Bean
//    public DispatcherServletPath dispatcherServletPath() {
//        return new DispatcherServletPath() {
//            @Override
//            public String getPath() {
//                return "/bla";
//            }
//        };
//    }
//
//    @Bean
//    public ServletRegistrationBean dispatcherServlet(@Autowired WebApplicationContext context) {
//
//        ServletRegistrationBean registration = new ServletRegistrationBean(new DispatcherServlet(context), "/bla/*");
//        registration.setLoadOnStartup(0);
//        registration.setName(
//                DispatcherServletAutoConfiguration.DEFAULT_DISPATCHER_SERVLET_REGISTRATION_BEAN_NAME);
//        return registration;
//    }

    @Bean
    public CommandLineRunner commandLineRunner(ApplicationContext ctx) {

        CatchUpSubscription membershipProjection = ctx.getBean(HazelcastMemberProjection.class);

        String database_url = System.getenv("DATABASE_URL");
        System.out.println("------------------------------------" + database_url);

        CatchUpSubscription personProjection = ctx.getBean(HazelcastPersonProjection.class);

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

    @Bean
    public EventsByTagQuery eventsByTagQuery(ActorSystem actorSystem) {
        JdbcReadJournal readJournal =
                PersistenceQuery.get(actorSystem).getReadJournalFor(JdbcReadJournal.class, JdbcReadJournal.Identifier());
        return readJournal;
    }

//    @Bean
//    public EventStore eventStore() {
//        return EventStoreBuilder.newBuilder().singleNodeAddress("127.0.0.1", 1113).userCredentials("admin", "changeit").build();
//    }

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
    public AccessTokenManager accessTokenManager() {
        return new AccessTokenManager(url, clientId, clientSecret, audience, grantType, scope);
    }

//    public EventStoreClient myEventStoreClient(AccessTokenManager accessTokenManager) {
//        Client client = ClientBuilder.newClient().register(new BearerTokenFilter(accessTokenManager));
//        return new JaxRsRestEventStoreClient(client, "http://localhost:8080");
//    }

    @Bean
    public EventStoreClient myEventStoreClient(ActorSystem actorSystem) {
        return new AkkaEventStore(actorSystem);
    }

    @Bean
    public ActorSystem actorSystem() {
        return ActorSystem.create(actorSystemName);
    }

    @Bean
    public ActorMaterializer actorMaterializer(ActorSystem actorSystem) {
        return ActorMaterializer.create(actorSystem);
    }

//    @Bean
//    public EventStore myEventStore() {
//        return new RedisEventStore("club-manager-event-store");
//    }

    @Bean
    public Client jaxRsClient() {
        return ClientBuilder.newClient();
    }

    @Bean
    public WebTarget clubManagerPersonServiceTarget(AccessTokenManager accessTokenManager) {
        Client client = ClientBuilder.newClient().register(new BearerTokenFilter(accessTokenManager));
        return client.target(personServiceUrl);
    }
}
