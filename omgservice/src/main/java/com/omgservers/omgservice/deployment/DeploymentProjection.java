package com.omgservers.omgservice.deployment;

import com.omgservers.omgservice.base.EntityProjection;
import io.quarkus.hibernate.orm.panache.common.ProjectedFieldName;

import java.time.OffsetDateTime;

public class DeploymentProjection extends EntityProjection {

    public Long stageId;
    public String stageName;
    public Long versionId;
    public Long versionMajor;
    public Long versionMinor;
    public Long versionPatch;
    public DeploymentStatus status;

    public DeploymentProjection(final Long id,
                                final OffsetDateTime created,
                                final OffsetDateTime modified,
                                @ProjectedFieldName("stage.id") Long stageId,
                                @ProjectedFieldName("stage.name") String stageName,
                                @ProjectedFieldName("version.id") Long versionId,
                                @ProjectedFieldName("version.major") Long versionMajor,
                                @ProjectedFieldName("version.minor") Long versionMinor,
                                @ProjectedFieldName("version.patch") Long versionPatch,
                                final DeploymentStatus status,
                                final boolean deleted) {
        super(id, created, modified, deleted);

        this.stageId = stageId;
        this.stageName = stageName;

        this.versionId = versionId;
        this.versionMajor = versionMajor;
        this.versionMinor = versionMinor;
        this.versionPatch = versionPatch;

        this.status = status;
    }
}