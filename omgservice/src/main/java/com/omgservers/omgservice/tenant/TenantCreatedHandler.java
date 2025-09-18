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
        final var tenant = Tenant.findByIdRequired(resourceId);

        LOGGER.info("Creating tenant {}", tenant);

        final var createdBy = tenant.createdBy;

        final var authz = new TenantConfig.Authz();

        final var viewersGroup = authzService.createViewersGroup(resourceId);
        authz.viewersGroup = new AuthzEntity(viewersGroup.getId(), viewersGroup.getName());

        final var managersGroup = authzService.createManagersGroup(resourceId);
        authz.managersGroup = new AuthzEntity(managersGroup.getId(), managersGroup.getName());

        final var adminsGroup = authzService.createAdminsGroup(resourceId);
        authz.adminsGroup = new AuthzEntity(adminsGroup.getId(), adminsGroup.getName());

        keycloakService.joinGroup(createdBy, adminsGroup);

        final var authzResource = authzService.createResource(resourceId);
        authz.authzResource = new AuthzEntity(authzResource.getId(), authzResource.getName());

        final var viewersPolicy = authzService.createViewersPolicy(resourceId, viewersGroup);
        authz.viewersPolicy = new AuthzEntity(viewersPolicy.getId(), viewersPolicy.getName());

        final var managersPolicy = authzService.createManagersPolicy(resourceId, managersGroup);
        authz.managersPolicy = new AuthzEntity(managersPolicy.getId(), managersPolicy.getName());

        final var adminsPolicy = authzService.createAdminsPolicy(resourceId, adminsGroup);
        authz.adminsPolicy = new AuthzEntity(adminsPolicy.getId(), adminsPolicy.getName());

        final var viewPolicies = Set.of(viewersPolicy, managersPolicy, adminsPolicy);
        final var viewPermission = authzService.createViewPermission(resourceId, authzResource, viewPolicies);
        authz.viewPermission = new AuthzEntity(viewPermission.getId(), viewPermission.getName());

        final var managePolicies = Set.of(managersPolicy, adminsPolicy);
        final var managePermission = authzService.createManagePermission(resourceId, authzResource, managePolicies);
        authz.managePermission = new AuthzEntity(managePermission.getId(), managePermission.getName());

        final var adminPolicies = Set.of(adminsPolicy);
        final var adminPermission = authzService.createAdminPermission(resourceId, authzResource, adminPolicies);
        authz.adminPermission = new AuthzEntity(adminPermission.getId(), adminPermission.getName());

        thisHandler.finish(resourceId, authz);

        LOGGER.info("Tenant {} created successfully", tenant);
    }

    @Transactional
    public void finish(final Long resourceId, final TenantConfig.Authz authz) {
        final var tenant = Tenant.findByIdLocked(resourceId);
        tenant.finishCreation(authz);
    }
}
