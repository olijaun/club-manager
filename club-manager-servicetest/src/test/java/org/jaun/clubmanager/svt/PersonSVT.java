package org.jaun.clubmanager.svt;

import io.restassured.RestAssured;
import io.restassured.config.DecoderConfig;
import io.restassured.config.EncoderConfig;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.jaun.clubmanager.svt.SvtUtil.load;
import static org.jaun.clubmanager.svt.SvtUtil.waitForAssertionTrue;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Order(1) // has to be run before MemberSVT
public class PersonSVT {

    private TokenHelper tokenHelper = new TokenHelper();

    @Test
    @Order(1)
    void loadPerson() {

        given() //
                .header("Authorization", "Bearer " + tokenHelper.getTestToken()) //
                .header("Content-Type", "application/json") //
                .body("""
                        {
                           "type": "NATURAL",
                           "basicData": {
                             "name": {
                               "lastNameOrCompanyName": "Jaun",
                               "firstName": "Oliver"
                             },
                             "birthDate": "1979-01-22",
                             "gender": "MALE"
                           },
                           "streetAddress": {
                             "street": "Einestrasse",
                             "houseNumber": "71",
                             "zip": "3007",
                             "city": "Bern",
                             "isoCountryCode": "CH",
                             "state": null
                           },
                           "contactData": {
                             "phoneNumber": "078 111 11 11",
                             "emailAddress": "oliver@somewhere-bla.org"
                           }
                         }
                        """)
                .put("/api/persons/P00000005")
                .then()
                .assertThat()
                .statusCode(200);

        waitForAssertionTrue(() -> given() //
                .header("Authorization", "Bearer " + tokenHelper.getTestToken()) //
                .get("/api/persons/P00000005")
                .then()
                .assertThat()
                .statusCode(200)
                .body("type", equalTo("NATURAL"))
                .body("basicData.name.lastNameOrCompanyName", equalTo("Jaun"))
                .body("basicData.name.firstName", equalTo("Oliver"))
                .body("basicData.birthDate", equalTo("1979-01-22"))
                .body("basicData.gender", equalTo("MALE"))
                .body("streetAddress.street", equalTo("Einestrasse"))
                .body("streetAddress.houseNumber", equalTo("71"))
                .body("streetAddress.zip", equalTo("3007"))
                .body("streetAddress.city", equalTo("Bern"))
                .body("streetAddress.isoCountryCode", equalTo("CH"))
                .body("streetAddress.state", nullValue())
                .body("contactData.phoneNumber", equalTo("078 111 11 11"))
                .body("contactData.emailAddress", equalTo("oliver@somewhere-bla.org"))
        );
    }

    @Test
    @Order(2)
    void loadPersonWithoutAddress() {

        given() //
                .header("Authorization", "Bearer " + tokenHelper.getTestToken()) //
                .header("Content-Type", "application/json") //
                .body("""
                        {
                           "type": "NATURAL",
                           "basicData": {
                             "name": {
                               "lastNameOrCompanyName": "Häberli",
                               "firstName": "Françoise"
                             },
                             "birthDate": "1976-06-23",
                             "gender": "FEMALE"
                           },
                           "contactData": {
                             "phoneNumber": "078 222 22 22",
                             "emailAddress": "haeberli@somewhere-bla.org"
                           }
                         }
                        """)
                .put("/api/persons/P00000006")
                .then()
                .assertThat()
                .statusCode(200);

        waitForAssertionTrue(() -> given() //
                .header("Authorization", "Bearer " + tokenHelper.getTestToken()) //
                .get("/api/persons/P00000006")
                .then()
                .assertThat()
                .statusCode(200)
                .body("type", equalTo("NATURAL"))
                .body("basicData.name.lastNameOrCompanyName", equalTo("Häberli"))
                .body("basicData.name.firstName", equalTo("Françoise"))
                .body("basicData.birthDate", equalTo("1976-06-23"))
                .body("basicData.gender", equalTo("FEMALE"))
                .body("streetAddress", nullValue())
                .body("contactData.phoneNumber", equalTo("078 222 22 22"))
                .body("contactData.emailAddress", equalTo("haeberli@somewhere-bla.org"))
        );
    }

    @Test
    @Order(3)
    void queryPerson() {

        given() //
                .header("Authorization", "Bearer " + tokenHelper.getTestToken()) //
                .get("/api/persons?firstName=oliver&lastNameOrCompanyName=jaun")
                .then()
                .assertThat()
                .statusCode(200)
                .body("persons", hasSize(1));
    }

    @Test
    @Order(4)
    void csvExport() {

        given() //
                .header("Authorization", "Bearer " + tokenHelper.getTestToken()) //
                .header("Accept", "text/csv") //
                .get("/api/persons")
                .then()
                .assertThat()
                .body(equalTo(load("/personsExport.csv")));
    }

    @Test
    @Order(5)
    void csvImport() {
        System.out.println(load("/personsImport.csv"));
        given() //
                // leaving this config here for documentation. either this config and/or charset=UTF-8 in the Content-Type has to be specified in order to properly load an UTF-8 encoded csv
                .config(RestAssured.config().decoderConfig(DecoderConfig.decoderConfig().defaultContentCharset("UTF-8")).encoderConfig(EncoderConfig.encoderConfig().defaultContentCharset("UTF-8")))
                .header("Authorization", "Bearer " + tokenHelper.getTestToken()) //
                .header("Content-Type", "text/csv; charset=UTF-8") // charset is important
                .body(load("/personsImport.csv"))
                .post("/api/persons")
                .then()
                .assertThat()
                .statusCode(200);

        waitForAssertionTrue(() -> given() //
                .header("Authorization", "Bearer " + tokenHelper.getTestToken()) //
                .get("/api/persons/P00000007")
                .then()
                .assertThat()
                .statusCode(200)
                .body("type", equalTo("NATURAL"))
                .body("basicData.name.lastNameOrCompanyName", equalTo("Impört1"))
                .body("basicData.name.firstName", equalTo("Böb"))
                .body("basicData.birthDate", equalTo("1982-03-03"))
                .body("basicData.gender", equalTo("MALE"))
                .body("streetAddress.street", equalTo("Bobstreet"))
                .body("streetAddress.houseNumber", equalTo("88"))
                .body("streetAddress.zip", equalTo("3000"))
                .body("streetAddress.city", equalTo("Bern"))
                .body("streetAddress.isoCountryCode", equalTo("CH"))
                .body("streetAddress.state", nullValue())
                .body("contactData.phoneNumber", equalTo("078 333 33 33"))
                .body("contactData.emailAddress", equalTo("bob@somewhere-bla.org"))
        );

        waitForAssertionTrue(() -> given() //
                .header("Authorization", "Bearer " + tokenHelper.getTestToken()) //
                .get("/api/persons/P00000008")
                .then()
                .assertThat()
                .statusCode(200)
                .body("type", equalTo("NATURAL"))
                .body("basicData.name.lastNameOrCompanyName", equalTo("Impört2"))
                .body("basicData.name.firstName", equalTo("Älice"))
                .body("basicData.birthDate", equalTo("1972-05-07"))
                .body("basicData.gender", equalTo("FEMALE"))
                .body("streetAddress", nullValue())
                .body("contactData.phoneNumber", equalTo("078 444 44 44"))
                .body("contactData.emailAddress", equalTo("alice@somewhere-bla.org"))
        );
    }


}
