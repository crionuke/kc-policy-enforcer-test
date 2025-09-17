package com.omgservers.omgservice.tenant;

import com.omgservers.omgservice.authz.AuthzEntity;

public class TenantConfig {

    public TenantConfigVersion version;
    public Authz authz;

    public static class Authz {

        static public Authz create() {
            final var authz = new Authz();
            authz.groups = new AuthzGroups();
            authz.policies = new AuthzPolicies();
            authz.permissions = new AuthzPermissions();

            return authz;
        }

        public AuthzGroups groups;
        public AuthzEntity resource;
        public AuthzPolicies policies;
        public AuthzPermissions permissions;
    }

    public static class AuthzGroups {
        public AuthzEntity viewers;
        public AuthzEntity managers;
        public AuthzEntity admins;
    }

    public static class AuthzPolicies {
        public AuthzEntity viewersGroupMembers;
        public AuthzEntity managersGroupMembers;
        public AuthzEntity adminsGroupMembers;
    }

    public static class AuthzPermissions {
        public AuthzEntity view;
        public AuthzEntity manage;
        public AuthzEntity admin;
    }
}
