package com.omgservers.omgservice.tenant;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;

public enum TenantConfigVersion {
    V1("1");

    final String version;

    TenantConfigVersion(final String version) {
        this.version = version;
    }

    @JsonValue
    public String toJson() {
        return version;
    }

    @JsonCreator
    public static TenantConfigVersion fromString(final String version) {
        return Arrays.stream(TenantConfigVersion.values())
                .filter(value -> value.version.equals(version))
                .findFirst()
                .orElseThrow();
    }
}
