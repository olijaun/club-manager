package org.jaun.clubmanager.oauth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices;

@Configuration
@EnableResourceServer
@EnableWebSecurity
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {

    @Autowired
    private OAuthProperties oAuthProperties;

    @Override
    public void configure(final HttpSecurity http) throws Exception {
        //http.authorizeRequests().anyRequest().hasIpAddress("localhost");
//        http.cors()
//                .and()
//                .sessionManagement()
//                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
//                .and()
//                .authorizeRequests()
//                .anyRequest()
//                .hasRole("USER");


        http.cors()
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                .anyRequest().hasRole("USER")
                .and()
                .httpBasic()
                .and()
                .csrf().disable();
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        String password = passwordEncoder().encode("password");
        auth.inMemoryAuthentication().withUser("user").password(password).roles("USER");
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
        resources.resourceId(oAuthProperties.getClientId());
    }

    @Bean
    public ResourceServerTokenServices tokenServices(OAuthProperties oAuthProperties, AccessTokenValidator tokenValidator) {
        GoogleTokenServices googleTokenServices = new GoogleTokenServices(tokenValidator);
        googleTokenServices.setUserInfoUrl(oAuthProperties.getUserInfoUrl());
        return googleTokenServices;
    }

    @Bean
    public AccessTokenValidator tokenValidator(OAuthProperties oAuthProperties) {
        GoogleAccessTokenValidator accessTokenValidator = new GoogleAccessTokenValidator();
        accessTokenValidator.setClientId(oAuthProperties.getClientId());
        accessTokenValidator.setCheckTokenUrl(oAuthProperties.getCheckTokenUrl() + "?access_token={accessToken}");
        return accessTokenValidator;
    }
}
