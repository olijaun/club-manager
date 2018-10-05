package org.jaun.clubmanager;

import java.util.List;
import java.util.Map;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth2.provider.token.UserAuthenticationConverter;
import org.springframework.stereotype.Component;

// alt? https://medium.com/@bvulaj/mapping-your-users-and-roles-with-spring-boot-oauth2-a7ac3bbe8e7f
// https://github.com/spring-projects/spring-security-oauth/issues/640
@Component
public class MyAuthoritiesExtractor implements UserAuthenticationConverter {

    @Override
    public Map<String, ?> convertUserAuthentication(Authentication userAuthentication) {
        return null;
    }

    @Override
    public Authentication extractAuthentication(Map<String, ?> map) {
        if (map.containsKey("sub")) {
            Object principal = map.get("sub");

            List<GrantedAuthority> authorities = AuthorityUtils.commaSeparatedStringToAuthorityList("ROLE_HOBBIT");
            //Collection<? extends GrantedAuthority> authorities = getAuthorities(map);
//            if (userDetailsService != null) {
//                UserDetails user = userDetailsService.loadUserByUsername((String) map.get(USERNAME));
//                authorities = user.getAuthorities();
//                principal = user;
//            }

            return new UsernamePasswordAuthenticationToken(principal, "N/A", authorities);
        }
        return null;
    }
}
