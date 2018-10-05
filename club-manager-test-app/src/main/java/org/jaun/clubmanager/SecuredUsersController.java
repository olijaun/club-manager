package org.jaun.clubmanager;

import static java.util.Objects.requireNonNull;

import java.util.Objects;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/users")
final class SecuredUsersController {
    private final UserAuthenticationService authentication;


    public SecuredUsersController(UserAuthenticationService authentication) {
        this.authentication = requireNonNull(authentication);
    }

    @GetMapping("/current")
    User getCurrent(@AuthenticationPrincipal final User user) {
        return user;
    }

    @GetMapping("/logout")
    boolean logout(@AuthenticationPrincipal final User user) {
        authentication.logout(user);
        return true;
    }
}