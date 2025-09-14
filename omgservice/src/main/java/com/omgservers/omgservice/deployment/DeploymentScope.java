package com.omgservers.omgservice.deployment;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;

public enum DeploymentScope {

    VIEW("scope:omg:deployment:view"),
    MANAGE("scope:omg:deployment:manage"),
    ADMIN("scope:omg:deployment:admin");

    final String name;

    DeploymentScope(final String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @JsonValue
    public String toJson() {
        return name;
    }

    @JsonCreator
    public static DeploymentScope fromString(final String name) {
        return Arrays.stream(DeploymentScope.values())
                .filter(value -> value.name.equals(name))
                .findFirst()
                .orElseThrow();
    }
}
