package com.omgservers.omgservice.errors;

public class PolicyNotFound extends ResourceNotFound {

    public PolicyNotFound(final String name) {
        super("Policy %s not found"
                .formatted(name));
    }
}