package com.omgservers.omgservice.stage;

import com.omgservers.omgservice.event.EventHandler;
import com.omgservers.omgservice.event.EventQualifier;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class StageCreatedHandler implements EventHandler {

    final StageService stageService;

    public StageCreatedHandler(final StageService stageService) {
        this.stageService = stageService;
    }

    @Override
    public EventQualifier getQualifier() {
        return EventQualifier.STAGE_CREATED;
    }

    @Override
    public void handle(final Long resourceId) {
        stageService.switchStateFromCreatingToCreated(resourceId);
    }
}
