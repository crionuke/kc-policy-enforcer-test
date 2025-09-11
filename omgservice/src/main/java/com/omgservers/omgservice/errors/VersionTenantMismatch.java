package com.omgservers.omgservice.errors;

public class VersionTenantMismatch extends ResourceConflict {

    public VersionTenantMismatch(final Long versionId,
                                 final Long current,
                                 final Long required) {
        super("Version %d tenant mismatch. Current: %d, required: %d"
                .formatted(versionId, current, required));
    }
}