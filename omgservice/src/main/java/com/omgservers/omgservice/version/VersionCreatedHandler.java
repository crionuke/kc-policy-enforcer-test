package com.omgservers.omgservice.version;

import com.omgservers.omgservice.event.EventHandler;
import com.omgservers.omgservice.event.EventQualifier;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class VersionCreatedHandler implements EventHandler {

    final VersionService versionService;

    public VersionCreatedHandler(final VersionService versionService) {
        this.versionService = versionService;
    }

    @Override
    public EventQualifier getQualifier() {
        return EventQualifier.VERSION_CREATED;
    }

    @Override
    public void handle(final Long resourceId) {
        versionService.switchStateFromCreatingToCreated(resourceId);
    }
}
