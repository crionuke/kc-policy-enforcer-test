package com.omgservers.omgservice.version;

import com.omgservers.omgservice.base.Projection;
import io.quarkus.hibernate.orm.panache.common.ProjectedFieldName;

import java.time.OffsetDateTime;

public class VersionProjection extends Projection {

    public Long tenantId;
    public String tenantName;
    public Long projectId;
    public String projectName;
    public Long major;
    public Long minor;
    public Long patch;
    public String semver;
    public VersionStatus status;

    public VersionProjection(final Long id,
                             final String createdBy,
                             final OffsetDateTime createdAt,
                             final OffsetDateTime updatedAt,
                             final @ProjectedFieldName("project.tenant.id") Long tenantId,
                             final @ProjectedFieldName("project.tenant.name") String tenantName,
                             final @ProjectedFieldName("project.id") Long projectId,
                             final @ProjectedFieldName("project.name") String projectName,
                             final Long major,
                             final Long minor,
                             final Long patch,
                             final VersionStatus status,
                             final boolean deleted) {
        super(id, createdBy, createdAt, updatedAt, deleted);
        this.tenantId = tenantId;
        this.tenantName = tenantName;
        this.projectId = projectId;
        this.projectName = projectName;
        this.major = major;
        this.minor = minor;
        this.patch = patch;
        this.semver = "%d.%d.%d".formatted(major, minor, patch);
        this.status = status;
    }
}
