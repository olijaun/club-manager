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

public class Csv2CsvConverter {


    public static void main(String[] args) throws Exception {

        File file = new File("/home/oliver/Dropbox/Caracoles-Vorstand/Mitgliederliste/Mitglieder.csv");
        CSVParser parser =
                CSVParser.parse(file, Charset.forName("UTF-8"), CSVFormat.newFormat(',').withQuote('"').withFirstRecordAsHeader());

        List<CSVRecord> inputRecords = parser.getRecords();

        List<SubscriptionDTO> subscriptions = new ArrayList<>(inputRecords.size());

        for (CSVRecord record : inputRecords) {

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

    }

}
