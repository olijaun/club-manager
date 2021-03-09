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
@Order(3) // has to be run after MemberSVT
public class MembershipSVT {

    private TokenHelper tokenHelper = new TokenHelper();

    @Test
    @Order(1)
    void createMemberWithoutSubscription() {

        given() //
                .header("Authorization", "Bearer " + tokenHelper.getTestToken()) //
                .header("Content-Type", "application/json") //
                .body("{}")
                .put("/api/members/P00000007")
                .then()
                .assertThat()
                .statusCode(200); // TODO: shouldn't this be 204

        waitForAssertionTrue(() -> given() //
                .header("Authorization", "Bearer " + tokenHelper.getTestToken()) //
                .get("/api/members")
                .then()
                .assertThat()
                .statusCode(200)
                .body("subscriptionPeriodIdFilter", nullValue())
                .body("members.find { it.id == 'P00000007' }.subscriptions", hasSize(0))
                .body("members.find { it.id == 'P00000007' }.firstName", equalTo("Böb"))
                .body("members.find { it.id == 'P00000007' }.lastNameOrCompanyName", equalTo("Impört1"))
                .body("members.find { it.id == 'P00000007' }.address", equalTo("Bobstreet 88, 3000 Bern"))
        );
    }

    @Test
    @Order(2)
    void createMemberWithSubscription() {

        given() //
                .header("Authorization", "Bearer " + tokenHelper.getTestToken()) //
                .header("Content-Type", "application/json") //
                .body("""
                        {
                        	"subscriptions": [
                                {
                                    "subscriptionPeriodId": "1",
                                    "subscriptionTypeId": "1",
                                    "id": "1"
                                },
                                {
                                    "subscriptionPeriodId": "2",
                                    "subscriptionTypeId": "2",
                                    "id": "2"
                                }
                            ]
                        }""")
                .put("/api/members/P00000008")
                .then()
                .assertThat()
                .statusCode(200); // TODO: shouldn't this be 204

        waitForAssertionTrue(() -> given() //
                .header("Authorization", "Bearer " + tokenHelper.getTestToken()) //
                .get("/api/members/P00000008")
                .then()
                .assertThat()
                .statusCode(200)
                .body("subscriptions", hasSize(2))
                // check subscription 1
                .body("subscriptions.find { it.id == '1' }.subscriptionPeriodId", equalTo("1"))
                .body("subscriptions.find { it.id == '1' }.subscriptionTypeId", equalTo("1"))
                .body("subscriptions.find { it.id == '1' }.id", equalTo("1"))
                .body("subscriptions.find { it.id == '1' }.memberId", equalTo("P00000008"))
                .body("subscriptions.find { it.id == '1' }.subscriptionDisplayInfo", equalTo("Vereinsjahr 2018 / Normalmitgliedschaft 2018"))
                // check subscription 2
                .body("subscriptions.find { it.id == '2' }.subscriptionPeriodId", equalTo("2"))
                .body("subscriptions.find { it.id == '2' }.subscriptionTypeId", equalTo("2"))
                .body("subscriptions.find { it.id == '2' }.id", equalTo("2"))
                .body("subscriptions.find { it.id == '2' }.memberId", equalTo("P00000008"))
                .body("subscriptions.find { it.id == '2' }.subscriptionDisplayInfo", equalTo("Vereinsjahr 2019 / Gönnermitgliedschaft 2019"))
                // check member
                .body("firstName", equalTo("Älice"))
                .body("lastNameOrCompanyName", equalTo("Impört2"))
                .body("address", nullValue())
        );
    }

    @Test
    @Order(3)
    void updateMemberWithSubscriptionOnMember() {

        // TODO: this rest operation should be removed and only PUT/POST /members/{id}/subscriptions should be used. but for the mean time this test is needed

        given() //
                .header("Authorization", "Bearer " + tokenHelper.getTestToken()) //
                .header("Content-Type", "application/json") //
                .body("""
                        {
                        	"subscriptions": [
                                {
                                    "subscriptionPeriodId": "2",
                                    "subscriptionTypeId": "2",
                                    "id": "1"
                                },
                                {
                                    "subscriptionPeriodId": "3",
                                    "subscriptionTypeId": "3",
                                    "id": "2"
                                }
                            ]
                        }""")
                .put("/api/members/P00000007")
                .then()
                .assertThat()
                .statusCode(200); // TODO: shouldn't this be 204

        waitForAssertionTrue(() -> given() //
                .header("Authorization", "Bearer " + tokenHelper.getTestToken()) //
                .get("/api/members/P00000007")
                .then()
                .assertThat()
                .statusCode(200)
                .body("subscriptions", hasSize(2))
                // check subscription 1
                .body("subscriptions.find { it.id == '1' }.subscriptionPeriodId", equalTo("2"))
                .body("subscriptions.find { it.id == '1' }.subscriptionTypeId", equalTo("2"))
                .body("subscriptions.find { it.id == '1' }.memberId", equalTo("P00000007"))
                .body("subscriptions.find { it.id == '1' }.subscriptionDisplayInfo", equalTo("Vereinsjahr 2019 / Gönnermitgliedschaft 2019"))
                // check subscription 2
                .body("subscriptions.find { it.id == '2' }.subscriptionPeriodId", equalTo("3"))
                .body("subscriptions.find { it.id == '2' }.subscriptionTypeId", equalTo("3"))
                .body("subscriptions.find { it.id == '2' }.memberId", equalTo("P00000007"))
                .body("subscriptions.find { it.id == '2' }.subscriptionDisplayInfo", equalTo("Vereinsjahr 2020 / Passivmitgliedschaft 2020"))
                // check member
                .body("firstName", equalTo("Böb"))
                .body("lastNameOrCompanyName", equalTo("Impört1"))
                .body("address", equalTo("Bobstreet 88, 3000 Bern"))
        );
    }

