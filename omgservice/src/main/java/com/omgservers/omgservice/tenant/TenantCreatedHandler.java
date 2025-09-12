package com.omgservers.omgservice.tenant;

import com.omgservers.omgservice.event.EventHandler;
import com.omgservers.omgservice.event.EventQualifier;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Set;

@ApplicationScoped
public class TenantCreatedHandler implements EventHandler {

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
        final var resource = tenantAuthzService.createResourceIfAny(resourceId);

        final var viewersGroup = tenantAuthzService.createViewersGroupIfAny(resourceId);
        final var managersGroup = tenantAuthzService.createManagersGroupIfAny(resourceId);
        final var adminsGroup = tenantAuthzService.createAdminsGroupIfAny(resourceId);

        final var viewersPolicy = tenantAuthzService.createViewersPolicyIfAny(resourceId, viewersGroup);
        final var managersPolicy = tenantAuthzService.createManagersPolicyIfAny(resourceId, managersGroup);
        final var adminsPolicy = tenantAuthzService.createAdminsPolicyIfAny(resourceId, adminsGroup);

        final var viewPermissionPolicies = Set.of(viewersPolicy, managersPolicy, adminsPolicy);
        tenantAuthzService.createViewPermissionIfAny(resourceId, resource, viewPermissionPolicies);
        tenantAuthzService.createManagePermissionIfAny(resourceId, resource, Set.of(managersPolicy, adminsPolicy));
        tenantAuthzService.createAdminPermissionIfAny(resourceId, resource, Set.of(adminsPolicy));

        tenantService.switchStateFromCreatingToCreated(resourceId);
    }
}
