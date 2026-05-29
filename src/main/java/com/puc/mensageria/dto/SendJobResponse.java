package com.puc.mensageria.dto;

import com.puc.mensageria.domain.SendJob;
import com.puc.mensageria.domain.SendJobStatus;
import java.time.Instant;

public record SendJobResponse(
        Long id,
        Long messageId,
        String messageSubject,
        SendJobStatus status,
        int totalSent,
        int totalFailed,
        Instant createdAt,
        Instant startedAt,
        Instant finishedAt
) {

    public static SendJobResponse from(SendJob job) {
        return new SendJobResponse(
                job.getId(),
                job.getMessage().getId(),
                job.getMessage().getSubject(),
                job.getStatus(),
                job.getTotalSent(),
                job.getTotalFailed(),
                job.getCreatedAt(),
                job.getStartedAt(),
                job.getFinishedAt()
        );
    }
}
