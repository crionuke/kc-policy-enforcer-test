package com.omgservers.omgservice.project;

import jakarta.validation.constraints.NotBlank;

public class NewProject {

    @NotBlank
    public String name;
}
