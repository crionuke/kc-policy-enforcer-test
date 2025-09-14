package com.omgservers.omgservice.stage;

import jakarta.validation.constraints.NotBlank;

public class NewStage {

    @NotBlank
    public String name;
}
