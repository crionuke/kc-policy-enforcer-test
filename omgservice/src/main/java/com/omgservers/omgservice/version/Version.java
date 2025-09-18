package com.omgservers.omgservice.version;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.omgservers.omgservice.base.Resource;
import com.omgservers.omgservice.errors.VersionNotFound;
import com.omgservers.omgservice.errors.VersionProjectMismatch;
import com.omgservers.omgservice.errors.VersionStatusMismatch;
import com.omgservers.omgservice.errors.VersionTenantMismatch;
import com.omgservers.omgservice.project.Project;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.LockModeType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "omgservice_version")
public class Version extends Resource {

    public static Version findByIdRequired(final Long versionId) {
        return Version.<Version>findByIdOptional(versionId)
                .orElseThrow(() -> new VersionNotFound(versionId));
    }

    public static Version findByIdLocked(final Long versionId) {
        return Version.<Version>findByIdOptional(versionId, LockModeType.OPTIMISTIC)
                .orElseThrow(() -> new VersionNotFound(versionId));
    }

    @ManyToOne
    @JoinColumn(name = "project_id", nullable = false)
    public Project project;

    @Column(nullable = false)
    public Long major;

    @Column(nullable = false)
    public Long minor;

    @Column(nullable = false)
    public Long patch;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    public VersionStatus status;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(nullable = false, columnDefinition = "jsonb")
    public VersionConfig config;

    @Transient
    @JsonIgnore
    public String getSemver() {
        return "%d.%d.%d".formatted(major, minor, patch);
    }

    public boolean finishCreation() {
        if (status != VersionStatus.CREATING) {
            return false;
        }

        status = VersionStatus.CREATED;
        return true;
    }

    public void ensureTenant(final Long requiredTenantId) {
        final var versionTenantId = project.tenant.id;
        if (!versionTenantId.equals(requiredTenantId)) {
            throw new VersionTenantMismatch(id, versionTenantId, requiredTenantId);
        }
    }

    public void ensureProject(final Long requiredProjectId) {
        final var versionProjectId = project.id;
        if (!versionProjectId.equals(requiredProjectId)) {
            throw new VersionProjectMismatch(id, versionProjectId, requiredProjectId);
        }
    }

    public void ensureCreatedStatus() {
        final var versionStatus = status;
        final var requiredStatus = VersionStatus.CREATED;
        if (!versionStatus.equals(requiredStatus)) {
            throw new VersionStatusMismatch(id, versionStatus, requiredStatus);
        }
    }

    public VersionProjection toProjection() {
        return new VersionProjection(id,
                createdBy,
                createdAt,
                updatedAt,
                project.tenant.id,
                project.tenant.name,
                project.id,
                project.name,
                major,
                minor,
                patch,
                status,
                deleted);
    }

    @Override
    public String toString() {
        return "Version{" +
                "id=" + id +
                ", semver=" + getSemver() +
                '}';
    }
}