package com.omgservers.omgservice.deployment;

import com.omgservers.omgservice.base.Resource;
import com.omgservers.omgservice.errors.DeploymentNotFound;
import com.omgservers.omgservice.errors.DeploymentStageMismatch;
import com.omgservers.omgservice.errors.DeploymentTenantMismatch;
import com.omgservers.omgservice.errors.DeploymentVersionMismatch;
import com.omgservers.omgservice.stage.Stage;
import com.omgservers.omgservice.version.Version;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.LockModeType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "omgservice_deployment")
public class Deployment extends Resource {

    public static Deployment findByIdRequired(final Long deploymentId) {
        return Deployment.<Deployment>findByIdOptional(deploymentId)
                .orElseThrow(() -> new DeploymentNotFound(deploymentId));
    }

    public static Deployment findByIdLocked(final Long deploymentId) {
        return Deployment.<Deployment>findByIdOptional(deploymentId, LockModeType.OPTIMISTIC)
                .orElseThrow(() -> new DeploymentNotFound(deploymentId));
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

    public boolean finishCreation() {
        if (status != DeploymentStatus.CREATING) {
            return false;
        }

        status = DeploymentStatus.CREATED;
        return true;
    }

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

    public DeploymentProjection toProjection() {
        return new DeploymentProjection(id,
                createdBy,
                createdAt,
                updatedAt,
                stage.tenant.id,
                stage.tenant.name,
                stage.id,
                stage.name,
                version.id,
                version.major,
                version.minor,
                version.patch,
                status,
                deleted);
    }

    @Override
    public String toString() {
        return "Deployment{" +
                "id=" + id +
                '}';
    }
}