package org.jaun.clubmanager.oauth;

public interface AccessTokenValidator {
    AccessTokenValidationResult validate(String accessToken);
}
