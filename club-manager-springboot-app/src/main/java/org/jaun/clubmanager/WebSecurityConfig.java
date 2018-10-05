package org.jaun.clubmanager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

// https://stackoverflow.com/questions/23526644/spring-security-with-oauth2-or-http-basic-authentication-for-the-same-resource
//@Configuration
//@Order(2)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.anonymous().disable().requestMatcher(request -> {
            String auth = request.getHeader(HttpHeaders.AUTHORIZATION);
            return (auth != null && auth.startsWith("Basic"));
        }).antMatcher("/**").authorizeRequests().anyRequest().authenticated().and().httpBasic();
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
}
