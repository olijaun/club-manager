package org.jaun.clubmanager;

import static java.util.Objects.requireNonNull;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/public/users")
final class PublicUsersController {
    private final UserAuthenticationService authentication;
    private final UserCrudService users;

    public PublicUsersController(UserAuthenticationService authentication, UserCrudService users) {
        this.authentication = requireNonNull(authentication);
        this.users = requireNonNull(users);
    }

    @PostMapping("/register")
    String register(@RequestParam("username") final String username, @RequestParam("password") final String password) {
        users.save(User.builder().id(username).username(username).password(password).build());

        return login(username, password);
    }

    @PostMapping("/login")
    String login(@RequestParam("username") final String username, @RequestParam("password") final String password) {
        return authentication.login(username, password).orElseThrow(() -> new RuntimeException("invalid login and/or password"));
    }
}
