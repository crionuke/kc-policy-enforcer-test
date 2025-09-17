package com.omgservers.omgservice.event;

import com.omgservers.omgservice.base.BaseEntity;
import com.omgservers.omgservice.base.SortOrder;
import com.omgservers.omgservice.errors.EventNotFound;
import io.quarkus.panache.common.Sort;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "omgservice_event")
public class Event extends BaseEntity {
    private static final Logger LOGGER = LoggerFactory.getLogger(Event.class);

    public static Event findByIdRequired(final Long eventId) {
        return Event.<Event>findByIdOptional(eventId)
                .orElseThrow(() -> new EventNotFound(eventId));
    }

    public static Event findByQualifierRequired(final EventQualifier qualifier, final Long resourceId) {
        return Event.<Event>find("qualifier = ?1 and resourceId = ?2 order by createdAt desc",
                        qualifier, resourceId).firstResultOptional()
                .orElseThrow(() -> new EventNotFound(qualifier, resourceId));
    }

    public static List<Event> listNotDeleted(final int size) {
        return Event.<Event>find("deleted", Sort.by("createdAt"), false)
                .page(0, size)
                .list();
    }

    public static void deleteAndMark(final Long eventId, final boolean failed) {
        Event.update("deleted = true, failed = ?1 where id = ?2", failed, eventId);
    }

    public static List<Event> search(List<EventQualifier> qualifiers,
                                     List<Long> resourceIds,
                                     List<EventSortColumn> sortBy,
                                     SortOrder order,
                                     int page,
                                     int size) {

        final List<String> conditions = new ArrayList<>();
        final Map<String, Object> parameters = new HashMap<>();

        if (!qualifiers.isEmpty()) {
            conditions.add("qualifier in :qualifiers");
            parameters.put("qualifiers", qualifiers);
        }

        if (!resourceIds.isEmpty()) {
            conditions.add("resourceId in :resourceIds");
            parameters.put("resourceIds", resourceIds);
        }

        final Sort sort;
        final var columns = sortBy.stream().map(EventSortColumn::getColumn).toArray(String[]::new);
        if (columns.length > 0) {
            sort = Sort.by(columns);
        } else {
            sort = Sort.by(EventSortColumn.ID.column);
        }

        if (order.equals(SortOrder.ASC)) {
            sort.ascending();
        } else {
            sort.descending();
        }

        if (conditions.isEmpty()) {
            return Event.<Event>findAll(sort)
                    .page(page, size)
                    .list();
        } else {
            final var query = String.join(" and ", conditions);
            return Event.<Event>find(query, sort, parameters)
                    .page(page, size)
                    .list();
        }
    }

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    public EventQualifier qualifier;

    @Column(nullable = false, name = "resource_id")
    public Long resourceId;

    public boolean failed;

}