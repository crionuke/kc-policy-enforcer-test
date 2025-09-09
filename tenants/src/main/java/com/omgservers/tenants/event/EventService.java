package com.omgservers.tenants.event;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.util.UUID;

@ApplicationScoped
public class EventService {

    public void tenantCreated(final UUID tenantId) {
        create(EventQualifier.TENANT_CREATED, tenantId);
    }

    public void tenantDeleted(final UUID tenantId) {
        create(EventQualifier.TENANT_DELETED, tenantId);
    }

    public void projectCreated(final UUID projectId) {
        create(EventQualifier.PROJECT_CREATED, projectId);
    }

    public void projectDeleted(final UUID projectId) {
        create(EventQualifier.PROJECT_DELETED, projectId);
    }

    public void versionCreated(final UUID versionId) {
        create(EventQualifier.VERSION_CREATED, versionId);
    }

    public void versionDeleted(final UUID versionId) {
        create(EventQualifier.VERSION_DELETED, versionId);
    }

    public void stageCreated(final UUID stageId) {
        create(EventQualifier.STAGE_CREATED, stageId);
    }

    public void stageDeleted(final UUID stageId) {
        create(EventQualifier.STAGE_DELETED, stageId);
    }

    @Transactional
    void create(final EventQualifier qualifier,
                final UUID resourceId) {
        final var event = new Event();
        event.qualifier = qualifier;
        event.resourceId = resourceId;
        event.persist();
    }
}