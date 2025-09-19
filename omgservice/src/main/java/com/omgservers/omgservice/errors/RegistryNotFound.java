package com.omgservers.omgservice.errors;

public class RegistryNotFound extends ResourceNotFound {

    public RegistryNotFound(final Long registryId) {
        super("Registry %d not found"
                .formatted(registryId));
    }
}