package com.omgservers.omgservice.registry;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class NewRegistry {

    @NotBlank
    @Size(max = 64)
    public String name;
}