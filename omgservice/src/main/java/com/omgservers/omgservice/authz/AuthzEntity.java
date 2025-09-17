package com.omgservers.omgservice.authz;

public class AuthzEntity {

    public String id;
    public String name;

    public AuthzEntity() {
    }

    public AuthzEntity(final String id, final String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public String toString() {
        return "AuthzEntity{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
