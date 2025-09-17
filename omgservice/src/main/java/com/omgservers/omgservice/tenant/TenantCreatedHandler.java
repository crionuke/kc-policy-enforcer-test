package com.omgservers.omgservice.tenant;

import com.omgservers.omgservice.authz.AuthzEntity;
import com.omgservers.omgservice.authz.KeycloakService;
import com.omgservers.omgservice.event.EventHandler;
import com.omgservers.omgservice.event.EventQualifier;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

@ApplicationScoped
public class TenantCreatedHandler implements EventHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(TenantCreatedHandler.class);

    final TenantCreatedHandler thisHandler;
    final TenantAuthzService authzService;
    final KeycloakService keycloakService;

    public TenantCreatedHandler(final TenantCreatedHandler thisHandler,
                                final TenantAuthzService authzService,
                                final KeycloakService keycloakService) {
        this.keycloakService = keycloakService;
        this.authzService = authzService;
        this.thisHandler = thisHandler;
    }

    @Override
    public EventQualifier getQualifier() {
        return EventQualifier.TENANT_CREATED;
    }

    @Override
    public void handle(final Long resourceId) {
        LOGGER.info("Creating tenant {}", resourceId);

        final var tenant = Tenant.findByIdRequired(resourceId);
        final var createdBy = tenant.createdBy;

        final var authz = TenantConfig.Authz.create();

        final var viewersGroup = authzService.createViewersGroup(resourceId);
        authz.groups.viewers = new AuthzEntity(viewersGroup.getId(), viewersGroup.getName());

        final var managersGroup = authzService.createManagersGroup(resourceId);
        authz.groups.managers = new AuthzEntity(managersGroup.getId(), managersGroup.getName());

        final var adminsGroup = authzService.createAdminsGroup(resourceId);
        authz.groups.admins = new AuthzEntity(adminsGroup.getId(), adminsGroup.getName());

        keycloakService.joinGroup(createdBy, adminsGroup);

        final var tenantResource = authzService.createResource(resourceId);
        authz.resource = new AuthzEntity(tenantResource.getId(), tenantResource.getName());

        final var viewersPolicy = authzService.createViewersPolicy(resourceId, viewersGroup);
        authz.policies.viewersGroupMembers = new AuthzEntity(viewersPolicy.getId(), viewersPolicy.getName());

        final var managersPolicy = authzService.createManagersPolicy(resourceId, managersGroup);
        authz.policies.managersGroupMembers = new AuthzEntity(managersPolicy.getId(), managersPolicy.getName());

        final var adminsPolicy = authzService.createAdminsPolicy(resourceId, adminsGroup);
        authz.policies.adminsGroupMembers = new AuthzEntity(adminsPolicy.getId(), adminsPolicy.getName());

        final var viewPermissionPolicies = Set.of(viewersPolicy, managersPolicy, adminsPolicy);
        final var viewPermission = authzService
                .createViewPermission(resourceId, tenantResource, viewPermissionPolicies);
        authz.permissions.view = new AuthzEntity(viewPermission.getId(), viewPermission.getName());

        final var managePermissionPolicies = Set.of(managersPolicy, adminsPolicy);
        final var managePermission = authzService
                .createManagePermission(resourceId, tenantResource, managePermissionPolicies);
        authz.permissions.manage = new AuthzEntity(managePermission.getId(), managePermission.getName());

        final var adminPermissionPolicies = Set.of(adminsPolicy);
        final var adminPermission = authzService
                .createAdminPermission(resourceId, tenantResource, adminPermissionPolicies);
        authz.permissions.admin = new AuthzEntity(adminPermission.getId(), adminPermission.getName());

        thisHandler.finish(resourceId, authz);

        LOGGER.info("Tenant {} created successfully", resourceId);
    }

    @Transactional
    public void finish(final Long resourceId, final TenantConfig.Authz authz) {
        final var tenant = Tenant.findByIdLocked(resourceId);
        tenant.finishCreation(authz);
    }
}
