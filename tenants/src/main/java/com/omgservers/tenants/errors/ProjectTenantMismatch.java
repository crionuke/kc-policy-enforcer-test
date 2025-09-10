package com.omgservers.tenants.errors;

public class ProjectTenantMismatch extends ResourceConflict {

    public ProjectTenantMismatch(final Long projectId,
                                 final Long current,
                                 final Long required) {
        super("Project %d tenant mismatch. Current: %d, required: %d"
                .formatted(projectId, current, required));
    }
}