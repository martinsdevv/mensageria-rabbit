package com.puc.mensageria.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateRecipientRequest(
        @NotBlank @Email String email,
        @Size(max = 100) String name
) {
}
