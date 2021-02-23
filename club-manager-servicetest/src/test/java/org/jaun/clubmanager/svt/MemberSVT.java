package org.jaun.clubmanager.svt;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.hasItem;
import static org.jaun.clubmanager.svt.SvtUtil.waitForNoException;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
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

        waitForNoException(() -> given() //
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
                .statusCode(200); // TODO: shouldn't this be 204
    }

    @Test
    @Order(2)
    void loadSubscriptionPeriod() {

        putSubscriptionPeriod(1, "2018-01-01", "2018-12-31", "Vereinsjahr 2018", "");
        putSubscriptionPeriod(2, "2019-01-01", "2019-12-31", "Vereinsjahr 2019", "");
        putSubscriptionPeriod(3, "2020-01-01", "2020-12-31", "Vereinsjahr 2020", "");
        putSubscriptionPeriod(4, "2021-01-01", "2021-12-31", "Vereinsjahr 2021", "");

        waitForNoException(() -> given() //
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
}
