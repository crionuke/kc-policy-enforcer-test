package com.omgservers.omgservice.stage;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;

public enum StageConfigVersion {
    V1("1");

    final String version;

    StageConfigVersion(final String version) {
        this.version = version;
    }

    @JsonValue
    public String toJson() {
        return version;
    }

    @JsonCreator
    public static StageConfigVersion fromString(final String version) {
        return Arrays.stream(StageConfigVersion.values())
                .filter(value -> value.version.equals(version))
                .findFirst()
                .orElseThrow();
    }
}
