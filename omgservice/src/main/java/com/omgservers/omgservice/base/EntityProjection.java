package com.omgservers.omgservice.base;

import java.time.OffsetDateTime;

public class EntityProjection {

    public Long id;
    public OffsetDateTime created;
    public OffsetDateTime modified;
    public boolean deleted;

    public EntityProjection(final Long id,
                            final OffsetDateTime created,
                            final OffsetDateTime modified,
                            final boolean deleted) {
        this.id = id;
        this.created = created;
        this.modified = modified;
        this.deleted = deleted;
    }
}
