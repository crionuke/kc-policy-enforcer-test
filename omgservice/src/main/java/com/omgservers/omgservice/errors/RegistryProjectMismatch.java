package com.omgservers.omgservice.errors;

public class RegistryProjectMismatch extends ResourceConflict {

    public RegistryProjectMismatch(final Long registryId,
                                   final Long current,
                                   final Long required) {
        super("Registry %d project mismatch. Current: %d, required: %d"
                .formatted(registryId, current, required));
    }
}