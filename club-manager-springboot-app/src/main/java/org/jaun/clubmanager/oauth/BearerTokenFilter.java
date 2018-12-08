package org.jaun.clubmanager.oauth;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;

import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import java.io.IOException;

public class BearerTokenFilter implements ClientRequestFilter {

    private final AccessTokenManager accessTokenManager;

    public BearerTokenFilter(AccessTokenManager accessTokenManager) {
        // TODO: actually no needed anymore because we propagete the callers bearer token and not a machine to machine token
        this.accessTokenManager = accessTokenManager;
    }

    @Override
    public void filter(ClientRequestContext ctx) throws IOException {

        // get the token and propagate it as bearer token
        OAuth2Authentication auth = (OAuth2Authentication) SecurityContextHolder.getContext().getAuthentication();
        OAuth2AuthenticationDetails details = (OAuth2AuthenticationDetails) auth.getDetails();

        //String bearerToken = this.accessTokenManager.getBearerToken();
        String bearerToken = details.getTokenValue();

        // modify header before send: ctx.getHeaders()
        ctx.getHeaders().add("Authorization", "Bearer " + bearerToken);
    }
}
