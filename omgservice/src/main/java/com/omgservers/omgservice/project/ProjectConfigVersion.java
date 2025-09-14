package com.omgservers.omgservice.project;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;

public enum ProjectConfigVersion {
    V1("1");

    final String version;

    ProjectConfigVersion(final String version) {
        this.version = version;
    }

    @JsonValue
    public String toJson() {
        return version;
    }

    @JsonCreator
    public static ProjectConfigVersion fromString(final String version) {
        return Arrays.stream(ProjectConfigVersion.values())
                .filter(value -> value.version.equals(version))
                .findFirst()
                .orElseThrow();
    }
}
