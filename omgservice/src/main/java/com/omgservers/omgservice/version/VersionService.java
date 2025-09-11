package com.omgservers.omgservice.version;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

@Transactional
@ApplicationScoped
public class VersionService {

    public boolean switchStateFromCreatingToCreated(final Long versionId) {
        final var stage = Version.findByIdRequired(versionId);
        if (stage.status == VersionStatus.CREATING) {
            stage.status = VersionStatus.CREATED;
            return true;
        } else {
            return false;
        }
    }
}
