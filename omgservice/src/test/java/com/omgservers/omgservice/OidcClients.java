package com.omgservers.omgservice;

import io.quarkus.oidc.client.NamedOidcClient;
import io.quarkus.oidc.client.OidcClient;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class OidcClients {

    final OidcClient adminOidcClient;

    public OidcClients(final @NamedOidcClient(value = "admin") OidcClient adminOidcClient) {
        this.adminOidcClient = adminOidcClient;
    }

    public OidcClient getAdminOidcClient() {
        return adminOidcClient;
    }

    public String getAdminAccessToken() {
        return adminOidcClient.getTokens().await().indefinitely().getAccessToken();
    }
}
