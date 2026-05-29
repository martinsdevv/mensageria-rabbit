package com.puc.mensageria.messaging;

import java.time.Instant;

public record SendEmailCommand(
        Long jobId,
        Long messageId,
        Instant requestedAt
) {
}
