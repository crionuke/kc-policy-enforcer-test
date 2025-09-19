package com.omgservers.omgservice.handler;

import com.omgservers.omgservice.event.EventHandler;
import com.omgservers.omgservice.event.EventQualifier;
import com.omgservers.omgservice.project.Project;
import com.omgservers.omgservice.registry.NewRegistry;
import com.omgservers.omgservice.registry.Registry;
import com.omgservers.omgservice.registry.RegistryNameService;
import com.omgservers.omgservice.registry.RegistryService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class ProjectCreated implements EventHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProjectCreated.class);

    final RegistryNameService registryNameService;
    final RegistryService registryService;
    final ProjectCreated thisHandler;

    public ProjectCreated(final RegistryNameService registryNameService,
                          final RegistryService registryService,
                          final ProjectCreated thisHandler) {
        this.registryNameService = registryNameService;
        this.registryService = registryService;
        this.thisHandler = thisHandler;
    }

    @Override
    public EventQualifier getQualifier() {
        return EventQualifier.PROJECT_CREATED;
    }

    @Override
    public void handle(final Long resourceId) {
        final var project = Project.findByIdRequired(resourceId);

        if (project.config.representation.withRegistry) {
            final var registryOptional = Registry.findByProjectOptional(project);
            if (registryOptional.isEmpty()) {
                LOGGER.info("Creating registry for {}", project);
                final var registry = thisHandler.createRegistry(project);

                LOGGER.info("{} created for {}", registry, project);
            } else {
                LOGGER.warn("{} already exists for {}", registryOptional.get(), project);
            }
        }
    }

    @Transactional
    public Registry createRegistry(final Project project) {
        final var newRegistry = new NewRegistry();
        final var defaultName = "project-%d".formatted(project.id);
        final var preparedName = registryNameService.prepare(project.name, defaultName);
        newRegistry.name = registryService.existsByName(preparedName) ? defaultName : preparedName;
        return registryService.create(project.id, newRegistry, project.createdBy);
    }
}
