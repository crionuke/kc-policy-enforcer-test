package com.omgservers.omgservice.tenant;

import com.omgservers.omgservice.base.BaseEntity;
import com.omgservers.omgservice.errors.TenantNotFound;
import com.omgservers.omgservice.errors.TenantStatusMismatch;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "omgservice_tenant")
public class Tenant extends BaseEntity {

    public static Tenant findByIdRequired(final Long tenantId) {
        return Tenant.<Tenant>findByIdOptional(tenantId)
                .orElseThrow(() -> new TenantNotFound(tenantId));
    }

    @Column(unique = true)
    public String name;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    public TenantStatus status;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(nullable = false, columnDefinition = "jsonb")
    public TenantConfig config;

    public void ensureCreatedStatus() {
        final var tenantStatus = status;
        final var requiredStatus = TenantStatus.CREATED;
        if (!tenantStatus.equals(requiredStatus)) {
            throw new TenantStatusMismatch(id, tenantStatus, requiredStatus);
        }
    }
}
