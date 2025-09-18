package com.omgservers.omgservice.stage;

import com.omgservers.omgservice.authz.AuthzEntity;

public class StageConfig {

    public StageConfigVersion version;

    public StageConfig.Authz authz;

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
