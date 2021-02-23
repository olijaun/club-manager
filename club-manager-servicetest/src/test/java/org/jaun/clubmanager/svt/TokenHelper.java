package org.jaun.clubmanager.svt;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import static io.restassured.RestAssured.given;

public final class TokenHelper {

    private String token;

    public String getTestToken() {

        if (token != null) {
            return token;
        }

        String filename = "/svt-secret.properties";

        try (InputStream is = MemberSVT.class.getResourceAsStream(filename)) {
            Properties p = new Properties();

            p.load(is);

            String tokenUrl = p.getProperty("token_url");
            String clientId = p.getProperty("client_id");
            String clientSecret = p.getProperty("client_secret");
            String audience = p.getProperty("audience");
            String grantType = p.getProperty("grant_type");

            token = given() //
                    .header("content-type", "application/json") //
                    .body("{\"client_id\":\"" + clientId + "\",\"client_secret\":\"" + clientSecret + "\",\"audience\":\"" + audience + "\",\"grant_type\":\"" + grantType + "\"}") //
                    .post(tokenUrl) //
                    .jsonPath().get("access_token");

            return token;
        } catch (IOException e) {
            throw new IllegalStateException("""
                    could not load property file. 
                    you have to create it because it contains secret information on how to get a token 
                    that cannot be checked in: """ + filename);
        }
    }
}
