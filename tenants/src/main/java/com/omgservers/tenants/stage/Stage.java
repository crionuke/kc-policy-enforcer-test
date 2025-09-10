package com.omgservers.tenants.stage;

import com.omgservers.tenants.base.BaseEntity;
import com.omgservers.tenants.errors.StageNotFound;
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
@Table(name = "omgtenants_stage")
public class Stage extends BaseEntity {

    public static Stage findByIdRequired(final Long stageId) {
        return Stage.<Stage>findByIdOptional(stageId)
                .orElseThrow(() -> new StageNotFound(stageId));
    }

    @ManyToOne()
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
}
