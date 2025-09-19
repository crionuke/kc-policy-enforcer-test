package com.omgservers.omgservice.errors;

public class RegistryTenantMismatch extends ResourceConflict {

    public RegistryTenantMismatch(final Long versionId,
                                  final Long current,
                                  final Long required) {
        super("Registry %d tenant mismatch. Current: %d, required: %d"
                .formatted(versionId, current, required));
    }
}