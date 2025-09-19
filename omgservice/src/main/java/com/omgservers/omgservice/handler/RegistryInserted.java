package com.omgservers.omgservice.handler;

import com.omgservers.omgservice.authz.AuthzEntity;
import com.omgservers.omgservice.authz.KeycloakService;
import com.omgservers.omgservice.event.EventHandler;
import com.omgservers.omgservice.event.EventQualifier;
import com.omgservers.omgservice.registry.Registry;
import com.omgservers.omgservice.registry.RegistryAuthzService;
import com.omgservers.omgservice.registry.RegistryConfig;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class RegistryInserted implements EventHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(RegistryInserted.class);

    final RegistryAuthzService registryAuthzService;
    final RegistryInserted thisHandler;
    final KeycloakService keycloakService;

    public RegistryInserted(final RegistryAuthzService registryAuthzService,
                            final RegistryInserted thisHandler,
                            final KeycloakService keycloakService) {
        this.registryAuthzService = registryAuthzService;
        this.keycloakService = keycloakService;
        this.thisHandler = thisHandler;
    }

    @Override
    public EventQualifier getQualifier() {
        return EventQualifier.REGISTRY_INSERTED;
    }

    @Override
    public void handle(final Long resourceId) {
        final var registry = Registry.findByIdRequired(resourceId);
        LOGGER.info("Creating {}", registry);

        final var tenantId = registry.project.tenant.id;
        final var projectId = registry.project.id;
        final var registryName = registry.name;
        final var createdBy = registry.createdBy;

        final var authz = new RegistryConfig.Authz();

        final var viewersGroup = registryAuthzService.createViewersGroup(tenantId, projectId, resourceId, registryName);
        authz.viewersGroup = new AuthzEntity(viewersGroup.getId(), viewersGroup.getName());

        final var managersGroup = registryAuthzService.createManagersGroup(tenantId, projectId, resourceId,
                registryName);
        authz.managersGroup = new AuthzEntity(managersGroup.getId(), managersGroup.getName());

        final var adminsGroup = registryAuthzService.createAdminsGroup(tenantId, projectId, resourceId, registryName);
        authz.adminsGroup = new AuthzEntity(adminsGroup.getId(), adminsGroup.getName());

        keycloakService.joinGroup(createdBy, adminsGroup);

        thisHandler.finish(resourceId);
        LOGGER.info("{} created successfully", registry);
    }

    @Transactional
    public void finish(final Long versionId) {
        final var version = Registry.findByIdLocked(versionId);
        version.finishCreation();
    }
}
