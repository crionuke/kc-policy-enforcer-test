package com.omgservers.omgservice.errors;

public class VersionProjectMismatch extends ResourceConflict {

    public VersionProjectMismatch(final Long versionId,
                                  final Long current,
                                  final Long required) {
        super("Version %d project mismatch. Current: %d, required: %d"
                .formatted(versionId, current, required));
    }
}