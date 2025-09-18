package com.omgservers.omgservice.stage;

import com.omgservers.omgservice.event.EventQualifier;
import com.omgservers.omgservice.event.TestEventService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.UUID;

@ApplicationScoped
public class TestStageService {

    @Inject
    StageResourceClient stageResourceClient;

    @Inject
    TestEventService testEventService;

    public StageProjection createStage(final Long tenantId,
                             final NewStage newStage,
                             final boolean process,
                             final String token) {
        final var stage = stageResourceClient.createCheck201(tenantId, newStage, token);
        if (process) {
            testEventService.process(EventQualifier.STAGE_CREATED, stage.id);
        }

        return stage;
    }

    public StageProjection createStage(final Long tenantId,
                             final boolean process,
                             final String token) {
        final var newStage = new NewStage();
        newStage.name = "stage-" + UUID.randomUUID();
        return createStage(tenantId, newStage, process, token);
    }
}
