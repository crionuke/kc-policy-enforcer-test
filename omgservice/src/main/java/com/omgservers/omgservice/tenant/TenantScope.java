package com.omgservers.omgservice.tenant;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;

public enum TenantScope {

    VIEW("scope:omg:tenant:view"),
    MANAGE("scope:omg:tenant:manage"),
    ADMIN("scope:omg:tenant:admin");

    final String name;

    TenantScope(final String name) {
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
    public static TenantScope fromString(final String name) {
        return Arrays.stream(TenantScope.values())
                .filter(value -> value.name.equals(name))
                .findFirst()
                .orElseThrow();
    }
}
