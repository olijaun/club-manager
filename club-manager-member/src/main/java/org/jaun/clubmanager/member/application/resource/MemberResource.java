package org.jaun.clubmanager.member.application.resource;

import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.StringUtils;
import org.jaun.clubmanager.domain.model.commons.ConcurrencyException;
import org.jaun.clubmanager.member.domain.model.member.*;
import org.jaun.clubmanager.member.domain.model.person.Person;
import org.jaun.clubmanager.member.domain.model.person.PersonId;
import org.jaun.clubmanager.member.domain.model.person.PersonService;
import org.jaun.clubmanager.member.domain.model.subscriptionperiod.*;
import org.jaun.clubmanager.member.infra.projection.HazelcastMemberProjection;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Path("/members")
public class MemberResource {

    @Inject
    private MemberRepository memberRepository;

    @Inject
    private SubscriptionPeriodRepository subscriptionPeriodRepository;

    @Inject
    private HazelcastMemberProjection projection;

    @Inject
    private PersonService personService;

    @GET
    @Produces({MediaType.APPLICATION_JSON, "text/csv"})
    @Path("/")
    public Response getMembers(@QueryParam("searchString") String searchString,
                               @QueryParam("subscriptionPeriodId") String subscriptionPeriodIdAsString, @QueryParam("sortBy") String sortyBy,
                               @QueryParam("sortAscending") String sortAscending) {

        if (StringUtils.isBlank(searchString)) {
            searchString = null;
        }

        if (StringUtils.isBlank(subscriptionPeriodIdAsString)) {
            subscriptionPeriodIdAsString = null;
        }

        boolean ascending = sortAscending == null || "true".equals(sortAscending);

        Collection<MemberDTO> memberDTOS = projection.searchMembers(searchString, subscriptionPeriodIdAsString, sortyBy, ascending);

        MembersDTO membersDTO = new MembersDTO();
        membersDTO.setSubscriptionPeriodIdFilter(subscriptionPeriodIdAsString);
        membersDTO.getMembers().addAll(memberDTOS);

        return Response.ok(membersDTO).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{member-id}")
    public Response getMember(@PathParam("member-id") String memberIdAsString) {

        MemberId memberId = new MemberId(memberIdAsString);

        MemberDTO memberDTO = projection.getMember(memberId).orElseThrow(() -> new NotFoundException());

        return Response.ok(memberDTO).build();
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/{member-id}")
    public Response createOrUpdateMember(@PathParam("member-id") String memberIdAsString, CreateMemberDTO createMemberDTO) {

        MemberId memberId = new MemberId(memberIdAsString);

        Member member = getOrCreateMember(memberId);

        List<SubscriptionRequest> subscriptionRequests = createMemberDTO.getSubscriptions().stream().map(subscriptionDTO -> {

            SubscriptionId subscriptionId = new SubscriptionId(subscriptionDTO.getId());
            SubscriptionTypeId subscriptionTypeId = new SubscriptionTypeId(subscriptionDTO.getSubscriptionTypeId());
            SubscriptionPeriodId subscriptionPeriodId = new SubscriptionPeriodId(subscriptionDTO.getSubscriptionPeriodId());

            SubscriptionPeriod period = getSubscriptionPeriod(subscriptionPeriodId);
            SubscriptionRequest subscriptionRequest = period.createSubscriptionRequest(subscriptionId, subscriptionTypeId,
                    Collections.emptyList());// TODO: support additional subscribers

            return subscriptionRequest;
        }).collect(Collectors.toList());

        Collection<Subscription> removals = member.getSubscriptions().getRemovals(subscriptionRequests);

        // TODO: improve handling when subscription are modified etc.
        if (!removals.isEmpty()) {
            throw new BadRequestException("you must not remove existing subscriptions: " + removals.stream()
                    .map(Subscription::getId)
                    .map(SubscriptionId::getValue)
                    .collect(Collectors.toList()));
        }

        subscriptionRequests.stream().forEach(r -> member.subscribe(r));

        try {
            memberRepository.save(member);
        } catch (ConcurrencyException e) {
            throw new IllegalStateException(e);
        }

        return Response.ok().build();
    }

    @POST
    @Consumes("text/csv")
    @Produces(MediaType.APPLICATION_JSON)
    @Path("")
    public Response importMembers(InputStream inputStream) {

        InputStreamReader reader = new InputStreamReader(inputStream);
        CSVParser csvParser;
        try {
            csvParser = new CSVParser(reader, MemberCsvFormat.FORMAT.withNullString(""));
        } catch (IOException e) {
            return Response.serverError().build();
        }

        List<CSVRecord> records;

        try {
            records = csvParser.getRecords();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        MemberCsvImportResultDTO resultDTO = new MemberCsvImportResultDTO();

        // skip header because CSVParser seams to return it although i specified "withHeader"
        for (int i = 1; i < records.size(); i++) {
            CSVRecord r = records.get(i);
            try {
                MemberId memberId = new MemberId(r.get(MemberCsvFormat.ID));
                Member member = getOrCreateMember(memberId);

                SubscriptionId subscriptionId = new SubscriptionId(r.get(MemberCsvFormat.SUBSCRIPTION_ID));
                SubscriptionTypeId subscriptionTypeId = new SubscriptionTypeId(r.get(MemberCsvFormat.SUBSCRIPTION_TYPE_ID));
                SubscriptionPeriodId subscriptionPeriodId = new SubscriptionPeriodId(r.get(MemberCsvFormat.SUBSCRIPTION_PERIOD_ID));

                SubscriptionPeriod period = getSubscriptionPeriod(subscriptionPeriodId);

                SubscriptionRequest subscriptionRequest =
                        period.createSubscriptionRequest(subscriptionId, subscriptionTypeId, Collections.emptyList());

                member.subscribe(subscriptionRequest);

                memberRepository.save(member);
                resultDTO.getImportedIds().add(subscriptionId.getValue());

            } catch (Exception e) {
                resultDTO.getFailed().add(new MemberCsvImportResultDTO.FailedImport(r.get(MemberCsvFormat.SUBSCRIPTION_ID), e.getMessage()));
            }
        }

        return Response.ok(resultDTO).build();
    }

    private Member getOrCreateMember(MemberId memberId) {

        Member member = memberRepository.get(memberId);
        if (member == null) {
            PersonId personId = new PersonId(memberId.getValue());
            Person person = personService.getPerson(personId);

            if (person == null) {
                throw new BadRequestException("person does not exist: " + personId);
            }

            member = new Member(memberId);
        }

        return member;
    }

    private SubscriptionPeriod getSubscriptionPeriod(SubscriptionPeriodId subscriptionPeriodId) {
        SubscriptionPeriod period = subscriptionPeriodRepository.get(subscriptionPeriodId);

        if (period == null) {
            throw new NotFoundException("could not find membership period " + subscriptionPeriodId.getValue());
        }
        return period;
    }
}
