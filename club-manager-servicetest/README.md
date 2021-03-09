# Servicetests

Test the fully deployed application.

In order to run them the file src/test/resources/svt-secret.properties has to be created with the following content. This is required for the test in order to fetch a token from auth0 for authentication.

```
token_url=https://jaun.eu.auth0.com/oauth/token
client_id=h<put client id here>
client_secret=<put client secret here>
audience=https://member-manager.jaun.org
grant_type=client_credentials
```

Of course also token_url and audience has to be adapted according your auth0 config.