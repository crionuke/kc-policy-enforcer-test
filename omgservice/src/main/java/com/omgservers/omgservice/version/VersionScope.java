package com.omgservers.omgservice.version;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;

public enum VersionScope {

    VIEW("scope:omg:version:view"),
    MANAGE("scope:omg:version:manage"),
    ADMIN("scope:omg:version:admin");

    final String name;

    VersionScope(final String name) {
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
    public static VersionScope fromString(final String name) {
        return Arrays.stream(VersionScope.values())
                .filter(value -> value.name.equals(name))
                .findFirst()
                .orElseThrow();
    }
}
