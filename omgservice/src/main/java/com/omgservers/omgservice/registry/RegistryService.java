package com.omgservers.omgservice.registry;

import com.omgservers.omgservice.event.EventQualifier;
import com.omgservers.omgservice.event.EventService;
import com.omgservers.omgservice.project.Project;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Transactional
@ApplicationScoped
public class RegistryService {
    private static final Logger LOGGER = LoggerFactory.getLogger(RegistryService.class);

    final RegistryNameService registryNameService;
    final EventService eventService;

    public RegistryService(final RegistryNameService registryNameService,
                           final EventService eventService) {
        this.registryNameService = registryNameService;
        this.eventService = eventService;
    }

    public boolean existsByName(final String name) {
        return Registry.count("name", name) > 0;
    }

    public Registry getById(final Long projectId, final Long id) {
        final var registry = Registry.findByIdRequired(id);
        registry.ensureProject(projectId);
        return registry;
    }

    public Registry create(final Long projectId,
                           final NewRegistry newRegistry,
                           final String createdBy) {
        final var project = Project.findByIdRequired(projectId);
        return create(project, newRegistry, createdBy);
    }

    public Registry create(final Project project,
                           final NewRegistry newRegistry,
                           final String createdBy) {
        project.ensureCreatedStatus();

        newRegistry.name = registryNameService.prepare(newRegistry.name);
        final var registry = new Registry();
        registry.createdBy = createdBy;
        registry.project = project;
        registry.name = newRegistry.name;
        registry.status = RegistryStatus.CREATING;
        registry.config = new RegistryConfig();
        registry.config.version = RegistryConfigVersion.V1;
        registry.persist();

        eventService.create(EventQualifier.REGISTRY_INSERTED, registry.id);

        return registry;
    }
}
