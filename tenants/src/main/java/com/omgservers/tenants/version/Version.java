package com.omgservers.tenants.version;

import com.omgservers.tenants.base.BaseEntity;
import com.omgservers.tenants.errors.VersionNotFound;
import com.omgservers.tenants.project.Project;
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
@Table(name = "omgtenants_version")
public class Version extends BaseEntity {

    public static Version findByIdRequired(final Long versionId) {
        return Version.<Version>findByIdOptional(versionId)
                .orElseThrow(() -> new VersionNotFound(versionId));
    }

    @ManyToOne()
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
}