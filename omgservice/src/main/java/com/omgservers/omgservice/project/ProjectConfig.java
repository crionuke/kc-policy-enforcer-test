package com.omgservers.omgservice.project;

import com.omgservers.omgservice.authz.AuthzEntity;

public class ProjectConfig {

    public ProjectConfigVersion version;

    public ProjectConfig.Authz authz;

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
