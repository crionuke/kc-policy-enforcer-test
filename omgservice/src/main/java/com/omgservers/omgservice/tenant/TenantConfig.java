package com.omgservers.omgservice.tenant;

import com.omgservers.omgservice.authz.AuthzEntity;

public class TenantConfig {

    public TenantConfigVersion version;
    public Authz authz;

    public static class Authz {

        public AuthzEntity authzResource;
        public AuthzEntity viewersGroup;
        public AuthzEntity managersGroup;
        public AuthzEntity adminsGroup;
        public AuthzEntity viewersPolicy;
        public AuthzEntity managersPolicy;
        public AuthzEntity adminsPolicy;
        public AuthzEntity viewPermission;
        public AuthzEntity managePermission;
        public AuthzEntity adminPermission;
    }
}
