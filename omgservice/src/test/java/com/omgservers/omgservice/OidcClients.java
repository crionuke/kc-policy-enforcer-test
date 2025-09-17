package com.omgservers.omgservice;

import io.quarkus.oidc.client.NamedOidcClient;
import io.quarkus.oidc.client.OidcClient;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class OidcClients {

    @Inject
    @NamedOidcClient(value = "admin")
    OidcClient adminOidcClient;

    public String getAdminAccessToken() {
        return adminOidcClient.getTokens().await().indefinitely().getAccessToken();
    }
}
