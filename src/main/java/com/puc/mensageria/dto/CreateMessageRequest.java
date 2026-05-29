package com.puc.mensageria.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateMessageRequest(
        @NotBlank @Size(max = 200) String subject,
        @NotBlank String body
) {
}
