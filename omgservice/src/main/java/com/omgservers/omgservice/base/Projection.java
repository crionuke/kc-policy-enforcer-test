package com.omgservers.omgservice.base;

import java.time.OffsetDateTime;

public class Projection {

    public Long id;
    public String createdBy;
    public OffsetDateTime createdAt;
    public OffsetDateTime updatedAt;
    public boolean deleted;

    public Projection(final Long id,
                      final String createdBy,
                      final OffsetDateTime createdAt,
                      final OffsetDateTime updatedAt,
                      final boolean deleted) {
        this.id = id;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.deleted = deleted;
    }
}
