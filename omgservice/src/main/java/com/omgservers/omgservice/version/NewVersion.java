package com.omgservers.omgservice.version;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public class NewVersion {

    @NotNull
    @PositiveOrZero
    public Long major;

    @NotNull
    @PositiveOrZero
    public Long minor;

    @NotNull
    @PositiveOrZero
    public Long patch;
}