package com.omgservers.omgservice.tenant;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class NewTenant {

    @NotBlank
    @Size(max = 64)
    public String name;
}
