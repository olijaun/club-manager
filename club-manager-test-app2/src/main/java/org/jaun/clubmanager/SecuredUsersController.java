package org.jaun.clubmanager;

import static java.util.Objects.requireNonNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/")
public class SecuredUsersController {
//    private final UserAuthenticationService authentication;
//
//
//    public SecuredUsersController(UserAuthenticationService authentication) {
//        this.authentication = requireNonNull(authentication);
//    }
//
//    @GetMapping("/current")
//    User getCurrent(@AuthenticationPrincipal final User user) {
//        return user;
//    }
//
//    @GetMapping("/logout")
//    boolean logout(@AuthenticationPrincipal final User user) {
//        authentication.logout(user);
//        return true;
//    }

    @PreAuthorize("hasRole('ROLE_OLI')")
    @GetMapping("/contacts")
    public Map<String, String> test() {
        Map<String,String> map = new HashMap<>();
        map.put("hello", "world");
        return map;
    }
}