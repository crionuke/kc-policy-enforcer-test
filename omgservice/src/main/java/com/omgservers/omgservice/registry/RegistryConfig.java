package com.omgservers.omgservice.registry;

import com.omgservers.omgservice.authz.AuthzEntity;

public class RegistryConfig {

    public RegistryConfigVersion version;
    public RegistryConfig.Authz authz;

    public static class Authz {

        public AuthzEntity viewersGroup;
        public AuthzEntity managersGroup;
        public AuthzEntity adminsGroup;
    }
}