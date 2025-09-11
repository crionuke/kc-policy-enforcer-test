package com.omgservers.tenants.event;

import com.omgservers.tenants.base.BaseEntity;
import com.omgservers.tenants.errors.EventNotFound;
import io.quarkus.panache.common.Sort;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;

import java.util.List;

@Entity
@Table(name = "omgtenants_event")
public class Event extends BaseEntity {

    public static Event findFirstRequired(final EventQualifier qualifier, final Long resourceId) {
        return Event.<Event>find("qualifier = ?1 and resourceId = ?2 order by created desc",
                        qualifier, resourceId).firstResultOptional()
                .orElseThrow(() -> new EventNotFound(qualifier, resourceId));
    }

    public static List<Event> listNotDeleted(final int size) {
        return Event.<Event>find("deleted", Sort.by("created"), false)
                .page(0, size)
                .list();
    }

    public static void deleteAndMark(final Long eventId, final boolean failed) {
        Event.update("deleted = true, failed = ?1 where id = ?2", failed, eventId);
    }

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    public EventQualifier qualifier;

    @Column(nullable = false, name = "resource_id")
    public Long resourceId;

    public boolean failed;

}