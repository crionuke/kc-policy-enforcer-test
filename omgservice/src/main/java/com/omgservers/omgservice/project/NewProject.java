package com.omgservers.omgservice.project;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class NewProject {

    @NotBlank
    @Size(max = 64)
    public String name;
}
