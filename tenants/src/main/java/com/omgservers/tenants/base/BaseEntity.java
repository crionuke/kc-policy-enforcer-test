package com.omgservers.tenants.base;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;
import java.util.UUID;

@MappedSuperclass
public class BaseEntity extends PanacheEntityBase {

    @Id
    @UUIDv7Identifier
    public UUID id;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    public OffsetDateTime created;

    @UpdateTimestamp
    @Column(nullable = false)
    public OffsetDateTime modified;

    @Column(nullable = false)
    public boolean deleted;

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "<" + id + ">";
    }
}
