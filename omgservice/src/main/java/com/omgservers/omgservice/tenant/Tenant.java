package com.omgservers.omgservice.tenant;

import com.omgservers.omgservice.base.Resource;
import com.omgservers.omgservice.errors.TenantNotFound;
import com.omgservers.omgservice.errors.TenantStatusMismatch;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.LockModeType;
import jakarta.persistence.Table;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "omgservice_tenant")
public class Tenant extends Resource {

    public static Tenant findByIdRequired(final Long tenantId) {
        return Tenant.<Tenant>findByIdOptional(tenantId)
                .orElseThrow(() -> new TenantNotFound(tenantId));
    }

    public static Tenant findByIdLocked(final Long tenantId) {
        return Tenant.<Tenant>findByIdOptional(tenantId, LockModeType.OPTIMISTIC)
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

    public boolean finishCreation(final TenantConfig.Authz authz) {
        if (status != TenantStatus.CREATING) {
            return false;
        }

        status = TenantStatus.CREATED;
        config.authz = authz;
        return true;
    }

    public void ensureCreatedStatus() {
        final var requiredStatus = TenantStatus.CREATED;
        if (!status.equals(requiredStatus)) {
            throw new TenantStatusMismatch(id, status, requiredStatus);
        }
    }


}
