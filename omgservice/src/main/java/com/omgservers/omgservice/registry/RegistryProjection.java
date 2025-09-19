package com.omgservers.omgservice.registry;

import com.omgservers.omgservice.base.Projection;
import io.quarkus.hibernate.orm.panache.common.ProjectedFieldName;

import java.time.OffsetDateTime;

public class RegistryProjection extends Projection {

    public Long tenantId;
    public String tenantName;
    public Long projectId;
    public String projectName;
    public String name;
    public RegistryStatus status;

    public RegistryProjection(final Long id,
                              final String createdBy,
                              final OffsetDateTime createdAt,
                              final OffsetDateTime updatedAt,
                              final @ProjectedFieldName("project.tenant.id") Long tenantId,
                              final @ProjectedFieldName("project.tenant.name") String tenantName,
                              final @ProjectedFieldName("project.id") Long projectId,
                              final @ProjectedFieldName("project.name") String projectName,
                              final String name,
                              final RegistryStatus status,
                              final boolean deleted) {
        super(id, createdBy, createdAt, updatedAt, deleted);
        this.tenantId = tenantId;
        this.tenantName = tenantName;
        this.projectId = projectId;
        this.projectName = projectName;
        this.name = name;
        this.status = status;
    }
}
