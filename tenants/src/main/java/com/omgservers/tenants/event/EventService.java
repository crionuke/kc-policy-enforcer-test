package com.omgservers.tenants.event;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class EventService {

    public void tenantCreated(final Long tenantId) {
        create(EventQualifier.TENANT_CREATED, tenantId);
    }

    public void tenantDeleted(final Long tenantId) {
        create(EventQualifier.TENANT_DELETED, tenantId);
    }

    public void projectCreated(final Long projectId) {
        create(EventQualifier.PROJECT_CREATED, projectId);
    }

    public void projectDeleted(final Long projectId) {
        create(EventQualifier.PROJECT_DELETED, projectId);
    }

    public void versionCreated(final Long versionId) {
        create(EventQualifier.VERSION_CREATED, versionId);
    }

    public void versionDeleted(final Long versionId) {
        create(EventQualifier.VERSION_DELETED, versionId);
    }

    public void stageCreated(final Long stageId) {
        create(EventQualifier.STAGE_CREATED, stageId);
    }

    public void stageDeleted(final Long stageId) {
        create(EventQualifier.STAGE_DELETED, stageId);
    }

    @Transactional
    void create(final EventQualifier qualifier, final Long resourceId) {
        final var event = new Event();
        event.qualifier = qualifier;
        event.resourceId = resourceId;
        event.persist();
    }
}