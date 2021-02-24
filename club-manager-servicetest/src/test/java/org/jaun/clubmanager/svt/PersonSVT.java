package org.jaun.clubmanager.svt;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
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
                             "birthDate": "1979-01-21",
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
                .body("basicData.name.firstName", equalTo("Oliver"))
        );
    }
}
