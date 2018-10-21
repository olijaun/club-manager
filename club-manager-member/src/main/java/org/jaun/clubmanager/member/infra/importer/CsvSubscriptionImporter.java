package org.jaun.clubmanager.member.infra.importer;

import java.io.File;
import java.nio.charset.Charset;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.StringUtils;
import org.jaun.clubmanager.member.application.resource.MembershipTypeDTO;
import org.jaun.clubmanager.member.application.resource.SubscriptionDTO;
import org.jaun.clubmanager.member.application.resource.SubscriptionPeriodDTO;
import org.jaun.clubmanager.member.application.resource.SubscriptionTypeDTO;

public class CsvSubscriptionImporter {

    private static void loadMembershipTypes() {

        MembershipTypeDTO ehren = new MembershipTypeDTO();
        ehren.setName("Ehrenmitglied");
        ehren.setDescription("Ehren-Mitgliedschaft");
        ehren.setId("0");

        MembershipTypeDTO normal = new MembershipTypeDTO();
        normal.setName("Normalmiglied");
        normal.setDescription("Normale Mitgliedschaft");
        normal.setId("1");

        MembershipTypeDTO goenner = new MembershipTypeDTO();
        goenner.setName("Gönnermitglied");
        goenner.setDescription("Gönnermitgliedschaft");
        goenner.setId("2");

        MembershipTypeDTO passiv = new MembershipTypeDTO();
        passiv.setName("Passivmitglied");
        passiv.setDescription("Passivmitgliedschaft");
        passiv.setId("3");

        Client client = ClientBuilder.newClient();
        WebTarget target = client.target("http://localhost:9001/api/membership-types");
        Response response1 = target.path(ehren.getId()).request().put(Entity.entity(ehren, MediaType.APPLICATION_JSON_TYPE));
        Response response2 = target.path(normal.getId()).request().put(Entity.entity(normal, MediaType.APPLICATION_JSON_TYPE));
        Response response3 = target.path(goenner.getId()).request().put(Entity.entity(goenner, MediaType.APPLICATION_JSON_TYPE));
        Response response4 = target.path(passiv.getId()).request().put(Entity.entity(passiv, MediaType.APPLICATION_JSON_TYPE));
        System.out.println("mt1: " + response1.getStatus());
        System.out.println("mt2: " + response2.getStatus());
        System.out.println("mt3: " + response3.getStatus());
        System.out.println("mt4: " + response4.getStatus());
    }

    private static void loadSubscriptionPeriod() {

        SubscriptionPeriodDTO period2018 = new SubscriptionPeriodDTO();
        period2018.setId("2018");
        period2018.setStartDate("2017-09-01");
        period2018.setEndDate("2018-12-31");
        period2018.setName("Periode 2017/2018");

        Client client = ClientBuilder.newClient();
        WebTarget target = client.target("http://localhost:9001/api/subscription-periods");
        Response subscriptionPeriodResponse =
                target.path(period2018.getId()).request().put(Entity.entity(period2018, MediaType.APPLICATION_JSON_TYPE));
        System.out.println("sp: " + subscriptionPeriodResponse.getStatus());


        SubscriptionTypeDTO ehren = new SubscriptionTypeDTO();
        ehren.setId("0");
        ehren.setName("Ehrenmigliedschaft 2018");
        ehren.setSubscriptionPeriodId("2018");
        ehren.setCurrency("CHF");
        ehren.setAmount(0);
        ehren.setMaxSubscribers(1);
        ehren.setMembershipTypeId("0");

        SubscriptionTypeDTO normal = new SubscriptionTypeDTO();
        normal.setId("1");
        normal.setName("Normalmitgliedschaft 2018");
        normal.setSubscriptionPeriodId("2018");
        normal.setCurrency("CHF");
        normal.setAmount(90);
        normal.setMaxSubscribers(1);
        normal.setMembershipTypeId("1");

        SubscriptionTypeDTO goenner = new SubscriptionTypeDTO();
        goenner.setId("2");
        goenner.setName("Gönnermigliedschaft 2018");
        goenner.setSubscriptionPeriodId("2018");
        goenner.setCurrency("CHF");
        goenner.setAmount(200);
        goenner.setMaxSubscribers(1);
        goenner.setMembershipTypeId("2");

        SubscriptionTypeDTO passiv = new SubscriptionTypeDTO();
        passiv.setId("3");
        passiv.setName("Passivmitgliedschaft 2018");
        passiv.setSubscriptionPeriodId("2018");
        passiv.setCurrency("CHF");
        passiv.setAmount(20);
        passiv.setMaxSubscribers(1);
        passiv.setMembershipTypeId("3");

        target = client.target("http://localhost:9001/api/subscription-periods").path(period2018.getId()).path("types");
        Response response1 = target.path("0").request().put(Entity.entity(ehren, MediaType.APPLICATION_JSON_TYPE));
        Response response2 = target.path("1").request().put(Entity.entity(normal, MediaType.APPLICATION_JSON_TYPE));
        Response response3 = target.path("2").request().put(Entity.entity(goenner, MediaType.APPLICATION_JSON_TYPE));
        Response response4 = target.path("3").request().put(Entity.entity(passiv, MediaType.APPLICATION_JSON_TYPE));

        System.out.println("st1: " + response1.getStatus());
        System.out.println("st2: " + response2.getStatus());
        System.out.println("st3: " + response3.getStatus());
        System.out.println("st4: " + response4.getStatus());
    }

