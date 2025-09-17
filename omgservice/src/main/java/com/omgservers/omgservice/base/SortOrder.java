package com.omgservers.omgservice.base;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;

public enum SortOrder {
    ASC(Constants.ASC),
    DESC(Constants.DESC);

    final String direction;

    SortOrder(final String direction) {
        this.direction = direction;
    }

    @JsonValue
    public String toJson() {
        return direction;
    }

    @JsonCreator
    public static SortOrder fromString(final String direction) {
        return Arrays.stream(SortOrder.values())
                .filter(value -> value.direction.equals(direction))
                .findFirst()
                .orElseThrow();
    }

    static public class Constants {
        static public final String ASC = "asc";
        static public final String DESC = "desc";
    }
}
