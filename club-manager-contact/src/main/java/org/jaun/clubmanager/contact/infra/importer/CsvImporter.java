package org.jaun.clubmanager.contact.infra.importer;

import java.io.File;
import java.nio.charset.Charset;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.StringUtils;
import org.jaun.clubmanager.contact.application.resource.ContactDTO;
import org.jaun.clubmanager.contact.application.resource.NameDTO;
import org.jaun.clubmanager.contact.application.resource.StreetAddressDTO;

public class CsvImporter {

    public static void main(String[] args) throws Exception {

        File file = new File("/home/oliver/Dropbox/Caracoles-Vorstand/Mitgliederliste/Mitglieder.csv");
        CSVParser parser =
                CSVParser.parse(file, Charset.forName("UTF-8"), CSVFormat.newFormat(',').withQuote('"').withFirstRecordAsHeader());


        List<CSVRecord> records = parser.getRecords();

        List<ContactDTO> contacts = new ArrayList<>(records.size());

        for (CSVRecord record : records) {

            ContactDTO contactDTO = new ContactDTO();
            contactDTO.setContactId(record.get("ausweisnr"));

            NameDTO nameDTO = new NameDTO();

            nameDTO.setFirstName(record.get("vorname"));
            nameDTO.setLastNameOrCompanyName(record.get("name"));
            contactDTO.setName(nameDTO);

            StreetAddressDTO streetAddressDTO = new StreetAddressDTO();

            String streetAndHouseNumber = record.get("adresse");
            String[] split = streetAndHouseNumber.split(" ");

            if (split.length == 0) {
                // no address
            } else if (split.length == 1) {
                streetAddressDTO.setStreet(split[0]);
            } else if (split.length > 1) {
                streetAddressDTO.setStreet(split[0]);
                streetAddressDTO.setStreetNumber(Stream.of(split).skip(1).collect(Collectors.joining(" ")));
            }
            streetAddressDTO.setZip(record.get("plz"));
            streetAddressDTO.setCity(record.get("ort"));
            streetAddressDTO.setIsoCountryCode("CH");

            contactDTO.setStreetAddress(streetAddressDTO);

            contactDTO.setContactType("PERSON");
            contactDTO.setEmailAddress(record.get("email"));

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

            String bezbisString = record.get("bezbis");
            if(!StringUtils.isEmpty(bezbisString)) {
                LocalDate bezbisDate = LocalDate.parse(bezbisString, formatter);
                if (bezbisDate.getYear() >= 2018) {
                    contacts.add(contactDTO);
                }
            }
        }
        System.out.println(contacts);
    }
}
