package com.omgservers.omgservice.tenant;

import com.omgservers.omgservice.base.Projection;

import java.time.OffsetDateTime;

public class TenantProjection extends Projection {

    public String name;
    public TenantStatus status;

    public TenantProjection(final Long id,
                            final String createdBy,
                            final OffsetDateTime createdAt,
                            final OffsetDateTime updatedAt,
                            final String name,
                            final TenantStatus status,
                            final boolean deleted) {
        super(id, createdBy, createdAt, updatedAt, deleted);
        this.name = name;
        this.status = status;
    }
}
