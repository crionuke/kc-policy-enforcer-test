package com.omgservers.tenants.errors;

public class VersionNotFound extends ResourceNotFound {

    public VersionNotFound(final Long versionId) {
        super("Version %d not found"
                .formatted(versionId));
    }
}