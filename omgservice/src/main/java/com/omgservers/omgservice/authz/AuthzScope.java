package com.omgservers.omgservice.authz;

import java.util.Set;

public enum AuthzScope {

    VIEW(Set.of("GET")),
    MANAGE(Set.of("GET", "PATCH", "PUT", "POST")),
    ADMIN(Set.of("DELETE")),
    ALL(Set.of("GET", "PATCH", "PUT", "POST", "DELETE"));

    final Set<String> methods;

    AuthzScope(Set<String> methods) {
        this.methods = methods;
    }

    public Set<String> getMethods() {
        return methods;
    }
}
