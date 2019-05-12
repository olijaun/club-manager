package org.jaun.clubmanager.member.application.resource;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;


@Provider
@Produces("text/csv")
public class MembersDTOCsvWriter implements MessageBodyWriter<MembersDTO> {

    @Override
    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return type == MembersDTO.class;
    }

    @Override
    public long getSize(MembersDTO membersDTO, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        // deprecated by JAX-RS 2.0 and ignored by Jersey runtime
        return 0;
    }

    @Override
    public void writeTo(MembersDTO membersDTO, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType,
            MultivaluedMap<String, Object> httpHeaders, OutputStream out) throws IOException, WebApplicationException {

        Writer writer = new PrintWriter(out);
        CSVPrinter csvPrinter = new CSVPrinter(writer, MemberCsvFormat.FORMAT);

        membersDTO.getMembers().forEach(m -> {

            if(m.getSubscriptions().isEmpty()) {

                writeRecord(m, Optional.empty(), csvPrinter);

            } else {
                m.getSubscriptions().forEach(s -> {

                    if (membersDTO.getSubscriptionPeriodIdFilter() == null || s.getSubscriptionPeriodId()
                            .equals(membersDTO.getSubscriptionPeriodIdFilter())) {

                        writeRecord(m, Optional.of(s), csvPrinter);
                    }

                });
            }
        });

        csvPrinter.flush();
        csvPrinter.close();
    }

    private void writeRecord(MemberDTO m, Optional<SubscriptionDTO> s, CSVPrinter csvPrinter) {
        try {

            ArrayList<String> record = new ArrayList<>();

            record.addAll(Arrays.asList(m.getId(), m.getLastNameOrCompanyName(), m.getFirstName(), m.getAddress(), //
                    s.map(SubscriptionDTO::getSubscriptionDisplayInfo).orElse(""),
                    s.map(SubscriptionDTO::getId).orElse(""),
                    s.map(SubscriptionDTO::getSubscriptionPeriodId).orElse(""),
                    s.map(SubscriptionDTO::getSubscriptionTypeId).orElse("")));

            csvPrinter.printRecord(record);

        } catch (IOException e) {
            throw new RuntimeException("failed to generate csv for record: " + m, e);
        }
    }
}
