package org.jaun.clubmanager.svt;

import io.restassured.response.Response;
import org.junit.jupiter.api.Test;


import static io.restassured.RestAssured.*;
import static io.restassured.matcher.RestAssuredMatchers.*;
import static org.hamcrest.Matchers.*;

public class MemberSVT {

    @Test
    void loadMemberTypes() {

        Response response = get("/api/membership-types");

        System.out.println(response.getBody().prettyPrint());
    }

}
