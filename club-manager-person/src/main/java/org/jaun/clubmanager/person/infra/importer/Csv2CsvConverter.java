package org.jaun.clubmanager.person.infra.importer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.StringUtils;
import org.jaun.clubmanager.person.application.resource.PersonCsvFormat;

public class Csv2CsvConverter {

    private static String formatId(String id) {
        int asInt = Integer.parseInt(id);

        return String.format("P%08d", asInt);
    }

    public static void main(String[] args) throws Exception {

        File file = new File("/home/oliver/Dropbox/Caracoles-Vorstand/Mitgliederliste/Mitglieder.csv");
        CSVParser parser =
                CSVParser.parse(file, Charset.forName("UTF-8"), CSVFormat.newFormat(',').withQuote('"').withFirstRecordAsHeader());

        FileOutputStream fos = new FileOutputStream("/tmp/out.csv");
        OutputStreamWriter writer = new OutputStreamWriter(fos);
        CSVPrinter printer = new CSVPrinter(writer, PersonCsvFormat.FORMAT.withTrim(true));

        List<CSVRecord> inputRecords = parser.getRecords();

        List<String> usedIds = new ArrayList<>();

        for (CSVRecord inputRecord : inputRecords) {

            ArrayList person1Record = new ArrayList();
            String person1Id = formatId(inputRecord.get("ID"));
            if (usedIds.contains(person1Id)) {
                throw new IllegalStateException("id already used: " + person1Id);
            }
            usedIds.add(person1Id);
            person1Record.add(person1Id);
            person1Record.add("NATURAL");

            String lastName = inputRecord.get("name");
            if (StringUtils.isBlank(lastName)) {
                lastName = "?";
            }
            person1Record.add(lastName);
            person1Record.add(inputRecord.get("vorname"));
            person1Record.add(null); // birthDate
            person1Record.add(null); // gender
            person1Record.add(inputRecord.get("email"));
            person1Record.add(null); // phone

            String streetAndHouseNumber = inputRecord.get("adresse");
            String[] split = streetAndHouseNumber.split(" ");

            String street = null;
            String houseNumber = null;
            if (split.length == 0) {
                // no address
            } else if (split.length == 1) {
                street = split[0];
            } else if (split.length > 1) {

                List<String> elements = Arrays.asList(split);
                street = elements.subList(0, elements.size() - 1).stream().collect(Collectors.joining(" "));
                houseNumber = elements.get(elements.size() - 1);
            }

            String zip = inputRecord.get("plz");
            String city = inputRecord.get("ort");

            if (StringUtils.isBlank(street) || StringUtils.isBlank(zip)) {

                // if street or zip is not specified then the address is invalid and not added

                person1Record.add(null); // street
                person1Record.add(null); // house number
                person1Record.add(null); // zip
                person1Record.add(null); // city
                person1Record.add(null); // state
                person1Record.add(null); // country
            } else {
                person1Record.add(street);
                person1Record.add(houseNumber);
                person1Record.add(zip);
                person1Record.add(city);
                person1Record.add(null); // state
                person1Record.add("CH");
            }

            printer.printRecord(person1Record);

            if (StringUtils.isNotEmpty(inputRecord.get("name2")) || StringUtils.isNotEmpty(inputRecord.get("vorname2"))) {

                ArrayList person2Record = new ArrayList();
                String person2Id = formatId("" + (Integer.parseInt(inputRecord.get("ID")) + 2000));
                if (usedIds.contains(person2Id)) {
                    throw new IllegalStateException("id already exists: " + person2Id);
                }

                usedIds.add(person2Id);
                person2Record.add(person2Id);
                person2Record.add("NATURAL");
                String lastName2 = inputRecord.get("name2");
                if (StringUtils.isBlank(lastName2)) {
                    lastName2 = "?";
                }
                person2Record.add(lastName2);
                person2Record.add(inputRecord.get("vorname2"));

                person2Record.add(null); // birthDate
                person2Record.add(null); // gender

                if (StringUtils.isNotBlank(inputRecord.get("email2"))) {
                    person2Record.add(inputRecord.get("email2"));
                } else {
                    person2Record.add(inputRecord.get("email"));
                }

                person2Record.add(null); // phone

                if (StringUtils.isBlank(street) || StringUtils.isBlank(zip)) {

                    // if street or zip is not specified then the address is invalid and not added

                    person2Record.add(null); // street
                    person2Record.add(null); // house number
                    person2Record.add(null); // zip
                    person2Record.add(null); // city
                    person2Record.add(null); // state
                    person2Record.add(null); // country
                } else {
                    person2Record.add(street);
                    person2Record.add(houseNumber);
                    person2Record.add(zip);
                    person2Record.add(city);
                    person2Record.add(null); // state
                    person2Record.add("CH");
                }

                printer.printRecord(person2Record);
            }

            String bezbisString = inputRecord.get("bezbis");
        }

        printer.flush();
        printer.close();
    }
}
