package com.omgservers.tenants.errors;

import java.util.UUID;

public class VersionNotFound extends ResourceNotFound {

    public VersionNotFound(final UUID versionId) {
        super("Version %s not found"
                .formatted(versionId));
    }
}