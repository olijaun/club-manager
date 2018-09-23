package org.jaun.clubmanager.oauth;

import org.springframework.beans.factory.annotation.Value;

public class OAuthProperties {

    @Value("${security.oauth2.client.clientId}")
    private String clientId;
    @Value("${security.oauth2.client.clientSecret}")
    private String clientSecret;
    @Value("${security.oauth2.resource.tokenInfoUri}")
    private String checkTokenUrl;
    @Value("${security.oauth2.resource.userInfoUri}")
    private String userInfoUrl;
    @Value("${security.oauth2.resource.param:token}")
    private String param;

    public String getClientId() {
        return clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public String getCheckTokenUrl() {
        return checkTokenUrl;
    }

    public String getUserInfoUrl() {
        return userInfoUrl;
    }

    public String getParam() {
        return param;
    }
}
