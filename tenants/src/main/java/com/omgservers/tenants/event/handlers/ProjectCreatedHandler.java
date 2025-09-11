package com.omgservers.tenants.event.handlers;

import com.omgservers.tenants.event.EventHandler;
import com.omgservers.tenants.event.EventQualifier;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ProjectCreatedHandler implements EventHandler {

    @Override
    public EventQualifier getQualifier() {
        return EventQualifier.PROJECT_CREATED;
    }

    @Override
    public void handle(final Long resourceId) {

    }
}
