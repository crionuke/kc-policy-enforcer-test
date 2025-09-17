package com.omgservers.omgservice.base;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;

@MappedSuperclass
public class Resource extends BaseEntity {

    @Column(name = "created_by", nullable = false, updatable = false)
    public String createdBy;

    @Override
    public String toString() {
        return "Resource{" +
                "id=" + id +
                '}';
    }
}
