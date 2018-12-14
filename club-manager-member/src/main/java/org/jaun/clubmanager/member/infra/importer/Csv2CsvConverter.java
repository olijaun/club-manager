package org.jaun.clubmanager.member.infra.importer;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.StringUtils;
import org.jaun.clubmanager.member.application.resource.MemberCsvFormat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Csv2CsvConverter {

    private static String formatId(String id) {
        int asInt = Integer.parseInt(id);

        return String.format("P%08d", asInt);
    }

    public static void main(String[] args) throws Exception {

        File file = new File("/home/oliver/Dropbox/Caracoles-Vorstand/Mitgliederliste/Mitglieder.csv");
        CSVParser parser =
                CSVParser.parse(file, Charset.forName("UTF-8"), CSVFormat.newFormat(',').withQuote('"').withFirstRecordAsHeader());

        FileOutputStream fos = new FileOutputStream("/tmp/member.csv");
        OutputStreamWriter writer = new OutputStreamWriter(fos);
        CSVPrinter printer = new CSVPrinter(writer, MemberCsvFormat.FORMAT.withTrim(true));

        List<CSVRecord> inputRecords = parser.getRecords();

        List<String> usedIds = new ArrayList<>();

        for (CSVRecord inputRecord : inputRecords) {

            ArrayList member1Record = new ArrayList();
            String person1Id = formatId(inputRecord.get("ID"));

            if (usedIds.contains(person1Id)) {
                throw new IllegalStateException("id already used: " + person1Id);
            }
            usedIds.add(person1Id);

            String bezbisString = inputRecord.get("bezbis");

            int ausweisNr;
            try {
                ausweisNr = Integer.parseInt(inputRecord.get("ausweisnr").split(" ")[0]);
            } catch (NumberFormatException e) {
                System.out.printf("error: " + inputRecord.toMap() + "; "); // + e.getMessage());
                continue;
            }

            if (inputRecord.get("vorname").equals("Gunnar")) {
                System.out.println("-----------------------> h");
            }

            if (StringUtils.isBlank(bezbisString)
                    || (!bezbisString.equals("31.08.2018") && (ausweisNr < 9 || ausweisNr > 20))) {
                continue;
            }

            member1Record.add(person1Id);

            String lastName = inputRecord.get("name");
            if (StringUtils.isBlank(lastName)) {
                lastName = "?";
            }

            member1Record.add(lastName);
            member1Record.add(inputRecord.get("vorname"));
            member1Record.add(null); // address
            member1Record.add(null); // subscriptionSummary

            int memberType;
            if (ausweisNr > 9 && ausweisNr < 100) {
                memberType = 0; // ehren
            } else if (ausweisNr > 199 && ausweisNr < 300) {
                memberType = 2; // gönner
            } else {
                memberType = 1; // normal
            }
            // TODO: passiv?

            member1Record.add(UUID.randomUUID()); // subscriptionId
            member1Record.add("1"); // subscriptionPeriodId
            member1Record.add(memberType); // subscription type

            printer.printRecord(member1Record);

            if (memberType != 2)
                // do not add second person for goenner because goenner is a "double membership"

                if (StringUtils.isNotEmpty(inputRecord.get("name2")) || StringUtils.isNotEmpty(inputRecord.get("vorname2"))) {

                    ArrayList person2Record = new ArrayList();
                    String person2Id = formatId("" + (Integer.parseInt(inputRecord.get("ID")) + 2000));
                    if (usedIds.contains(person2Id)) {
                        throw new IllegalStateException("id already exists: " + person2Id);
                    }

                    usedIds.add(person2Id);
                    person2Record.add(person2Id);
                    String lastName2 = inputRecord.get("name2");
                    if (StringUtils.isBlank(lastName2)) {
                        lastName2 = "?";
                    }
                    person2Record.add(lastName2);
                    person2Record.add(inputRecord.get("vorname2"));

                    person2Record.add(null); // address
                    person2Record.add(null); // subscriptionSummary

                    person2Record.add(UUID.randomUUID()); // subscriptionId
                    person2Record.add("1"); // subscriptionPeriodId
                    person2Record.add(memberType); // subscription type

                    printer.printRecord(person2Record);
                }
        }

        printer.flush();
        printer.close();
    }
}
