package com.omgservers.omgservice.event;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;

public enum EventSortColumn {
    ID("id"),
    CREATED_AT("createdAt");

    final String column;

    EventSortColumn(final String column) {
        this.column = column;
    }

    public String getColumn() {
        return column;
    }

    @JsonValue
    public String toJson() {
        return column;
    }

    @JsonCreator
    public static EventSortColumn fromString(final String column) {
        return Arrays.stream(EventSortColumn.values())
                .filter(value -> value.column.equals(column))
                .findFirst()
                .orElseThrow();
    }
}
