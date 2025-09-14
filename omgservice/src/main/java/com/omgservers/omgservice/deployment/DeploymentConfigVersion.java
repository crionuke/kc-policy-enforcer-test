package com.omgservers.omgservice.deployment;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;

public enum DeploymentConfigVersion {
    V1("1");

    final String version;

    DeploymentConfigVersion(final String version) {
        this.version = version;
    }

    @JsonValue
    public String toJson() {
        return version;
    }

    @JsonCreator
    public static DeploymentConfigVersion fromString(final String version) {
        return Arrays.stream(DeploymentConfigVersion.values())
                .filter(value -> value.version.equals(version))
                .findFirst()
                .orElseThrow();
    }
}