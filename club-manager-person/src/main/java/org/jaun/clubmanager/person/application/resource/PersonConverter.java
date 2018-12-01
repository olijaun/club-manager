package org.jaun.clubmanager.person.application.resource;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Currency;
import java.util.stream.Collectors;

import org.jaun.clubmanager.person.domain.model.person.Country;
import org.jaun.clubmanager.person.domain.model.person.EmailAddress;
import org.jaun.clubmanager.person.domain.model.person.Gender;
import org.jaun.clubmanager.person.domain.model.person.Name;
import org.jaun.clubmanager.person.domain.model.person.Person;
import org.jaun.clubmanager.person.domain.model.person.PersonId;
import org.jaun.clubmanager.person.domain.model.person.PersonType;
import org.jaun.clubmanager.person.domain.model.person.PhoneNumber;
import org.jaun.clubmanager.person.domain.model.person.StreetAddress;

public class PersonConverter {

    public static PersonDTO toPersonDTO(Person in) {
        if (in == null) {
            return null;
        }
        PersonDTO out = new PersonDTO();
        out.setId(in.getId().getValue());
        out.setBasicData(toBasicDataDTO(in));
        out.setStreetAddress(toAddressDTO(in.getStreetAddress()));
        out.setContactData(toContactDataDTO(in));

        return out;
    }

    private static BasicDataDTO toBasicDataDTO(Person in) {
        BasicDataDTO basicDataDTO = new BasicDataDTO();
        basicDataDTO.setName(toNameDTO(in.getName()));
        basicDataDTO.setBirthDate(in.getBirthDate().map(PersonConverter::toDateString).orElse(null));
        basicDataDTO.setGender(in.getGender().map(PersonConverter::toGenderAsString).orElse(null));

        return basicDataDTO;
    }

    private static ContactDataDTO toContactDataDTO(Person in) {
        ContactDataDTO contactDataDTO = new ContactDataDTO();
        contactDataDTO.setEmailAddress(in.getEmailAddress().map(EmailAddress::getValue).orElse(null));
        contactDataDTO.setPhoneNumber(in.getPhoneNumber().map(PhoneNumber::getValue).orElse(null));
        return contactDataDTO;
    }

    public static NameDTO toNameDTO(Name name) {
        NameDTO nameDTO = new NameDTO();
        nameDTO.setFirstName(name.getFirstName().orElse(null));
        nameDTO.setLastNameOrCompanyName(name.getLastNameOrCompanyName());
        return nameDTO;
    }

    public static String toGenderAsString(Gender gender) {
        switch (gender) {
            case MALE:
                return "MALE";
            case FEMALE:
                return "FEMALE";
            default:
                throw new IllegalArgumentException("unknown gender: " + gender);
        }
    }

    public static Gender toGender(String gender) {
        switch (gender) {
            case "MALE":
                return Gender.MALE;
            case "FEMALE":
                return Gender.FEMALE;
            default:
                throw new IllegalArgumentException("invalid gender: " + gender);
        }
    }

    public static StreetAddressDTO toAddressDTO(StreetAddress streetAddress) {
        if (streetAddress == null) {
            return null;
        }
        StreetAddressDTO streetAddressDTO = new StreetAddressDTO();
        streetAddressDTO.setCity(streetAddress.getCity());
        if (streetAddress.getCountry() != null) {
            streetAddressDTO.setIsoCountryCode(streetAddress.getCountry().getIsoCountryCode());
        }
        streetAddressDTO.setStreet(streetAddress.getStreet());
        streetAddressDTO.setHouseNumber(streetAddress.getHouseNumber().orElse(null));
        streetAddressDTO.setZip(streetAddress.getZip());
        streetAddressDTO.setState(streetAddress.getState().orElse(null));
        return streetAddressDTO;
    }

    public static StreetAddress toStreetAddress(StreetAddressDTO in) {
        if (in == null) {
            return null;
        }
        return StreetAddress.builder()
                .city(in.getCity())
                .country(new Country(in.getIsoCountryCode()))
                .street(in.getStreet())
                .houseNumber(in.getHouseNumber())
                .zip(in.getZip())
                .state(in.getState())
                .build();
    }

    public static Person toPerson(PersonId contractId, CreatePersonDTO in) {
        if (in == null) {
            return null;
        }
        PersonType personType = toPersonType(in);

        BasicDataDTO basicDataDTO = in.getBasicData();
        Name name = toName(basicDataDTO.getName());
        Gender gender = basicDataDTO.getGender() == null ? null : Gender.valueOf(basicDataDTO.getGender());
        Person person = new Person(contractId, personType, name, toLocalDate(basicDataDTO.getBirthDate()), gender);

        person.changeStreetAddress(toStreetAddress(in.getStreetAddress()));
        person.changeBasicData(name, toLocalDate(basicDataDTO.getBirthDate()), gender);

        if (in.getContactData() != null) {
            ContactDataDTO contactDataDTO = in.getContactData();
            person.changeContactData(
                    contactDataDTO.getEmailAddress() == null ? null : new EmailAddress(contactDataDTO.getEmailAddress()),
                    contactDataDTO.getPhoneNumber() == null ? null : new PhoneNumber(contactDataDTO.getPhoneNumber()));
        }

        return person;
    }

    public static String toDateString(LocalDate localDate) {
        if (localDate == null) {
            return null;
        }
        return localDate.format(DateTimeFormatter.ISO_DATE);
    }

    public static Name toName(CreatePersonDTO in) {

        if (in.getBasicData() == null) {
            return null;
        }
        return toName(in.getBasicData().getName());
    }

    public static Name toName(NameDTO in) {
        return new Name(in.getLastNameOrCompanyName(), in.getFirstName());
    }

    public static PersonType toPersonType(CreatePersonDTO contactDTO) {
        switch (contactDTO.getType()) {
            case "NATURAL":
                return PersonType.NATURAL;
            case "JURISTIC":
                return PersonType.JURISTIC;
            default:
                throw new IllegalArgumentException("unknown contact type");
        }
    }

    public static PersonsDTO toContactsDTO(Collection<Person> people) {
        PersonsDTO personsDTO = new PersonsDTO();
        personsDTO.setPersons(people.stream().map(PersonConverter::toPersonDTO).collect(Collectors.toList()));
        return personsDTO;
    }

    public static Currency toCurrency(String currencyCode) {
        if (currencyCode == null) {
            return null;
        }
        return Currency.getInstance(currencyCode);
    }

    public static LocalDate toLocalDate(String birthDateAsString) {
        if (birthDateAsString == null) {
            return null;
        }
        return LocalDate.parse(birthDateAsString);
    }

    public static PhoneNumber toPhoneNumber(String phoneNumberAsString) {
        if (phoneNumberAsString == null) {
            return null;
        }
        return new PhoneNumber(phoneNumberAsString);
    }

    public static EmailAddress toEmailAddress(String emailAddressAsString) {
        if (emailAddressAsString == null) {
            return null;
        }
        return new EmailAddress(emailAddressAsString);
    }
}
