package org.jaun.clubmanager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

// https://octoperf.com/blog/2018/03/08/securing-rest-api-spring-security/

// https://www.baeldung.com/spring-security-oauth-jwt

// https://auth0.com/blog/implementing-jwt-authentication-on-spring-boot/


// https://medium.com/@ivarprudnikov/spring-boot-security-expressions-for-auth0-jwt-30ac616a09f0
@SpringBootApplication
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class OauthApp {

    public static void main(String[] args) {
        SpringApplication.run(OauthApp.class, args);
    }
}