    @Test
    @Order(4)
    void deleteSubscription() {

        given() //
                .header("Authorization", "Bearer " + tokenHelper.getTestToken()) //
                .header("Content-Type", "application/json") //
                .delete("/api/members/P00000007/subscriptions/1")
                .then()
                .assertThat()
                .statusCode(200); // TODO: shouldn't this be 204

        waitForAssertionTrue(() -> given() //
                .header("Authorization", "Bearer " + tokenHelper.getTestToken()) //
                .get("/api/members/P00000007")
                .then()
                .assertThat()
                .statusCode(200)
                .body("subscriptions", hasSize(1))
        );
    }

    // TODO: service not implemented yet
//    @Test
//    @Order(5)
//    void updateMemberWithSubscription() {
//
//        given() //
//                .header("Authorization", "Bearer " + tokenHelper.getTestToken()) //
//                .header("Content-Type", "application/json") //
//                .body("""
//                        {
//                        	"subscriptions": [
//                                {
//                                    "subscriptionPeriodId": "3",
//                                    "subscriptionTypeId": "3"
//                                }
//                            ]
//                        }""")
//                .put("/api/members/P00000008/subscriptions/2")
//                .then()
//                .assertThat()
//                .statusCode(200); // TODO: shouldn't this be 204
//
//    }

    @Test
    @Order(6)
    void csvExport() {

        given() //
                .header("Authorization", "Bearer " + tokenHelper.getTestToken()) //
                .header("Accept", "text/csv") //
                .get("/api/members")
                .then()
                .assertThat()
                .body(equalTo(load("/membersExport.csv")));
    }

    @Test
    @Order(7)
    void csvImport() {
        System.out.println(load("/membersImport.csv"));
        given() //
                // leaving this config here for documentation. either this config and/or charset=UTF-8 in the Content-Type has to be specified in order to properly load an UTF-8 encoded csv
                .config(RestAssured.config().decoderConfig(DecoderConfig.decoderConfig().defaultContentCharset("UTF-8")).encoderConfig(EncoderConfig.encoderConfig().defaultContentCharset("UTF-8")))
                .header("Authorization", "Bearer " + tokenHelper.getTestToken()) //
                .header("Content-Type", "text/csv; charset=UTF-8") // charset is important
                .body(load("/membersImport.csv"))
                .post("/api/members")
                .then()
                .assertThat()
                .statusCode(200);

        waitForAssertionTrue(() -> given() //
                .header("Authorization", "Bearer " + tokenHelper.getTestToken()) //
                .get("/api/members/P00000005")
                .then()
                .assertThat()
                .statusCode(200)
                .body("id", equalTo("P00000005"))
                .body("lastNameOrCompanyName", equalTo("Jaun"))
                .body("firstName", equalTo("Oliver"))
                .body("address", equalTo("Einestrasse 71, 3007 Bern"))
                .body("subscriptions", hasSize(2))
                // subscription 1
                .body("subscriptions.find { it.id == '1' }.subscriptionPeriodId", equalTo("1"))
                .body("subscriptions.find { it.id == '1' }.subscriptionTypeId", equalTo("1"))
                .body("subscriptions.find { it.id == '1' }.memberId", equalTo("P00000005"))
                .body("subscriptions.find { it.id == '1' }.subscriptionDisplayInfo", equalTo("Vereinsjahr 2018 / Normalmitgliedschaft 2018"))
                // subscription 2
                .body("subscriptions.find { it.id == '2' }.subscriptionPeriodId", equalTo("2"))
                .body("subscriptions.find { it.id == '2' }.subscriptionTypeId", equalTo("2"))
                .body("subscriptions.find { it.id == '2' }.memberId", equalTo("P00000005"))
                .body("subscriptions.find { it.id == '2' }.subscriptionDisplayInfo", equalTo("Vereinsjahr 2019 / Gönnermitgliedschaft 2019"))
        );

        waitForAssertionTrue(() -> given() //
                .header("Authorization", "Bearer " + tokenHelper.getTestToken()) //
                .get("/api/members/P00000006")
                .then()
                .assertThat()
                .statusCode(200)
                .body("id", equalTo("P00000006"))
                .body("lastNameOrCompanyName", equalTo("Häberli"))
                .body("firstName", equalTo("Françoise"))
                .body("address", nullValue())
                .body("subscriptions", hasSize(1))
                // subscription 1
                .body("subscriptions.find { it.id == '2' }.subscriptionPeriodId", equalTo("3"))
                .body("subscriptions.find { it.id == '2' }.subscriptionTypeId", equalTo("3"))
                .body("subscriptions.find { it.id == '2' }.memberId", equalTo("P00000006"))
                .body("subscriptions.find { it.id == '2' }.subscriptionDisplayInfo", equalTo("Vereinsjahr 2020 / Passivmitgliedschaft 2020"))
        );
    }
}
