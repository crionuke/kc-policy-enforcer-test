package com.omgservers.tenants.tenant;

import com.omgservers.tenants.base.BaseEntity;
import com.omgservers.tenants.errors.TenantNotFound;
import com.omgservers.tenants.errors.TenantStatusMismatch;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.UUID;

@Entity
@Table(name = "tenant")
public class Tenant extends BaseEntity {

    public static Tenant findByIdRequired(final UUID tenantId) {
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
