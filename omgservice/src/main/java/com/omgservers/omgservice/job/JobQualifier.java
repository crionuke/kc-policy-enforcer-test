package com.omgservers.omgservice.job;

import java.util.Arrays;

public enum JobQualifier {
    EVENT_PROCESSOR("event-processor", "system", 1),
    EVENT_RECOVERER("event-recoverer", "system", 30);

    public static JobQualifier fromString(final String identity) {
        return Arrays.stream(JobQualifier.values())
                .filter(value -> value.identity.equals(identity))
                .findFirst()
                .orElseThrow();
    }

    final String identity;
    final String group;
    final int interval;

    JobQualifier(final String identity, final String group, final int interval) {
        this.identity = identity;
        this.group = group;
        this.interval = interval;
    }

    public String getIdentity() {
        return identity;
    }

    public String getGroup() {
        return group;
    }

    public int getInterval() {
        return interval;
    }
}
