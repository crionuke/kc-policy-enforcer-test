package com.omgservers.omgservice.project;

import com.omgservers.omgservice.base.Projection;
import io.quarkus.hibernate.orm.panache.common.ProjectedFieldName;

import java.time.OffsetDateTime;

public class ProjectProjection extends Projection {

    public Long tenantId;
    public String tenantName;
    public String name;
    public ProjectStatus status;

    public ProjectProjection(final Long id,
                             final String createdBy,
                             final OffsetDateTime createdAt,
                             final OffsetDateTime updatedAt,
                             final @ProjectedFieldName("tenant.id") Long tenantId,
                             final @ProjectedFieldName("tenant.name") String tenantName,
                             final String name,
                             final ProjectStatus status,
                             final boolean deleted) {
        super(id, createdBy, createdAt, updatedAt, deleted);
        this.tenantId = tenantId;
        this.tenantName = tenantName;
        this.name = name;
        this.status = status;
    }
}
