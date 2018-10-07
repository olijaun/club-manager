package org.jaun.clubmanager.oauth;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.DefaultAccessTokenConverter;
import org.springframework.stereotype.Component;

@Component
public class CustomAccessTokenConverter extends DefaultAccessTokenConverter {

    @Override
    public OAuth2Authentication extractAuthentication(Map<String, ?> claims) {

        List<String> roles = new ArrayList<>();

        if (claims.containsKey("https://jaun.org/user_roles")) {

            List<String> rolesWithPrefix = ((List<String>) claims.get("https://jaun.org/user_roles")).stream()
                    .map(r -> "ROLE_" + r.toUpperCase())
                    .collect(Collectors.toList());

            roles.addAll(rolesWithPrefix);
        } else {
            List<String> scopes = Arrays.asList(claims.get("scope").toString().split(" +"));

            if (scopes.contains("m2m")) {
                roles.add("ROLE_M2M");
            }
        }

        HashMap<String, Object> newClaimMap = new HashMap<>(claims);
        newClaimMap.put("authorities", roles);
        OAuth2Authentication authentication = super.extractAuthentication(newClaimMap);
        authentication.setDetails(claims);
        return authentication;
    }
}

