package com.puc.mensageria.dto;

import com.puc.mensageria.domain.EmailMessage;
import java.time.Instant;

public record MessageResponse(
        Long id,
        String subject,
        String body,
        Instant createdAt
) {

    public static MessageResponse from(EmailMessage message) {
        return new MessageResponse(
                message.getId(),
                message.getSubject(),
                message.getBody(),
                message.getCreatedAt()
        );
    }
}
