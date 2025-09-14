package com.omgservers.omgservice.stage;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;

public enum StageScope {

    VIEW("scope:omg:stage:view"),
    MANAGE("scope:omg:stage:manage"),
    ADMIN("scope:omg:stage:admin");

    final String name;

    StageScope(final String name) {
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
    public static StageScope fromString(final String name) {
        return Arrays.stream(StageScope.values())
                .filter(value -> value.name.equals(name))
                .findFirst()
                .orElseThrow();
    }
}
