package org.jaun.clubmanager.member.application.resource;

import org.apache.commons.csv.CSVFormat;

public final class MemberCsvFormat {

    public final static String ID = "id";
    public final static String LAST_NAME = "lastName";
    public final static String FIRST_NAME = "firstName";
    public final static String ADDRESS = "address";
    public final static String SUBSCRIPTION_SUMMARY = "subscriptionSummary";
    public final static String SUBSCRIPTION_ID = "subscriptionId";
    public final static String SUBSCRIPTION_PERIOD_ID = "subscriptionPeriodId";
    public final static String SUBSCRIPTION_TYPE_ID = "subscriptionTypeId";

    public static CSVFormat FORMAT = CSVFormat.DEFAULT.withHeader( //
            ID, LAST_NAME, FIRST_NAME, ADDRESS, //
            SUBSCRIPTION_SUMMARY, SUBSCRIPTION_ID, //
            SUBSCRIPTION_PERIOD_ID, SUBSCRIPTION_TYPE_ID);
}
