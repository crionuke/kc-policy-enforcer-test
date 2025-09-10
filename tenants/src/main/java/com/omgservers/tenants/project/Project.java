package com.omgservers.tenants.project;

import com.omgservers.tenants.base.BaseEntity;
import com.omgservers.tenants.errors.ProjectNotFound;
import com.omgservers.tenants.errors.ProjectStatusMismatch;
import com.omgservers.tenants.errors.ProjectTenantMismatch;
import com.omgservers.tenants.tenant.Tenant;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "omgtenants_project")
public class Project extends BaseEntity {

    public static Project findByIdRequired(final Long projectId) {
        return Project.<Project>findByIdOptional(projectId)
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
}
