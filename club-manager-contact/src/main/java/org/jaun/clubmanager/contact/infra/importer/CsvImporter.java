package org.jaun.clubmanager.contact.infra.importer;

import java.io.File;
import java.nio.charset.Charset;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

            ContactDTO contactDTO1 = new ContactDTO();
            contactDTO1.setContactId(record.get("ID"));


            NameDTO nameDTO = new NameDTO();

            nameDTO.setFirstName(record.get("vorname"));
            nameDTO.setLastNameOrCompanyName(record.get("name"));
            contactDTO1.setName(nameDTO);

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

            contactDTO1.setStreetAddress(streetAddressDTO);

            contactDTO1.setContactType("PERSON");
            if (StringUtils.isNotBlank(record.get("email"))) {
                contactDTO1.setEmailAddress(record.get("email"));
            }

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

            ContactDTO contactDTO2 = null;
            if (StringUtils.isNotEmpty(record.get("name2")) || StringUtils.isNotEmpty(record.get("vorname2"))) {
                contactDTO2 = new ContactDTO();
                contactDTO2.setContactId(record.get("ID") + "2");
                contactDTO2.setContactType("PERSON");
                NameDTO nameDTO2 = new NameDTO();
                nameDTO2.setLastNameOrCompanyName(record.get("name2"));
                nameDTO2.setFirstName(record.get("vorname2"));
                contactDTO2.setName(nameDTO2);
                contactDTO2.setStreetAddress(streetAddressDTO);
                if (StringUtils.isNotBlank(record.get("email2"))) {
                    contactDTO2.setEmailAddress(record.get("email2"));
                } else {
                    contactDTO2.setEmailAddress(contactDTO1.getEmailAddress());
                }
            }

            String bezbisString = record.get("bezbis");
            if (!StringUtils.isEmpty(bezbisString)) {
                LocalDate bezbisDate = LocalDate.parse(bezbisString, formatter);
                if (bezbisDate.getYear() >= 2018) {
                    contacts.add(contactDTO1);
                    if (contactDTO2 != null) {
                        contacts.add(contactDTO2);
                    }
                }
            }
        }


        // make sure there are no duplicate ids
        Map<String, ContactDTO> set = contacts.stream().collect(Collectors.toMap(ContactDTO::getContactId, Function.identity()));

        for (ContactDTO contactDTO : contacts) {
            save(contactDTO);
        }
    }

    private static void save(ContactDTO contactDTO) {

        Client client = ClientBuilder.newClient();
        WebTarget target = client.target("http://localhost:9001/api/contacts").path(contactDTO.getContactId());

        Response response = target.request().put(Entity.entity(contactDTO, MediaType.APPLICATION_JSON_TYPE));

        if (response.getStatusInfo().getFamily().equals(Response.Status.Family.SUCCESSFUL)) {
            System.out.println("saved contact " + contactDTO.getContactId() + ": " + contactDTO.getName().getFirstName() + " "
                               + contactDTO.getName().getLastNameOrCompanyName());
        } else {
            System.out.println("failed with code: " + response.getStatus() + ", contact: " + contactDTO);
        }
    }
}
