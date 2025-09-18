package com.omgservers.omgservice.deployment;

import com.omgservers.omgservice.base.Projection;
import io.quarkus.hibernate.orm.panache.common.ProjectedFieldName;

import java.time.OffsetDateTime;

public class DeploymentProjection extends Projection {

    public Long stageId;
    public String stageName;
    public Long versionId;
    public String versionSemver;
    public Long versionMajor;
    public Long versionMinor;
    public Long versionPatch;
    public DeploymentStatus status;

    public DeploymentProjection(final Long id,
                                final String createdBy,
                                final OffsetDateTime createdAt,
                                final OffsetDateTime updatedAt,
                                final @ProjectedFieldName("stage.id") Long stageId,
                                final @ProjectedFieldName("stage.name") String stageName,
                                final @ProjectedFieldName("version.id") Long versionId,
                                final @ProjectedFieldName("version.major") Long versionMajor,
                                final @ProjectedFieldName("version.minor") Long versionMinor,
                                final @ProjectedFieldName("version.patch") Long versionPatch,
                                final DeploymentStatus status,
                                final boolean deleted) {
        super(id, createdBy, createdAt, updatedAt, deleted);

        this.stageId = stageId;
        this.stageName = stageName;

        this.versionId = versionId;
        this.versionMajor = versionMajor;
        this.versionMinor = versionMinor;
        this.versionPatch = versionPatch;
        this.versionSemver = "%d.%d.%d".formatted(versionMajor, versionMinor, versionPatch);

        this.status = status;
    }
}