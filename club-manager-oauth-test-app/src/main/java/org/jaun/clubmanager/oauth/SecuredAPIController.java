package org.jaun.clubmanager.oauth;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin
@RestController
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecuredAPIController {

    @PreAuthorize("hasRole('ROLE_USER')")
    @RequestMapping(value = "/secure")
    public Response secure() {
        return new Response("example");
    }

    public static class Response {
        private String str;

        public Response(String str) {
            this.str = str;
        }

        public String getStr() {
            return str;
        }

        public void setStr(String str) {
            this.str = str;
        }
    }
}
