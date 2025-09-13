package com.omgservers.omgservice.tenant;

import com.omgservers.omgservice.event.EventHandler;
import com.omgservers.omgservice.event.EventQualifier;
import jakarta.enterprise.context.ApplicationScoped;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

@ApplicationScoped
public class TenantCreatedHandler implements EventHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(TenantCreatedHandler.class);

    final TenantAuthzService tenantAuthzService;
    final TenantService tenantService;

    public TenantCreatedHandler(final TenantAuthzService tenantAuthzService,
                                final TenantService tenantService) {
        this.tenantAuthzService = tenantAuthzService;
        this.tenantService = tenantService;
    }

    @Override
    public EventQualifier getQualifier() {
        return EventQualifier.TENANT_CREATED;
    }

    @Override
    public void handle(final Long resourceId) {
        LOGGER.info("Creating tenant {}", resourceId);

        final var viewersGroup = tenantAuthzService.createViewersGroup(resourceId);
        final var managersGroup = tenantAuthzService.createManagersGroup(resourceId);
        final var adminsGroup = tenantAuthzService.createAdminsGroup(resourceId);

        final var tenantResource = tenantAuthzService.createResource(resourceId);

        final var viewersPolicy = tenantAuthzService.createViewersPolicy(resourceId, viewersGroup);
        final var managersPolicy = tenantAuthzService.createManagersPolicy(resourceId, managersGroup);
        final var adminsPolicy = tenantAuthzService.createAdminsPolicy(resourceId, adminsGroup);

        final var viewPermissionPolicies = Set.of(viewersPolicy, managersPolicy, adminsPolicy);
        tenantAuthzService.createViewPermission(resourceId, tenantResource, viewPermissionPolicies);

        final var managePermissionPolicies = Set.of(managersPolicy, adminsPolicy);
        tenantAuthzService.createManagePermission(resourceId, tenantResource, managePermissionPolicies);

        final var adminPermissionPolicies = Set.of(adminsPolicy);
        tenantAuthzService.createAdminPermission(resourceId, tenantResource, adminPermissionPolicies);

        tenantService.switchStateFromCreatingToCreated(resourceId);
    }
}
