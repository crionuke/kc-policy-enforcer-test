package com.omgservers.tenants.event;

import com.omgservers.tenants.base.BaseEntity;
import com.omgservers.tenants.errors.EventNotFound;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(name = "omgtenants_event")
public class Event extends BaseEntity {

    public static Event findFirstRequired(final EventQualifier qualifier, final UUID resourceId) {
        return Event.<Event>find("qualifier = ?1 and resourceId = ?2 order by created desc",
                        qualifier, resourceId).firstResultOptional()
                .orElseThrow(() -> new EventNotFound(qualifier, resourceId));
    }

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    public EventQualifier qualifier;

    @Column(nullable = false, name = "resource_id")
    public UUID resourceId;

}