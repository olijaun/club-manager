package org.jaun.clubmanager.oauth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class OAuthTestApp {

    public static void main(String[] args) {
        SpringApplication.run(OAuthTestApp.class, args);
    }

    @Bean
    public OAuthProperties props() {
        return new OAuthProperties();
    }
}
