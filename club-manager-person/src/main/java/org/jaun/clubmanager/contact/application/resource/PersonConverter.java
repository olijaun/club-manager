package org.jaun.clubmanager.contact.application.resource;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Currency;
import java.util.stream.Collectors;

import org.jaun.clubmanager.contact.domain.model.contact.Country;
import org.jaun.clubmanager.contact.domain.model.contact.EmailAddress;
import org.jaun.clubmanager.contact.domain.model.contact.Name;
import org.jaun.clubmanager.contact.domain.model.contact.Person;
import org.jaun.clubmanager.contact.domain.model.contact.PersonId;
import org.jaun.clubmanager.contact.domain.model.contact.PersonType;
import org.jaun.clubmanager.contact.domain.model.contact.PhoneNumber;
import org.jaun.clubmanager.contact.domain.model.contact.Sex;
import org.jaun.clubmanager.contact.domain.model.contact.StreetAddress;

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
        basicDataDTO.setBirthDate(in.getBirthDate().orElse(null));
        basicDataDTO.setSex(in.getSex().map(PersonConverter::toSexAsString).orElse(null));

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

    public static String toSexAsString(Sex sex) {
        switch (sex) {
            case MALE:
                return "MALE";
            case FEMALE:
                return "FEMALE";
            default:
                throw new IllegalArgumentException("unknown sex: " + sex);
        }
    }

    public static Sex toSex(String sex) {
        switch (sex) {
            case "MALE":
                return Sex.MALE;
            case "FEMALE":
                return Sex.FEMALE;
            default:
                throw new IllegalArgumentException("invalid sex: " + sex);
        }
    }

    public static StreetAddressDTO toAddressDTO(StreetAddress streetAddress) {
        if (streetAddress == null) {
            return null;
        }
        StreetAddressDTO streetAddressDTO = new StreetAddressDTO();
        streetAddressDTO.setCity(streetAddress.getCity());
        streetAddressDTO.setIsoCountryCode(streetAddress.getCountry().getIsoCountryCode());
        streetAddressDTO.setStreet(streetAddress.getStreet());
        streetAddressDTO.setStreetNumber(streetAddress.getStreetNumber());
        streetAddressDTO.setZip(streetAddress.getZip());
        streetAddressDTO.setState(streetAddress.getState());
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
                .streetNumber(in.getStreetNumber())
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
        Sex sex = basicDataDTO.getSex() == null ? null : Sex.valueOf(basicDataDTO.getSex());

        Person person = new Person(contractId, personType, name, basicDataDTO.getBirthDate(), sex);

        person.changeStreetAddress(toStreetAddress(in.getStreetAddress()));

        if (in.getContactData() != null) {
            ContactDataDTO contactDataDTO = in.getContactData();
            person.changeContactData(
                    contactDataDTO.getEmailAddress() == null ? null : new EmailAddress(contactDataDTO.getEmailAddress()),
                    contactDataDTO.getPhoneNumber() == null ? null : new PhoneNumber(contactDataDTO.getPhoneNumber()));
        }

        return person;
    }

    public static Name toName(CreatePersonDTO in) {

        if(in.getBasicData() == null) {
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
        personsDTO.setContacts(people.stream().map(PersonConverter::toPersonDTO).collect(Collectors.toList()));
        return personsDTO;
    }

    public static Currency toCurrency(String currencyCode) {
        if(currencyCode == null) {
            return null;
        }
        return Currency.getInstance(currencyCode);
    }

    public static LocalDate toLocalDate(String birthDateAsString) {
        if(birthDateAsString == null) {
            return null;
        }
        return LocalDate.parse(birthDateAsString);
    }

    public static PhoneNumber toPhoneNumber(String phoneNumberAsString) {
        if(phoneNumberAsString == null) {
            return null;
        }
        return new PhoneNumber(phoneNumberAsString);
    }

    public static EmailAddress toEmailAddress(String emailAddressAsString) {
        if(emailAddressAsString == null) {
            return null;
        }
        return new EmailAddress(emailAddressAsString);
    }
}
