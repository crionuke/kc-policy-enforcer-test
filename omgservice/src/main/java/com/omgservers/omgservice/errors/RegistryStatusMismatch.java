package com.omgservers.omgservice.errors;

import com.omgservers.omgservice.registry.RegistryStatus;

public class RegistryStatusMismatch extends ResourceConflict {

    public RegistryStatusMismatch(final Long registryId,
                                  final RegistryStatus current,
                                  final RegistryStatus required) {
        super("Registry %d has invalid status. Current: %s, required: %s"
                .formatted(registryId, current, required));
    }
}