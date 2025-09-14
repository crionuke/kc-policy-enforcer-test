package com.omgservers.omgservice.version;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;

public enum VersionConfigVersion {
    V1("1");

    final String version;

    VersionConfigVersion(final String version) {
        this.version = version;
    }

    @JsonValue
    public String toJson() {
        return version;
    }

    @JsonCreator
    public static VersionConfigVersion fromString(final String version) {
        return Arrays.stream(VersionConfigVersion.values())
                .filter(value -> value.version.equals(version))
                .findFirst()
                .orElseThrow();
    }
}