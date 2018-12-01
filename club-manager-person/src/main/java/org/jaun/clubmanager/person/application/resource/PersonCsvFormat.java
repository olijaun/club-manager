package org.jaun.clubmanager.person.application.resource;

import org.apache.commons.csv.CSVFormat;

public final class PersonCsvFormat {

    public final static String ID = "id";
    public final static String TYPE = "type";
    public final static String LAST_NAME = "lastName";
    public final static String FIRST_NAME = "firstName";
    public final static String BIRTH_DATE = "birthDate";
    public final static String GENDER = "gender";
    public final static String EMAIL_ADDRESS = "emailAddress";
    public final static String PHONE_NUMBER = "phoneNumber";
    public final static String STREET = "street";
    public final static String HOUSE_NUMBER = "houseNumber";
    public final static String ZIP = "zip";
    public final static String CITY = "city";
    public final static String ISO_COUNTRY_CODE = "isoCountryCode";
    public final static String STATE = "state";

    public static CSVFormat FORMAT = CSVFormat.DEFAULT.withHeader( //
            ID, TYPE, LAST_NAME, FIRST_NAME, BIRTH_DATE, GENDER, //
            EMAIL_ADDRESS, PHONE_NUMBER, //
            STREET, HOUSE_NUMBER, ZIP, CITY, ISO_COUNTRY_CODE, STATE);
}
