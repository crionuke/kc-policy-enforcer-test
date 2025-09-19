package com.omgservers.omgservice.registry;

import com.omgservers.omgservice.base.Resource;
import com.omgservers.omgservice.errors.RegistryProjectMismatch;
import com.omgservers.omgservice.errors.RegistryStatusMismatch;
import com.omgservers.omgservice.errors.RegistryTenantMismatch;
import com.omgservers.omgservice.errors.VersionNotFound;
import com.omgservers.omgservice.project.Project;
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

import java.util.Optional;

@Entity
@Table(name = "omgservice_registry")
public class Registry extends Resource {

    public static Registry findByIdRequired(final Long registryId) {
        return Registry.<Registry>findByIdOptional(registryId)
                .orElseThrow(() -> new VersionNotFound(registryId));
    }

    public static Optional<Registry> findByProjectOptional(final Project project) {
        return Registry.<Registry>find("project", project)
                .firstResultOptional();
    }

    public static Registry findByIdLocked(final Long versionId) {
        return Registry.<Registry>findByIdOptional(versionId, LockModeType.OPTIMISTIC)
                .orElseThrow(() -> new VersionNotFound(versionId));
    }

    @ManyToOne
    @JoinColumn(name = "project_id", nullable = false)
    public Project project;

    @Column(nullable = false)
    public String name;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    public RegistryStatus status;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(nullable = false, columnDefinition = "jsonb")
    public RegistryConfig config;

    public boolean finishCreation() {
        if (status != RegistryStatus.CREATING) {
            return false;
        }

        status = RegistryStatus.CREATED;
        return true;
    }

    public void ensureTenant(final Long requiredTenantId) {
        final var versionTenantId = project.tenant.id;
        if (!versionTenantId.equals(requiredTenantId)) {
            throw new RegistryTenantMismatch(id, versionTenantId, requiredTenantId);
        }
    }

    public void ensureProject(final Long requiredProjectId) {
        final var registryProjectId = project.id;
        if (!registryProjectId.equals(requiredProjectId)) {
            throw new RegistryProjectMismatch(id, registryProjectId, requiredProjectId);
        }
    }

    public void ensureCreatedStatus() {
        final var registryStatus = status;
        final var requiredStatus = RegistryStatus.CREATED;
        if (!registryStatus.equals(requiredStatus)) {
            throw new RegistryStatusMismatch(id, registryStatus, requiredStatus);
        }
    }

    public RegistryProjection toProjection() {
        return new RegistryProjection(id,
                createdBy,
                createdAt,
                updatedAt,
                project.tenant.id,
                project.tenant.name,
                project.id,
                project.name,
                name,
                status,
                deleted);
    }

    @Override
    public String toString() {
        return "Registry{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}