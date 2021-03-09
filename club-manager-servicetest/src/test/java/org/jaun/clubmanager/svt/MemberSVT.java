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
@Order(2) // has to be run after PersonSVT
public class MemberSVT {

    private TokenHelper tokenHelper = new TokenHelper();

    private void putMembershipType(int id, String name, String description) {
        given() //
                .header("Authorization", "Bearer " + tokenHelper.getTestToken()) //
                .header("Content-Type", "application/json") //
                .body(String.format("""
                        {
                        	"name": "%s",
                        	"description": "%s"
                        }
                        """, name, description))
                .put("/api/membership-types/" + id)
                .then()
                .assertThat()
                .statusCode(204);
    }

    @Test
    @Order(1)
    void loadMemberTypes() {

        putMembershipType(0, "Ehren", "Ehrenmitgliedschaft");
        putMembershipType(1, "Normal", "Normale Mitgliedschaft");
        putMembershipType(2, "Gönner", "Gönnermitgliedschaft");
        putMembershipType(3, "Passivmitglied", "Passivmitgliedschaft");
        putMembershipType(4, "Doppelgönner", "Doppelgönnermitgliedschaft");

        waitForAssertionTrue(() -> given() //
                .header("Authorization", "Bearer " + tokenHelper.getTestToken()) //
                .get("/api/membership-types")
                .then()
                .assertThat()
                .statusCode(200)
                .body("membershipTypes.findAll { it.id == '0' }.name", hasItem("Ehren"))
                .body("membershipTypes.findAll { it.id == '0' }.description", hasItem("Ehrenmitgliedschaft"))
                .body("membershipTypes.findAll { it.id == '1' }.name", hasItem("Normal"))
                .body("membershipTypes.findAll { it.id == '1' }.description", hasItem("Normale Mitgliedschaft"))
                .body("membershipTypes.findAll { it.id == '2' }.name", hasItem("Gönner"))
                .body("membershipTypes.findAll { it.id == '2' }.description", hasItem("Gönnermitgliedschaft"))
                .body("membershipTypes.findAll { it.id == '3' }.name", hasItem("Passivmitglied"))
                .body("membershipTypes.findAll { it.id == '3' }.description", hasItem("Passivmitgliedschaft"))
                .body("membershipTypes.findAll { it.id == '4' }.name", hasItem("Doppelgönner"))
                .body("membershipTypes.findAll { it.id == '4' }.description", hasItem("Doppelgönnermitgliedschaft"))
        );
    }

    private void putSubscriptionPeriod(int id, String startDate, String endDate, String name, String description) {
        given() //
                .header("Authorization", "Bearer " + tokenHelper.getTestToken()) //
                .header("Content-Type", "application/json") //
                .body(String.format("""
                        {
                         	 "startDate" : "%s",
                             "endDate": "%s",
                             "name": "%s",
                             "description": "%s"
                         }
                        """, startDate, endDate, name, description))
                .put("/api/subscription-periods/" + id)
                .then()
                .assertThat()
                .body(equalTo(String.valueOf(id)))
                .statusCode(200); // TODO: shouldn't this be 204
    }

    @Test
    @Order(2)
    void loadSubscriptionPeriod() {

        putSubscriptionPeriod(1, "2018-01-01", "2018-12-31", "Vereinsjahr 2018", "");
        putSubscriptionPeriod(2, "2019-01-01", "2019-12-31", "Vereinsjahr 2019", "");
        putSubscriptionPeriod(3, "2020-01-01", "2020-12-31", "Vereinsjahr 2020", "");
        putSubscriptionPeriod(4, "2021-01-01", "2021-12-31", "Vereinsjahr 2021", "");

        waitForAssertionTrue(() -> given() //
                .header("Authorization", "Bearer " + tokenHelper.getTestToken()) //
                .get("/api/subscription-periods")
                .then()
                .assertThat()
                .statusCode(200)
                .body("subscriptionPeriods.findAll { it.id == '1' }.startDate", hasItem("2018-01-01"))
                .body("subscriptionPeriods.findAll { it.id == '1' }.endDate", hasItem("2018-12-31"))
                .body("subscriptionPeriods.findAll { it.id == '1' }.name", hasItem("Vereinsjahr 2018"))
                .body("subscriptionPeriods.findAll { it.id == '1' }.description", hasItem(""))
                //
                .body("subscriptionPeriods.findAll { it.id == '2' }.startDate", hasItem("2019-01-01"))
                .body("subscriptionPeriods.findAll { it.id == '2' }.endDate", hasItem("2019-12-31"))
                .body("subscriptionPeriods.findAll { it.id == '2' }.name", hasItem("Vereinsjahr 2019"))
                .body("subscriptionPeriods.findAll { it.id == '2' }.description", hasItem(""))
                //
                .body("subscriptionPeriods.findAll { it.id == '3' }.startDate", hasItem("2020-01-01"))
                .body("subscriptionPeriods.findAll { it.id == '3' }.endDate", hasItem("2020-12-31"))
                .body("subscriptionPeriods.findAll { it.id == '3' }.name", hasItem("Vereinsjahr 2020"))
                .body("subscriptionPeriods.findAll { it.id == '3' }.description", hasItem(""))
                //
                .body("subscriptionPeriods.findAll { it.id == '4' }.startDate", hasItem("2021-01-01"))
                .body("subscriptionPeriods.findAll { it.id == '4' }.endDate", hasItem("2021-12-31"))
                .body("subscriptionPeriods.findAll { it.id == '4' }.name", hasItem("Vereinsjahr 2021"))
                .body("subscriptionPeriods.findAll { it.id == '4' }.description", hasItem(""))
        );
    }

