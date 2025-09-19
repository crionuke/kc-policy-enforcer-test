package com.omgservers.omgservice.errors;

public class InvalidRegistryName extends BadRequest {

    public InvalidRegistryName(final String name) {
        super("%s is invalid registry name".formatted(name));
    }
}