package com.omgservers.omgservice.project;

import com.omgservers.omgservice.base.Resource;
import com.omgservers.omgservice.errors.ProjectNotFound;
import com.omgservers.omgservice.errors.ProjectStatusMismatch;
import com.omgservers.omgservice.errors.ProjectTenantMismatch;
import com.omgservers.omgservice.tenant.Tenant;
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
@Table(name = "omgservice_project")
public class Project extends Resource {

    public static Project findByIdRequired(final Long projectId) {
        return Project.<Project>findByIdOptional(projectId)
                .orElseThrow(() -> new ProjectNotFound(projectId));
    }

    public static Project findByIdLocked(final Long projectId) {
        return Project.<Project>findByIdOptional(projectId, LockModeType.OPTIMISTIC)
                .orElseThrow(() -> new ProjectNotFound(projectId));
    }

    @ManyToOne
    @JoinColumn(name = "tenant_id", nullable = false)
    public Tenant tenant;

    @Column(unique = true)
    public String name;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    public ProjectStatus status;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(nullable = false, columnDefinition = "jsonb")
    public ProjectConfig config;

    public boolean finishCreation(final ProjectConfig.Authz authz) {
        if (status != ProjectStatus.CREATING) {
            return false;
        }

        status = ProjectStatus.CREATED;
        config.authz = authz;
        return true;
    }

    public void ensureTenant(final Long requiredTenantId) {
        final var projectTenantId = tenant.id;
        if (!projectTenantId.equals(requiredTenantId)) {
            throw new ProjectTenantMismatch(id, projectTenantId, requiredTenantId);
        }
    }

    public void ensureCreatedStatus() {
        final var projectStatus = status;
        final var requiredStatus = ProjectStatus.CREATED;
        if (!projectStatus.equals(requiredStatus)) {
            throw new ProjectStatusMismatch(id, projectStatus, requiredStatus);
        }
    }

    public ProjectProjection toProjection() {
        return new ProjectProjection(id,
                createdBy,
                createdAt,
                updatedAt,
                tenant.id,
                tenant.name,
                name,
                status,
                deleted);
    }

    @Override
    public String toString() {
        return "Project{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
