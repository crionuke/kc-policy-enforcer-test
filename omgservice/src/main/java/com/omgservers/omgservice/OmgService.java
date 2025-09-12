package com.omgservers.omgservice;

import io.quarkus.runtime.Quarkus;
import io.quarkus.runtime.annotations.QuarkusMain;

@QuarkusMain
public class OmgService {

    public static void main(String... args) {
        Quarkus.run(args);
    }
}
