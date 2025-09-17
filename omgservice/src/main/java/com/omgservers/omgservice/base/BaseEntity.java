package com.omgservers.omgservice.base;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Version;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;

@MappedSuperclass
public class BaseEntity extends PanacheEntityBase {

    @Id
    @SequenceGenerator(name = "entity_id_generator", sequenceName = "omgservice_id_sequence")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "entity_id_generator")
    public Long id;

    @Version
    public Long revision;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    public OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    public OffsetDateTime updatedAt;

    @Column(nullable = false)
    public boolean deleted;

    @Override
    public String toString() {
        return "BaseEntity{" +
                "id=" + id +
                '}';
    }
}
