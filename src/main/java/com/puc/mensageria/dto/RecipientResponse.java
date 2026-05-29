package com.puc.mensageria.dto;

import com.puc.mensageria.domain.Recipient;
import java.time.Instant;

public record RecipientResponse(
        Long id,
        String email,
        String name,
        Instant createdAt
) {

    public static RecipientResponse from(Recipient recipient) {
        return new RecipientResponse(
                recipient.getId(),
                recipient.getEmail(),
                recipient.getName(),
                recipient.getCreatedAt()
        );
    }
}
