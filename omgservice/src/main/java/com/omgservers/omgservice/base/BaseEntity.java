package com.omgservers.omgservice.base;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.SequenceGenerator;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;

@MappedSuperclass
public class BaseEntity extends PanacheEntityBase {

    @Id
    @SequenceGenerator(name = "entity_id_generator", sequenceName = "omgservice_id_sequence")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "entity_id_generator")
    public Long id;

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
