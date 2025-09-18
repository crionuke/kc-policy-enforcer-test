package com.omgservers.omgservice.authz;

public enum AuthzScope {

    VIEW("scope:omg:view"),
    MANAGE("scope:omg:manage"),
    ADMIN("scope:omg:admin");

    final String name;

    AuthzScope(final String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
