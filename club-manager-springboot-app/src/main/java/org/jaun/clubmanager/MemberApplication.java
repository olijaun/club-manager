package org.jaun.clubmanager;

import akka.actor.ActorSystem;
import akka.persistence.jdbc.query.javadsl.JdbcReadJournal;
import akka.persistence.query.PersistenceQuery;
import akka.persistence.query.javadsl.EventsByTagQuery;
import akka.stream.ActorMaterializer;
import com.hazelcast.config.Config;
import com.hazelcast.config.JoinConfig;
import com.hazelcast.config.NetworkConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import org.jaun.clubmanager.eventstore.EventStoreClient;
import org.jaun.clubmanager.eventstore.akka.AkkaEventStore;
import org.jaun.clubmanager.member.domain.model.member.MemberRepository;
import org.jaun.clubmanager.member.domain.model.membershiptype.MembershipTypeRepository;
import org.jaun.clubmanager.member.domain.model.subscriptionperiod.SubscriptionPeriodRepository;
import org.jaun.clubmanager.member.infra.projection.HazelcastMemberProjection;
import org.jaun.clubmanager.member.infra.repository.MemberRepositoryImpl;
import org.jaun.clubmanager.member.infra.repository.MembershipTypeRepositoryImpl;
import org.jaun.clubmanager.member.infra.repository.SubscriptionPeriodRepositoryImpl;
import org.jaun.clubmanager.member.infra.service.ClubManagerPersonService;
import org.jaun.clubmanager.oauth.AccessTokenManager;
import org.jaun.clubmanager.oauth.BearerTokenFilter;
import org.jaun.clubmanager.person.domain.model.person.PersonIdRegistryRepository;
import org.jaun.clubmanager.person.domain.model.person.PersonRepository;
import org.jaun.clubmanager.person.infra.projection.HazelcastPersonProjection;
import org.jaun.clubmanager.person.infra.repository.PersonIdRegistryRepositoryImpl;
import org.jaun.clubmanager.person.infra.repository.PersonRepositoryImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.ContextStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;

@SpringBootApplication(scanBasePackages = {"org.jaun.clubmanager"})
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, jsr250Enabled = true)
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

    @Bean
    public CommandLineRunner commandLineRunner(ApplicationContext ctx) {

        return args -> {
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
//    public Client jaxRsClient() {
//        return ClientBuilder.newClient();
//    }

    @Bean
    public ClubManagerPersonService clubManagerPersonService() {
        Client client = ClientBuilder.newClient().register(new BearerTokenFilter());
        WebTarget target = client.target(personServiceUrl);
        return new ClubManagerPersonService(target);
    }

    @Bean
    public PersonIdRegistryRepository personIdRegistryRepository(EventStoreClient eventStoreClient) {
        return new PersonIdRegistryRepositoryImpl(eventStoreClient);
    }

    @Bean
    public PersonRepository personRepository(EventStoreClient eventStoreClient) {
        return new PersonRepositoryImpl(eventStoreClient);
    }

    @Bean
    public MemberRepository memberRepository(EventStoreClient eventStoreClient) {
        return new MemberRepositoryImpl(eventStoreClient);
    }

    @Bean
    public MembershipTypeRepository membershipTypeRepository(EventStoreClient eventStoreClient) {
        return new MembershipTypeRepositoryImpl(eventStoreClient);
    }

    @Bean
    public SubscriptionPeriodRepository subscriptionPeriodRepository(EventStoreClient eventStoreClient) {
        return new SubscriptionPeriodRepositoryImpl(eventStoreClient);
    }

    @Bean
    public HazelcastPersonProjection hazelcastPersonProjection(ActorSystem actorSystem, ActorMaterializer actorMaterializer, EventsByTagQuery eventsByTagQuery, HazelcastInstance hazelcastInstance) {
        return new HazelcastPersonProjection(actorSystem, actorMaterializer, eventsByTagQuery, hazelcastInstance);
    }

    @Bean
    public HazelcastMemberProjection hazelcastMemberProjection(ActorSystem actorSystem, ActorMaterializer actorMaterializer, EventsByTagQuery eventsByTagQuery, HazelcastInstance hazelcastInstance) {
        return new HazelcastMemberProjection(actorSystem, actorMaterializer, eventsByTagQuery, hazelcastInstance);
    }
}
