package org.jaun.clubmanager.person.application.resource;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;

import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

import org.apache.commons.csv.CSVPrinter;


@Provider
@Produces("text/csv")
public class PersonsDTOCsvWriter implements MessageBodyWriter<PersonsDTO> {

    @Override
    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return type == PersonsDTO.class;
    }

    @Override
    public long getSize(PersonsDTO personsDTO, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        // deprecated by JAX-RS 2.0 and ignored by Jersey runtime
        return 0;
    }

    @Override
    public void writeTo(PersonsDTO personsDTO, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType,
            MultivaluedMap<String, Object> httpHeaders, OutputStream out) throws IOException, WebApplicationException {

        Writer writer = new PrintWriter(out);

        CSVPrinter csvPrinter = new CSVPrinter(writer, PersonCsvFormat.FORMAT);

        personsDTO.getPersons().forEach(p -> {
            try {

                ArrayList<String> record = new ArrayList<>();

                record.addAll(Arrays.asList(p.getId(), p.getType(),
                        p.getBasicData().getName().getLastNameOrCompanyName(), p.getBasicData().getName().getFirstName(),
                        p.getBasicData().getBirthDate(), p.getBasicData().getGender()));
                if (p.getContactData() == null) {
                    record.addAll(Arrays.asList(null, null));
                } else {

                    record.addAll(Arrays.asList(p.getContactData().getEmailAddress(), p.getContactData().getPhoneNumber()));
                }

                if (p.getStreetAddress() == null) {
                    record.addAll(Arrays.asList(null, null, null, null, null, null));
                } else {
                    StreetAddressDTO streetAddress = p.getStreetAddress();
                    record.addAll(Arrays.asList(streetAddress.getStreet(), streetAddress.getHouseNumber(), streetAddress.getZip(),
                            streetAddress.getCity(), streetAddress.getState(), streetAddress.getIsoCountryCode()));
                }
                csvPrinter.printRecord(record);

            } catch (IOException e) {
                throw new RuntimeException("failed to generate csv for record: " + p, e);
            }
        });

        csvPrinter.flush();
        csvPrinter.close();
    }
}
