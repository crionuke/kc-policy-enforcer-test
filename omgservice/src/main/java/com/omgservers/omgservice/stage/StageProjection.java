package com.omgservers.omgservice.stage;

import com.omgservers.omgservice.base.Projection;
import io.quarkus.hibernate.orm.panache.common.ProjectedFieldName;

import java.time.OffsetDateTime;

public class StageProjection extends Projection {

    public Long tenantId;
    public String tenantName;
    public String name;
    public StageStatus status;

    public StageProjection(final Long id,
                           final String createdBy,
                           final OffsetDateTime createdAt,
                           final OffsetDateTime updatedAt,
                           final @ProjectedFieldName("tenant.id") Long tenantId,
                           final @ProjectedFieldName("tenant.name") String tenantName,
                           final String name,
                           final StageStatus status,
                           final boolean deleted) {
        super(id, createdBy, createdAt, updatedAt, deleted);
        this.tenantId = tenantId;
        this.tenantName = tenantName;
        this.name = name;
        this.status = status;
    }
}