    private void putSubscriptionPeriodType(int subscriptionPeriodId, int typeId, int membershipTypeId, String name, int amount, String currency, int maxSubscribers) {
        given() //
                .header("Authorization", "Bearer " + tokenHelper.getTestToken()) //
                .header("Content-Type", "application/json") //
                .body(String.format("""
                        {
                          	"membershipTypeId": "%s",
                          	"name": "%s",
                          	"amount": %d,
                          	"currency": "%s",
                          	"maxSubscribers": %d
                          }
                        """, membershipTypeId, name, amount, currency, maxSubscribers))
                .put("/api/subscription-periods/" + subscriptionPeriodId + "/types/" + typeId)
                .then()
                .assertThat()
                .body(equalTo(String.valueOf(subscriptionPeriodId))) // TODO: current return value doesn't really make sense
                .statusCode(200);
    }

    @Test
    @Order(3)
    void loadSubscriptionPeriodTypes() {

        putSubscriptionPeriodType(1, 0, 0, "Ehrenmitgliedschaft 2018", 0, "CHF", 1);
        putSubscriptionPeriodType(1, 1, 1, "Normalmitgliedschaft 2018", 90, "CHF", 1);
        putSubscriptionPeriodType(1, 2, 2, "Gönnermitgliedschaft 2018", 250, "CHF", 1);
        putSubscriptionPeriodType(1, 3, 3, "Passivmitgliedschaft 2018", 20, "CHF", 1);

        putSubscriptionPeriodType(2, 0, 0, "Ehrenmitgliedschaft 2019", 0, "CHF", 1);
        putSubscriptionPeriodType(2, 1, 1, "Normalmitgliedschaft 2019", 60, "CHF", 1);
        putSubscriptionPeriodType(2, 2, 2, "Gönnermitgliedschaft 2019", 200, "CHF", 1);
        putSubscriptionPeriodType(2, 3, 3, "Passivmitgliedschaft 2019", 20, "CHF", 1);
        putSubscriptionPeriodType(2, 4, 4, "Doppelgönnermitgliedschaft 2019", 300, "CHF", 1);

        putSubscriptionPeriodType(3, 0, 0, "Ehrenmitgliedschaft 2020", 0, "CHF", 1);
        putSubscriptionPeriodType(3, 1, 1, "Normalmitgliedschaft 2020", 60, "CHF", 1);
        putSubscriptionPeriodType(3, 2, 2, "Gönnermitgliedschaft 2020", 200, "CHF", 1);
        putSubscriptionPeriodType(3, 3, 3, "Passivmitgliedschaft 2020", 20, "CHF", 1);
        putSubscriptionPeriodType(3, 4, 4, "Doppelgönnermitgliedschaft 2020", 300, "CHF", 1);

        waitForAssertionTrue(() -> {
                    given() //
                            .header("Authorization", "Bearer " + tokenHelper.getTestToken()) //
                            .get("/api/subscription-periods/1")
                            .then()
                            .assertThat()
                            .statusCode(200)
                            .body("subscriptionTypes.find { it.membershipTypeId == '0' }.id", equalTo("0"))
                            .body("subscriptionTypes.find { it.membershipTypeId == '0' }.name", equalTo("Ehrenmitgliedschaft 2018"))
                            .body("subscriptionTypes.find { it.membershipTypeId == '0' }.amount", equalTo(0))
                            .body("subscriptionTypes.find { it.membershipTypeId == '0' }.currency", equalTo("CHF"))
                            .body("subscriptionTypes.find { it.membershipTypeId == '0' }.maxSubscribers", equalTo(1))
                            .body("subscriptionTypes.find { it.membershipTypeId == '0' }.subscriptionPeriodId", equalTo("1"))
                            //
                            .body("subscriptionTypes.find { it.membershipTypeId == '1' }.id", equalTo("1"))
                            .body("subscriptionTypes.find { it.membershipTypeId == '1' }.name", equalTo("Normalmitgliedschaft 2018"))
                            .body("subscriptionTypes.find { it.membershipTypeId == '1' }.amount", equalTo(90))
                            .body("subscriptionTypes.find { it.membershipTypeId == '1' }.currency", equalTo("CHF"))
                            .body("subscriptionTypes.find { it.membershipTypeId == '1' }.maxSubscribers", equalTo(1))
                            .body("subscriptionTypes.find { it.membershipTypeId == '1' }.subscriptionPeriodId", equalTo("1"))
                            //
                            .body("subscriptionTypes.find { it.membershipTypeId == '2' }.id", equalTo("2"))
                            .body("subscriptionTypes.find { it.membershipTypeId == '2' }.name", equalTo("Gönnermitgliedschaft 2018"))
                            .body("subscriptionTypes.find { it.membershipTypeId == '2' }.amount", equalTo(250))
                            .body("subscriptionTypes.find { it.membershipTypeId == '2' }.currency", equalTo("CHF"))
                            .body("subscriptionTypes.find { it.membershipTypeId == '2' }.maxSubscribers", equalTo(1))
                            .body("subscriptionTypes.find { it.membershipTypeId == '2' }.subscriptionPeriodId", equalTo("1"))
                            //
                            .body("subscriptionTypes.find { it.membershipTypeId == '3' }.id", equalTo("3"))
                            .body("subscriptionTypes.find { it.membershipTypeId == '3' }.name", equalTo("Passivmitgliedschaft 2018"))
                            .body("subscriptionTypes.find { it.membershipTypeId == '3' }.amount", equalTo(20))
                            .body("subscriptionTypes.find { it.membershipTypeId == '3' }.currency", equalTo("CHF"))
                            .body("subscriptionTypes.find { it.membershipTypeId == '3' }.maxSubscribers", equalTo(1))
                            .body("subscriptionTypes.find { it.membershipTypeId == '3' }.subscriptionPeriodId", equalTo("1"));

                    given() //
                            .header("Authorization", "Bearer " + tokenHelper.getTestToken()) //
                            .get("/api/subscription-periods/2")
                            .then()
                            .assertThat()
                            .statusCode(200)
                            .body("subscriptionTypes.find { it.membershipTypeId == '0' }.id", equalTo("0"))
                            .body("subscriptionTypes.find { it.membershipTypeId == '0' }.name", equalTo("Ehrenmitgliedschaft 2019"))
                            .body("subscriptionTypes.find { it.membershipTypeId == '0' }.amount", equalTo(0))
                            .body("subscriptionTypes.find { it.membershipTypeId == '0' }.currency", equalTo("CHF"))
                            .body("subscriptionTypes.find { it.membershipTypeId == '0' }.maxSubscribers", equalTo(1))
                            .body("subscriptionTypes.find { it.membershipTypeId == '0' }.subscriptionPeriodId", equalTo("2"))
                            //
                            .body("subscriptionTypes.find { it.membershipTypeId == '1' }.id", equalTo("1"))
                            .body("subscriptionTypes.find { it.membershipTypeId == '1' }.name", equalTo("Normalmitgliedschaft 2019"))
                            .body("subscriptionTypes.find { it.membershipTypeId == '1' }.amount", equalTo(60))
                            .body("subscriptionTypes.find { it.membershipTypeId == '1' }.currency", equalTo("CHF"))
                            .body("subscriptionTypes.find { it.membershipTypeId == '1' }.maxSubscribers", equalTo(1))
                            .body("subscriptionTypes.find { it.membershipTypeId == '1' }.subscriptionPeriodId", equalTo("2"))
                            //
                            .body("subscriptionTypes.find { it.membershipTypeId == '2' }.id", equalTo("2"))
                            .body("subscriptionTypes.find { it.membershipTypeId == '2' }.name", equalTo("Gönnermitgliedschaft 2019"))
                            .body("subscriptionTypes.find { it.membershipTypeId == '2' }.amount", equalTo(200))
                            .body("subscriptionTypes.find { it.membershipTypeId == '2' }.currency", equalTo("CHF"))
                            .body("subscriptionTypes.find { it.membershipTypeId == '2' }.maxSubscribers", equalTo(1))
                            .body("subscriptionTypes.find { it.membershipTypeId == '2' }.subscriptionPeriodId", equalTo("2"))
                            //
                            .body("subscriptionTypes.find { it.membershipTypeId == '3' }.id", equalTo("3"))
                            .body("subscriptionTypes.find { it.membershipTypeId == '3' }.name", equalTo("Passivmitgliedschaft 2019"))
                            .body("subscriptionTypes.find { it.membershipTypeId == '3' }.amount", equalTo(20))
                            .body("subscriptionTypes.find { it.membershipTypeId == '3' }.currency", equalTo("CHF"))
                            .body("subscriptionTypes.find { it.membershipTypeId == '3' }.maxSubscribers", equalTo(1))
                            .body("subscriptionTypes.find { it.membershipTypeId == '3' }.subscriptionPeriodId", equalTo("2"))
                            //
                            .body("subscriptionTypes.find { it.membershipTypeId == '4' }.id", equalTo("4"))
                            .body("subscriptionTypes.find { it.membershipTypeId == '4' }.name", equalTo("Doppelgönnermitgliedschaft 2019"))
                            .body("subscriptionTypes.find { it.membershipTypeId == '4' }.amount", equalTo(300))
                            .body("subscriptionTypes.find { it.membershipTypeId == '4' }.currency", equalTo("CHF"))
                            .body("subscriptionTypes.find { it.membershipTypeId == '4' }.maxSubscribers", equalTo(1))
                            .body("subscriptionTypes.find { it.membershipTypeId == '4' }.subscriptionPeriodId", equalTo("2"));

                    given() //
                            .header("Authorization", "Bearer " + tokenHelper.getTestToken()) //
                            .get("/api/subscription-periods/3")
                            .then()
                            .assertThat()
                            .statusCode(200)
                            .body("subscriptionTypes.find { it.membershipTypeId == '0' }.id", equalTo("0"))
                            .body("subscriptionTypes.find { it.membershipTypeId == '0' }.name", equalTo("Ehrenmitgliedschaft 2020"))
                            .body("subscriptionTypes.find { it.membershipTypeId == '0' }.amount", equalTo(0))
                            .body("subscriptionTypes.find { it.membershipTypeId == '0' }.currency", equalTo("CHF"))
                            .body("subscriptionTypes.find { it.membershipTypeId == '0' }.maxSubscribers", equalTo(1))
                            .body("subscriptionTypes.find { it.membershipTypeId == '0' }.subscriptionPeriodId", equalTo("3"))
                            //
                            .body("subscriptionTypes.find { it.membershipTypeId == '1' }.id", equalTo("1"))
                            .body("subscriptionTypes.find { it.membershipTypeId == '1' }.name", equalTo("Normalmitgliedschaft 2020"))
                            .body("subscriptionTypes.find { it.membershipTypeId == '1' }.amount", equalTo(60))
                            .body("subscriptionTypes.find { it.membershipTypeId == '1' }.currency", equalTo("CHF"))
                            .body("subscriptionTypes.find { it.membershipTypeId == '1' }.maxSubscribers", equalTo(1))
                            .body("subscriptionTypes.find { it.membershipTypeId == '1' }.subscriptionPeriodId", equalTo("3"))
                            //
                            .body("subscriptionTypes.find { it.membershipTypeId == '2' }.id", equalTo("2"))
                            .body("subscriptionTypes.find { it.membershipTypeId == '2' }.name", equalTo("Gönnermitgliedschaft 2020"))
                            .body("subscriptionTypes.find { it.membershipTypeId == '2' }.amount", equalTo(200))
                            .body("subscriptionTypes.find { it.membershipTypeId == '2' }.currency", equalTo("CHF"))
                            .body("subscriptionTypes.find { it.membershipTypeId == '2' }.maxSubscribers", equalTo(1))
                            .body("subscriptionTypes.find { it.membershipTypeId == '2' }.subscriptionPeriodId", equalTo("3"))
                            //
                            .body("subscriptionTypes.find { it.membershipTypeId == '3' }.id", equalTo("3"))
                            .body("subscriptionTypes.find { it.membershipTypeId == '3' }.name", equalTo("Passivmitgliedschaft 2020"))
                            .body("subscriptionTypes.find { it.membershipTypeId == '3' }.amount", equalTo(20))
                            .body("subscriptionTypes.find { it.membershipTypeId == '3' }.currency", equalTo("CHF"))
                            .body("subscriptionTypes.find { it.membershipTypeId == '3' }.maxSubscribers", equalTo(1))
                            .body("subscriptionTypes.find { it.membershipTypeId == '3' }.subscriptionPeriodId", equalTo("3"))
                            //
                            .body("subscriptionTypes.find { it.membershipTypeId == '4' }.id", equalTo("4"))
                            .body("subscriptionTypes.find { it.membershipTypeId == '4' }.name", equalTo("Doppelgönnermitgliedschaft 2020"))
                            .body("subscriptionTypes.find { it.membershipTypeId == '4' }.amount", equalTo(300))
                            .body("subscriptionTypes.find { it.membershipTypeId == '4' }.currency", equalTo("CHF"))
                            .body("subscriptionTypes.find { it.membershipTypeId == '4' }.maxSubscribers", equalTo(1))
                            .body("subscriptionTypes.find { it.membershipTypeId == '4' }.subscriptionPeriodId", equalTo("3"));

                }
        );
    }
}
