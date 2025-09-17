package com.omgservers.omgservice.stage;

import com.omgservers.omgservice.base.Resource;
import com.omgservers.omgservice.errors.StageNotFound;
import com.omgservers.omgservice.errors.StageStatusMismatch;
import com.omgservers.omgservice.errors.StageTenantMismatch;
import com.omgservers.omgservice.tenant.Tenant;
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
@Table(name = "omgservice_stage")
public class Stage extends Resource {

    public static Stage findByIdRequired(final Long stageId) {
        return Stage.<Stage>findByIdOptional(stageId)
                .orElseThrow(() -> new StageNotFound(stageId));
    }

    @ManyToOne
    @JoinColumn(name = "tenant_id", nullable = false)
    public Tenant tenant;

    @Column(unique = true)
    public String name;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    public StageStatus status;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(nullable = false, columnDefinition = "jsonb")
    public StageConfig config;

    public void ensureTenant(final Long requiredTenantId) {
        final var stageTenantId = tenant.id;
        if (!stageTenantId.equals(requiredTenantId)) {
            throw new StageTenantMismatch(id, stageTenantId, requiredTenantId);
        }
    }

    public void ensureCreatedStatus() {
        final var stageStatus = status;
        final var requiredStatus = StageStatus.CREATED;
        if (!stageStatus.equals(requiredStatus)) {
            throw new StageStatusMismatch(id, stageStatus, requiredStatus);
        }
    }
}
