package org.jaun.clubmanager.oauth;

import java.io.InputStream;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;

public class AccessTokenManager {

    private final WebTarget target;
    private final String clientId;
    private final String clientSecret;
    private final String audience;
    private final String grantType;
    private final String scope;
    private String accessToken;
    private long expiration = 0;

    public AccessTokenManager(String url, String clientId, String clientSecret, String audience, String grantType, String scope) {

        target = ClientBuilder.newClient().target(url);

        System.out.printf("myurl: " + url);

        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.audience = audience;
        this.grantType = grantType;
        this.scope = scope;
    }

    public String getBearerToken() {

        if (System.currentTimeMillis() < expiration) {
            return accessToken;
        }

        JsonObject requestObject = Json.createObjectBuilder()
                .add("client_id", clientId)
                .add("client_secret", clientSecret)
                .add("audience", audience)
                .add("grant_type", grantType)
                .add("scope", scope)
                .build();

        System.out.println();
        InputStream jsonString = target.request().post(Entity.json(requestObject.toString()), InputStream.class);
        System.out.println("request Object: " + requestObject.toString());

        JsonReader reader = Json.createReader(jsonString);
        JsonObject jsonObject = reader.readObject();
        this.accessToken = jsonObject.getString("access_token");
        this.expiration = System.currentTimeMillis() + (Long.valueOf(jsonObject.getInt("expires_in")) * 1000) - 60 * 2 * 1000;

        return accessToken;
    }
}