    public static void main(String[] args) throws Exception {

        loadMembershipTypes();
        loadSubscriptionPeriod();

        File file = new File("/home/oliver/Dropbox/Caracoles-Vorstand/Mitgliederliste/Mitglieder.csv");
        CSVParser parser =
                CSVParser.parse(file, Charset.forName("UTF-8"), CSVFormat.newFormat(',').withQuote('"').withFirstRecordAsHeader());

        List<CSVRecord> records = parser.getRecords();

        List<SubscriptionDTO> subscriptions = new ArrayList<>(records.size());

        for (CSVRecord record : records) {

            SubscriptionDTO subscription1 = new SubscriptionDTO();
            subscription1.setMemberId(record.get("ID"));
            subscription1.setSubscriptionPeriodId("2018");

            String[] ausweisNumbers = record.get("ausweisnr").split(" ");
            String ausweisNr1 = ausweisNumbers[0];

            subscription1.setId(ausweisNr1);

            String ausweisNr2 = ausweisNumbers.length > 1 ? ausweisNumbers[1] : null;

            if (ausweisNr1.length() < 3) {
                // ehrenmitglied
                subscription1.setSubscriptionTypeId("0");
            } else if (ausweisNr1.startsWith("2")) {
                // gönner
                subscription1.setSubscriptionTypeId("2");
            } else {
                // normal mitglied
                subscription1.setSubscriptionTypeId("1");
            }

            SubscriptionDTO subscription2 = null;
            if (StringUtils.isNotEmpty(record.get("name2")) || StringUtils.isNotEmpty(record.get("vorname2"))) {

                subscription2 = new SubscriptionDTO();
                subscription2.setMemberId(record.get("ID") + "2");
                subscription2.setSubscriptionPeriodId(subscription1.getSubscriptionPeriodId());
                subscription2.setSubscriptionTypeId(subscription1.getSubscriptionTypeId());

                if (StringUtils.isBlank(ausweisNr2)) {
                    subscription2.setId(ausweisNr1 + ".2");
                } else {
                    subscription2.setId(ausweisNr1 + "." + ausweisNr2);
                }
            }

            if (subscription1.getId().equals("10")) {
                System.out.println("10 --> " + record.get("name"));
            }
            if (subscription2 != null && subscription2.getId().equals("10")) {
                System.out.println("10 --> " + record.get("name2"));
            }

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

            String bezbisString = record.get("bezbis");
            if (!StringUtils.isEmpty(bezbisString)) {
                LocalDate bezbisDate = LocalDate.parse(bezbisString, formatter);
                if (bezbisDate.getYear() >= 2018) {
                    subscriptions.add(subscription1);
                    if (subscription2 != null) {
                        subscriptions.add(subscription2);
                    }
                }
            }
        }

        // make sure there are no duplicate ids
        Map<String, SubscriptionDTO> set =
                subscriptions.stream().collect(Collectors.toMap(SubscriptionDTO::getId, Function.identity()));

        for (SubscriptionDTO subscriptionDTO : subscriptions) {
            save(subscriptionDTO);
        }
    }

    private static void save(SubscriptionDTO subscriptionDTO) {

        Client client = ClientBuilder.newClient();
        WebTarget target = client.target("http://localhost:9001/api/subscriptions").path(subscriptionDTO.getId());

        Response response = target.request().put(Entity.entity(subscriptionDTO, MediaType.APPLICATION_JSON_TYPE));

        if (response.getStatusInfo().getFamily().equals(Response.Status.Family.SUCCESSFUL)) {
            System.out.println("saved subscription " + subscriptionDTO);
        } else {
            System.out.println("failed with code: " + response.getStatus() + ", subscription: " + subscriptionDTO);
        }
    }
}
