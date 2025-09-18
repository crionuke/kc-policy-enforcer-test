package com.omgservers.omgservice.stage;

import com.omgservers.omgservice.authz.AuthzEntity;
import com.omgservers.omgservice.authz.KeycloakService;
import com.omgservers.omgservice.event.EventHandler;
import com.omgservers.omgservice.event.EventQualifier;
import com.omgservers.omgservice.tenant.TenantAuthzService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

@ApplicationScoped
public class StageCreatedHandler implements EventHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(StageCreatedHandler.class);

    final TenantAuthzService tenantAuthzService;
    final StageAuthzService stageAuthzService;
    final StageCreatedHandler thisHandler;
    final KeycloakService keycloakService;
    final StageService stageService;

    public StageCreatedHandler(final TenantAuthzService tenantAuthzService,
                               final StageAuthzService stageAuthzService,
                               final StageCreatedHandler thisHandler,
                               final KeycloakService keycloakService,
                               final StageService stageService) {
        this.tenantAuthzService = tenantAuthzService;
        this.stageAuthzService = stageAuthzService;
        this.keycloakService = keycloakService;
        this.stageService = stageService;
        this.thisHandler = thisHandler;
    }

    @Override
    public EventQualifier getQualifier() {
        return EventQualifier.STAGE_CREATED;
    }

    @Override
    public void handle(final Long resourceId) {
        final var stage = Stage.findByIdRequired(resourceId);

        LOGGER.info("Creating stage {}", stage);

        final var tenantId = stage.tenant.id;
        final var createdBy = stage.createdBy;

        final var authz = new StageConfig.Authz();

        final var viewersGroup = stageAuthzService.createViewersGroup(resourceId);
        authz.viewersGroup = new AuthzEntity(viewersGroup.getId(), viewersGroup.getName());

        final var managersGroup = stageAuthzService.createManagersGroup(resourceId);
        authz.managersGroup = new AuthzEntity(managersGroup.getId(), managersGroup.getName());

        final var adminsGroup = stageAuthzService.createAdminsGroup(resourceId);
        authz.adminsGroup = new AuthzEntity(adminsGroup.getId(), adminsGroup.getName());

        keycloakService.joinGroup(createdBy, adminsGroup);

        final var authzResource = stageAuthzService.createResource(tenantId, resourceId);
        authz.authzResource = new AuthzEntity(authzResource.getId(), authzResource.getName());

        final var viewersPolicy = stageAuthzService.createViewersPolicy(resourceId, viewersGroup);
        authz.viewersPolicy = new AuthzEntity(viewersPolicy.getId(), viewersPolicy.getName());

        final var managersPolicy = stageAuthzService.createManagersPolicy(resourceId, managersGroup);
        authz.managersPolicy = new AuthzEntity(managersPolicy.getId(), managersPolicy.getName());

        final var adminsPolicy = stageAuthzService.createAdminsPolicy(resourceId, adminsGroup);
        authz.adminsPolicy = new AuthzEntity(adminsPolicy.getId(), adminsPolicy.getName());

        final var tenantViewersPolicyName = stage.tenant.config.authz.viewersPolicy.name;
        final var tenantManagersPolicyName = stage.tenant.config.authz.managersPolicy.name;
        final var tenantAdminsPolicyName = stage.tenant.config.authz.adminsPolicy.name;

        final var tenantViewersPolicy = keycloakService.findPolicyByNameRequired(tenantViewersPolicyName);
        final var tenantManagersPolicy = keycloakService.findPolicyByNameRequired(tenantManagersPolicyName);
        final var tenantAdminsPolicy = keycloakService.findPolicyByNameRequired(tenantAdminsPolicyName);

        final var viewPolicies = Set.of(viewersPolicy, managersPolicy, adminsPolicy, tenantViewersPolicy,
                tenantManagersPolicy, tenantAdminsPolicy);
        final var viewPermission = stageAuthzService.createViewPermission(resourceId, authzResource, viewPolicies);
        authz.viewPermission = new AuthzEntity(viewPermission.getId(), viewPermission.getName());

        final var managePolicies = Set.of(managersPolicy, adminsPolicy, tenantAdminsPolicy);
        final var managePermission = stageAuthzService.createManagePermission(resourceId, authzResource,
                managePolicies);
        authz.managePermission = new AuthzEntity(managePermission.getId(), managePermission.getName());

        final var adminPermissionPolicies = Set.of(adminsPolicy, tenantAdminsPolicy);
        stageAuthzService.createAdminPermission(resourceId, authzResource, adminPermissionPolicies);
        authz.adminPermission = new AuthzEntity(managePermission.getId(), managePermission.getName());

        thisHandler.finish(resourceId, authz);

        LOGGER.info("Stage {} created successfully", stage);
    }

    @Transactional
    public void finish(final Long stageId, final StageConfig.Authz authz) {
        final var stage = Stage.findByIdLocked(stageId);
        stage.finishCreation(authz);
    }
}
