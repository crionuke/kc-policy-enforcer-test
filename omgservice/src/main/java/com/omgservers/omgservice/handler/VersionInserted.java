package com.omgservers.omgservice.handler;

import com.omgservers.omgservice.event.EventHandler;
import com.omgservers.omgservice.event.EventQualifier;
import com.omgservers.omgservice.version.Version;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class VersionInserted implements EventHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(VersionInserted.class);

    final VersionInserted thisHandler;

    public VersionInserted(final VersionInserted thisHandler) {
        this.thisHandler = thisHandler;
    }

    @Override
    public EventQualifier getQualifier() {
        return EventQualifier.VERSION_INSERTED;
    }

    @Override
    public void handle(final Long resourceId) {
        final var version = Version.findByIdRequired(resourceId);
        LOGGER.info("Creating {}", version);

        thisHandler.finish(resourceId);
        LOGGER.info("{} created successfully", version);
    }

    @Transactional
    public void finish(final Long versionId) {
        final var version = Version.findByIdLocked(versionId);
        version.finishCreation();
    }
}
