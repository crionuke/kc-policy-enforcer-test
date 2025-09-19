package com.omgservers.omgservice.version;

import com.omgservers.omgservice.event.EventQualifier;
import com.omgservers.omgservice.event.TestEventService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class TestVersionService {

    @Inject
    VersionResourceClient versionResourceClient;

    @Inject
    TestEventService testEventService;

    public VersionProjection createVersion(final Long projectId,
                                 final NewVersion newVersion,
                                 final boolean process,
                                 final String token) {
        final var version = versionResourceClient.createCheck201(projectId, newVersion, token);
        if (process) {
            testEventService.process(EventQualifier.VERSION_INSERTED, version.id);
        }

        return version;
    }

    public VersionProjection createVersion(final Long projectId,
                                 final boolean process,
                                 final String token) {
        final var newVersion = new NewVersion();
        newVersion.major = 1L;
        newVersion.minor = 1L;
        newVersion.patch = 1L;
        return createVersion(projectId, newVersion, process, token);
    }
}
