package org.jaun.clubmanager.oauth;

import java.io.IOException;

import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;

public class BearerTokenFilter implements ClientRequestFilter {

    private final AccessTokenManager accessTokenManager;

    public BearerTokenFilter(AccessTokenManager accessTokenManager) {
        this.accessTokenManager = accessTokenManager;
    }

    @Override
    public void filter(ClientRequestContext ctx) throws IOException {

        String bearerToken = this.accessTokenManager.getBearerToken();

        // modify header before send: ctx.getHeaders()
        ctx.getHeaders().add("Authorization", "Bearer " + bearerToken);
    }
}
