package com.omgservers.tenants.deployment;

import com.omgservers.tenants.base.BaseEntity;
import com.omgservers.tenants.errors.DeploymentNotFound;
import com.omgservers.tenants.errors.DeploymentStageMismatch;
import com.omgservers.tenants.errors.DeploymentTenantMismatch;
import com.omgservers.tenants.errors.DeploymentVersionMismatch;
import com.omgservers.tenants.stage.Stage;
import com.omgservers.tenants.version.Version;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.List;

@Entity
@Table(name = "omgtenants_deployment")
public class Deployment extends BaseEntity {

    public static Deployment findByIdRequired(final Long deploymentId) {
        return Deployment.<Deployment>findByIdOptional(deploymentId)
                .orElseThrow(() -> new DeploymentNotFound(deploymentId));
    }

    public static List<DeploymentProjection> listByProjectId(final Long projectId) {
        return Deployment.
                <Deployment>find("version.project.id = ?1 order by created asc", projectId)
                .project(DeploymentProjection.class)
                .list();
    }

    @ManyToOne
    @JoinColumn(name = "stage_id", nullable = false)
    public Stage stage;

    @ManyToOne
    @JoinColumn(name = "version_id", nullable = false)
    public Version version;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    public DeploymentStatus status;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(nullable = false, columnDefinition = "jsonb")
    public DeploymentConfig config;

    public void ensureTenant(final Long requiredTenantId) {
        final var deploymentTenantId = stage.tenant.id;
        if (!deploymentTenantId.equals(requiredTenantId)) {
            throw new DeploymentTenantMismatch(id, deploymentTenantId, requiredTenantId);
        }
    }

    public void ensureStage(final Long requiredStageId) {
        final var deploymentStageId = stage.id;
        if (!deploymentStageId.equals(requiredStageId)) {
            throw new DeploymentStageMismatch(id, deploymentStageId, requiredStageId);
        }
    }

    public void ensureVersion(final Long requiredVersionId) {
        final var deploymentVersionId = version.id;
        if (!deploymentVersionId.equals(requiredVersionId)) {
            throw new DeploymentVersionMismatch(id, deploymentVersionId, requiredVersionId);
        }
    }
}