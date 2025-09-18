package com.omgservers.omgservice.version;

import com.omgservers.omgservice.event.EventHandler;
import com.omgservers.omgservice.event.EventQualifier;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class VersionCreatedHandler implements EventHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(VersionCreatedHandler.class);

    final VersionCreatedHandler thisHandler;

    public VersionCreatedHandler(final VersionCreatedHandler thisHandler) {
        this.thisHandler = thisHandler;
    }

    @Override
    public EventQualifier getQualifier() {
        return EventQualifier.VERSION_CREATED;
    }

    @Override
    public void handle(final Long resourceId) {
        final var version = Version.findByIdRequired(resourceId);
        LOGGER.info("Creating version {}", version);

        thisHandler.finish(resourceId);
        LOGGER.info("Version {} created successfully", version);
    }

    @Transactional
    public void finish(final Long versionId) {
        final var version = Version.findByIdLocked(versionId);
        version.finishCreation();
    }
}
