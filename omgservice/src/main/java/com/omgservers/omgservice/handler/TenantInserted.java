package com.omgservers.omgservice.handler;

import com.omgservers.omgservice.authz.AuthzEntity;
import com.omgservers.omgservice.authz.KeycloakService;
import com.omgservers.omgservice.event.EventHandler;
import com.omgservers.omgservice.event.EventQualifier;
import com.omgservers.omgservice.tenant.Tenant;
import com.omgservers.omgservice.tenant.TenantAuthzService;
import com.omgservers.omgservice.tenant.TenantConfig;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

@ApplicationScoped
public class TenantInserted implements EventHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(TenantInserted.class);

    final TenantAuthzService tenantAuthzService;
    final TenantInserted thisHandler;
    final KeycloakService keycloakService;

    public TenantInserted(final TenantAuthzService tenantAuthzService,
                          final TenantInserted thisHandler,
                          final KeycloakService keycloakService) {
        this.tenantAuthzService = tenantAuthzService;
        this.keycloakService = keycloakService;
        this.thisHandler = thisHandler;
    }

    @Override
    public EventQualifier getQualifier() {
        return EventQualifier.TENANT_INSERTED;
    }

    @Override
    public void handle(final Long resourceId) {
        final var tenant = Tenant.findByIdRequired(resourceId);

        LOGGER.info("Creating {}", tenant);

        final var createdBy = tenant.createdBy;

        final var authz = new TenantConfig.Authz();

        final var viewersGroup = tenantAuthzService.createViewersGroup(resourceId);
        authz.viewersGroup = new AuthzEntity(viewersGroup.getId(), viewersGroup.getName());

        final var managersGroup = tenantAuthzService.createManagersGroup(resourceId);
        authz.managersGroup = new AuthzEntity(managersGroup.getId(), managersGroup.getName());

        final var adminsGroup = tenantAuthzService.createAdminsGroup(resourceId);
        authz.adminsGroup = new AuthzEntity(adminsGroup.getId(), adminsGroup.getName());

        keycloakService.joinGroup(createdBy, adminsGroup);

        final var authzResource = tenantAuthzService.createResource(resourceId);
        authz.authzResource = new AuthzEntity(authzResource.getId(), authzResource.getName());

        final var viewersPolicy = tenantAuthzService.createViewersPolicy(resourceId, viewersGroup);
        authz.viewersPolicy = new AuthzEntity(viewersPolicy.getId(), viewersPolicy.getName());

        final var managersPolicy = tenantAuthzService.createManagersPolicy(resourceId, managersGroup);
        authz.managersPolicy = new AuthzEntity(managersPolicy.getId(), managersPolicy.getName());

        final var adminsPolicy = tenantAuthzService.createAdminsPolicy(resourceId, adminsGroup);
        authz.adminsPolicy = new AuthzEntity(adminsPolicy.getId(), adminsPolicy.getName());

        final var viewPolicies = Set.of(viewersPolicy, managersPolicy, adminsPolicy);
        final var viewPermission = tenantAuthzService.createViewPermission(resourceId, authzResource, viewPolicies);
        authz.viewPermission = new AuthzEntity(viewPermission.getId(), viewPermission.getName());

        final var managePolicies = Set.of(managersPolicy, adminsPolicy);
        final var managePermission =
                tenantAuthzService.createManagePermission(resourceId, authzResource, managePolicies);
        authz.managePermission = new AuthzEntity(managePermission.getId(), managePermission.getName());

        final var adminPolicies = Set.of(adminsPolicy);
        final var adminPermission = tenantAuthzService.createAdminPermission(resourceId, authzResource, adminPolicies);
        authz.adminPermission = new AuthzEntity(adminPermission.getId(), adminPermission.getName());

        thisHandler.finish(resourceId, authz);

        LOGGER.info("{} created successfully", tenant);
    }

    @Transactional
    public void finish(final Long resourceId, final TenantConfig.Authz authz) {
        final var tenant = Tenant.findByIdLocked(resourceId);
        tenant.finishCreation(authz);
    }
}
