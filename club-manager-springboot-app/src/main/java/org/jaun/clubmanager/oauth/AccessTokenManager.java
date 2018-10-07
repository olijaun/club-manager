package org.jaun.clubmanager.oauth;

import java.io.InputStream;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class AccessTokenManager {

    private static final String REQUEST =
            "{\"client_id\":\"2Ggwn7qS7QqLaeX6M4K91bKXntfwYxXk\",\"client_secret\":\"yKTON-g1HxGIHvjymnONrwkoxWHZuLgE298kul6IH7dGnyly1ChLXyI0_0TxFTj_\",\"audience\":\"https://member-manager.jaun.org\",\"grant_type\":\"client_credentials\",\"scope\": \"m2m\"}";
    private final WebTarget target;
    private String accessToken;
    private long expiration = 0;

    public AccessTokenManager() {
        target = ClientBuilder.newClient().target("https://jaun.eu.auth0.com/oauth/token");
    }

    public String getBearerToken() {

        if(System.currentTimeMillis() < expiration) {
            return accessToken;
        }

        InputStream jsonString = target.request().post(Entity.json(REQUEST), InputStream.class);
        JsonReader reader = Json.createReader(jsonString);
        JsonObject jsonObject = reader.readObject();
        this.accessToken = jsonObject.getString("access_token");
        this.expiration = System.currentTimeMillis() + (Long.valueOf(jsonObject.getInt("expires_in")) * 1000) - 60 * 2 * 1000;

        return accessToken;
    }
}
