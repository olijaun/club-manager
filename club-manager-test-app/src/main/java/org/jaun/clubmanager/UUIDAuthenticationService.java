package org.jaun.clubmanager;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

@Service
final class UUIDAuthenticationService implements UserAuthenticationService {
    private final UserCrudService users;

    protected UUIDAuthenticationService(UserCrudService users) {
        this.users = Objects.requireNonNull(users);
    }

    @Override
    public Optional<String> login(final String username, final String password) {
        final String uuid = UUID.randomUUID().toString();
        final User user = User.builder().id(uuid).username(username).password(password).build();

        users.save(user);
        return Optional.of(uuid);
    }

    @Override
    public Optional<User> findByToken(final String token) {
        return users.find(token);
    }

    @Override
    public void logout(final User user) {

    }
}