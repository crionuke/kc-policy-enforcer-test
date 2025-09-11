package com.omgservers.omgservice.errors;

import com.omgservers.omgservice.version.VersionStatus;

public class VersionStatusMismatch extends ResourceConflict {

    public VersionStatusMismatch(final Long versionId,
                                 final VersionStatus current,
                                 final VersionStatus required) {
        super("Version %d has invalid status. Current: %s, required: %s"
                .formatted(versionId, current, required));
    }
}