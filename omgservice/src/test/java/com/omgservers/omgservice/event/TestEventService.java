package com.omgservers.omgservice.event;

import com.omgservers.omgservice.OidcClients;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class TestEventService {

    @Inject
    EventResourceClient eventResourceClient;

    @Inject
    OidcClients oidcClients;

    public void process(final EventQualifier qualifier,
                        final Long resourceId) {
        final var token = oidcClients.getAdminAccessToken();
        final var event = eventResourceClient.getByQualifierAndResourceIdCheck200(qualifier, resourceId, token);
        eventResourceClient.processByIdCheck204(event.id, token);
    }
}
