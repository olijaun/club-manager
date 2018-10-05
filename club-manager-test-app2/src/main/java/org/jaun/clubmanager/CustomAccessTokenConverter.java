package org.jaun.clubmanager;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.DefaultAccessTokenConverter;
import org.springframework.stereotype.Component;

@Component
public class CustomAccessTokenConverter extends DefaultAccessTokenConverter {

    @Override
    public OAuth2Authentication extractAuthentication(Map<String, ?> claims) {
        HashMap<String, Object> map = new HashMap<>(claims);
        map.put("authorities", Arrays.asList("ROLE_OLI"));
        OAuth2Authentication authentication = super.extractAuthentication(map);
        authentication.setDetails(claims);
        return authentication;
    }
}

