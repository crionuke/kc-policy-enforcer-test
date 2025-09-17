package com.omgservers.omgservice.stage;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class NewStage {

    @NotBlank
    @Size(max = 64)
    public String name;
}
