package org.art.vertex.application.command;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record LoginCommand(

    @NotBlank(message = "Email is required")
    String email,

    @NotBlank(message = "Password is required")
    String password
) {
}
