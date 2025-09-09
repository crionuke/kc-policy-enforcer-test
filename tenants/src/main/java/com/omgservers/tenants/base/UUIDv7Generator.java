package com.omgservers.tenants.base;

import com.fasterxml.uuid.Generators;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

public class UUIDv7Generator implements IdentifierGenerator {

    @Override
    public Object generate(final SharedSessionContractImplementor session,
                           final Object object) {
        return Generators.timeBasedEpochGenerator().generate();
    }
}
