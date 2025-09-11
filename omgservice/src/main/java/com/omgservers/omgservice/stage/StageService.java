package com.omgservers.omgservice.stage;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

@Transactional
@ApplicationScoped
public class StageService {

    public boolean switchStateFromCreatingToCreated(final Long stageId) {
        final var stage = Stage.findByIdRequired(stageId);
        if (stage.status == StageStatus.CREATING) {
            stage.status = StageStatus.CREATED;
            return true;
        } else {
            return false;
        }
    }
}
