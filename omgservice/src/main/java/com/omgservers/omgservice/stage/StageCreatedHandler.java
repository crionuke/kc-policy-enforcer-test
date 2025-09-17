package com.omgservers.omgservice.stage;

import com.omgservers.omgservice.authz.KeycloakService;
import com.omgservers.omgservice.event.EventHandler;
import com.omgservers.omgservice.event.EventQualifier;
import com.omgservers.omgservice.tenant.TenantAuthzService;
import jakarta.enterprise.context.ApplicationScoped;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

@ApplicationScoped
public class StageCreatedHandler implements EventHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(StageCreatedHandler.class);

    final TenantAuthzService tenantAuthzService;
    final StageAuthzService stageAuthzService;
    final KeycloakService keycloakService;
    final StageService stageService;

    public StageCreatedHandler(final TenantAuthzService tenantAuthzService,
                               final StageAuthzService stageAuthzService,
                               final KeycloakService keycloakService,
                               final StageService stageService) {
        this.tenantAuthzService = tenantAuthzService;
        this.stageAuthzService = stageAuthzService;
        this.stageService = stageService;
        this.keycloakService = keycloakService;
    }

    @Override
    public EventQualifier getQualifier() {
        return EventQualifier.STAGE_CREATED;
    }

    @Override
    public void handle(final Long resourceId) {
        final var stage = Stage.findByIdRequired(resourceId);
        final var tenantId = stage.tenant.id;
        final var createdBy = stage.createdBy;

        LOGGER.info("Creating stage {}", resourceId);

        final var viewersGroup = stageAuthzService.createViewersGroup(resourceId);
        final var managersGroup = stageAuthzService.createManagersGroup(resourceId);
        final var adminsGroup = stageAuthzService.createAdminsGroup(resourceId);

        keycloakService.joinGroup(createdBy, adminsGroup);

        final var stageResource = stageAuthzService.createResource(tenantId, resourceId);

        final var stageViewersPolicy = stageAuthzService.createViewersPolicy(resourceId, viewersGroup);
        final var stageManagersPolicy = stageAuthzService.createManagersPolicy(resourceId, managersGroup);
        final var stageAdminsPolicy = stageAuthzService.createAdminsPolicy(resourceId, adminsGroup);

        final var tenantViewersPolicyName = tenantAuthzService.getViewersPolicyName(tenantId);
        final var tenantManagersPolicyName = tenantAuthzService.getManagersPolicyName(tenantId);
        final var tenantAdminsPolicyName = tenantAuthzService.getAdminsPolicyName(tenantId);

        final var tenantViewersPolicy = keycloakService.findPolicyByNameRequired(tenantViewersPolicyName);
        final var tenantManagersPolicy = keycloakService.findPolicyByNameRequired(tenantManagersPolicyName);
        final var tenantAdminsPolicy = keycloakService.findPolicyByNameRequired(tenantAdminsPolicyName);

        final var viewPermissionPolicies = Set.of(stageViewersPolicy,
                stageManagersPolicy,
                stageAdminsPolicy,
                tenantViewersPolicy,
                tenantManagersPolicy,
                tenantAdminsPolicy);
        stageAuthzService.createViewPermission(resourceId, stageResource, viewPermissionPolicies);

        final var managePermissionPolicies = Set.of(stageManagersPolicy,
                stageAdminsPolicy,
                tenantManagersPolicy,
                tenantAdminsPolicy);
        stageAuthzService.createManagePermission(resourceId, stageResource, managePermissionPolicies);

        final var adminPermissionPolicies = Set.of(stageAdminsPolicy, tenantAdminsPolicy);
        stageAuthzService.createAdminPermission(resourceId, stageResource, adminPermissionPolicies);

        stageService.switchStateFromCreatingToCreated(resourceId);
    }
}
