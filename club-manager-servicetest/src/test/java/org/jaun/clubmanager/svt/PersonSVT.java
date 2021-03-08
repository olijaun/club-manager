package org.jaun.clubmanager.svt;

import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.jaun.clubmanager.svt.SvtUtil.waitForAssertionTrue;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
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
    void csv() {

        String body = given() //
                .header("Authorization", "Bearer " + tokenHelper.getTestToken()) //
                .header("Accept", "text/csv") //
                .get("/api/persons")
                .body()
                .print();

        assertThat(body, equalTo(load("/persons.csv")));
    }

    private String load(String resourcePath) {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            getClass().getResourceAsStream(resourcePath).transferTo(bos);
            return new String(bos.toByteArray(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new IllegalStateException("could not load test resource " + resourcePath, e);
        }
    }
}
