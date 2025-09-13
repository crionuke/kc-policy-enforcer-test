package com.omgservers.omgservice.project;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;

public enum ProjectScope {

    VIEW("scope:omg:project:view"),
    DEVELOP("scope:omg:project:develop"),
    MANAGE("scope:omg:project:manage"),
    ADMIN("scope:omg:project:admin");

    final String name;

    ProjectScope(final String name) {
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
    public static ProjectScope fromString(final String name) {
        return Arrays.stream(ProjectScope.values())
                .filter(value -> value.name.equals(name))
                .findFirst()
                .orElseThrow();
    }
}
