package com.omgservers.tenants;

import io.quarkus.runtime.Quarkus;
import io.quarkus.runtime.annotations.QuarkusMain;

@QuarkusMain
public class TenantsApplication {

    public static void main(String... args) {
        Quarkus.run(args);
    }
}
