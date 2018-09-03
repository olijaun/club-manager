package org.jaun.clubmanager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

// https://www.baeldung.com/spring-security-multiple-auth-providers
// https://www.baeldung.com/spring-security-multiple-entry-points
// https://www.baeldung.com/x-509-authentication-in-spring-security
@Configuration
@EnableWebSecurity
public class CustomWebSecurityConfigurerAdapter extends WebSecurityConfigurerAdapter {

//    @Autowired
//    private MyBasicAuthenticationEntryPoint authenticationEntryPoint;

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication().withUser("oliver").password(passwordEncoder().encode("password")).authorities("ROLE_USER");
        auth.inMemoryAuthentication().withUser("test").password(passwordEncoder().encode("password")).authorities("ROLE_USER");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        // .authenticationEntryPoint(authenticationEntryPoint);
        http.authorizeRequests().antMatchers("/internal/**").hasIpAddress("localhost");
        http.authorizeRequests().antMatchers("/external/**").authenticated().and().httpBasic();

        // http.addFilterAfter(new CustomFilter(),
        // BasicAuthenticationFilter.class);
        //http.antMatcher("/v2/**").authorizeRequests().anyRequest().permitAll();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
