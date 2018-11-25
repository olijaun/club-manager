package org.jaun.clubmanager.person.infra.importer;

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
import org.jaun.clubmanager.person.application.resource.BasicDataDTO;
import org.jaun.clubmanager.person.application.resource.ContactDataDTO;
import org.jaun.clubmanager.person.application.resource.NameDTO;
import org.jaun.clubmanager.person.application.resource.PersonDTO;
import org.jaun.clubmanager.person.application.resource.StreetAddressDTO;

public class CsvImporter {

    public static void main(String[] args) throws Exception {

        File file = new File("/home/oliver/Dropbox/Caracoles-Vorstand/Mitgliederliste/Mitglieder.csv");
        CSVParser parser =
                CSVParser.parse(file, Charset.forName("UTF-8"), CSVFormat.newFormat(',').withQuote('"').withFirstRecordAsHeader());


        List<CSVRecord> records = parser.getRecords();

        List<PersonDTO> personDTOS = new ArrayList<>(records.size());

        for (CSVRecord record : records) {

            PersonDTO personDTO1 = new PersonDTO();
            personDTO1.setId(record.get("ID"));

            BasicDataDTO basicDataDTO = new BasicDataDTO();

            NameDTO nameDTO = new NameDTO();
            nameDTO.setFirstName(record.get("vorname"));
            nameDTO.setLastNameOrCompanyName(record.get("name"));

            basicDataDTO.setName(nameDTO);

            personDTO1.setBasicData(basicDataDTO);

            StreetAddressDTO streetAddressDTO = new StreetAddressDTO();

            String streetAndHouseNumber = record.get("adresse");
            String[] split = streetAndHouseNumber.split(" ");

            if (split.length == 0) {
                // no address
            } else if (split.length == 1) {
                streetAddressDTO.setStreet(split[0]);
            } else if (split.length > 1) {
                streetAddressDTO.setStreet(split[0]);
                streetAddressDTO.setHouseNumber(Stream.of(split).skip(1).collect(Collectors.joining(" ")));
            }
            streetAddressDTO.setZip(record.get("plz"));
            streetAddressDTO.setCity(record.get("ort"));
            streetAddressDTO.setIsoCountryCode("CH");

            personDTO1.setStreetAddress(streetAddressDTO);

            ContactDataDTO contactDataDTO = new ContactDataDTO();

            personDTO1.setType("NATURAL");
            if (StringUtils.isNotBlank(record.get("email"))) {
                contactDataDTO.setEmailAddress(record.get("email"));
            }

            personDTO1.setContactData(contactDataDTO);

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

            PersonDTO personDTO2 = null;
            if (StringUtils.isNotEmpty(record.get("name2")) || StringUtils.isNotEmpty(record.get("vorname2"))) {
                personDTO2 = new PersonDTO();
                personDTO2.setId(record.get("ID") + "2");
                personDTO2.setType("NATURAL");
                NameDTO nameDTO2 = new NameDTO();
                nameDTO2.setLastNameOrCompanyName(record.get("name2"));
                nameDTO2.setFirstName(record.get("vorname2"));

                BasicDataDTO basicDataDTO2 = new BasicDataDTO();

                basicDataDTO2.setName(nameDTO2);

                personDTO2.setBasicData(basicDataDTO2);

                personDTO2.setStreetAddress(streetAddressDTO);

                ContactDataDTO contactDataDTO2 = new ContactDataDTO();

                if (StringUtils.isNotBlank(record.get("email2"))) {
                    contactDataDTO2.setEmailAddress(record.get("email2"));
                } else {
                    contactDataDTO2.setEmailAddress(contactDataDTO.getEmailAddress());
                }
                personDTO2.setContactData(contactDataDTO2);
            }

            String bezbisString = record.get("bezbis");
            if (!StringUtils.isEmpty(bezbisString)) {
                LocalDate bezbisDate = LocalDate.parse(bezbisString, formatter);
                if (bezbisDate.getYear() >= 2018) {
                    personDTOS.add(personDTO1);
                    if (personDTO2 != null) {
                        personDTOS.add(personDTO2);
                    }
                }
            }
        }


        // make sure there are no duplicate ids
        Map<String, PersonDTO> set = personDTOS.stream().collect(Collectors.toMap(PersonDTO::getId, Function.identity()));

        for (PersonDTO personDTO : personDTOS) {
            save(personDTO);
        }
    }

    private static void save(PersonDTO personDTO) {

        Client client = ClientBuilder.newClient();
        WebTarget target = client.target("http://localhost:9001/api/persons").path(personDTO.getId());

        Response response = target.request().put(Entity.entity(personDTO, MediaType.APPLICATION_JSON_TYPE));

        if (response.getStatusInfo().getFamily().equals(Response.Status.Family.SUCCESSFUL)) {
            System.out.println("saved contact " + personDTO.getId() + ": " + personDTO.getBasicData().getName().getFirstName() + " "
                               + personDTO.getBasicData().getName().getLastNameOrCompanyName());
        } else {
            System.out.println("failed with code: " + response.getStatus() + ", person: " + personDTO);
        }
    }
}
