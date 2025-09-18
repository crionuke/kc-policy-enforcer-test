package com.omgservers.omgservice.stage;

import com.omgservers.omgservice.event.EventQualifier;
import com.omgservers.omgservice.event.EventService;
import com.omgservers.omgservice.tenant.Tenant;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

@Transactional
@ApplicationScoped
public class StageService {

    final EventService eventService;

    public StageService(final EventService eventService) {
        this.eventService = eventService;
    }

    public Stage getById(final Long id) {
        return Stage.findByIdRequired(id);
    }

    public Stage create(final Long tenantId,
                        final NewStage newStage,
                        final String createdBy) {
        final var tenant = Tenant.findByIdRequired(tenantId);
        tenant.ensureCreatedStatus();

        final var stage = new Stage();
        stage.createdBy = createdBy;
        stage.tenant = tenant;
        stage.name = newStage.name;
        stage.status = StageStatus.CREATING;
        stage.config = new StageConfig();
        stage.config.version = StageConfigVersion.V1;
        stage.persist();

        eventService.create(EventQualifier.STAGE_CREATED, stage.id);

        return stage;
    }
}
