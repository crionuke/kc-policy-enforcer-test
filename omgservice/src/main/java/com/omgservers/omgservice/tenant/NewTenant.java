package com.omgservers.omgservice.tenant;

import jakarta.validation.constraints.NotBlank;

public class NewTenant {

    @NotBlank
    public String name;
}
