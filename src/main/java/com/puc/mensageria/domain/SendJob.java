package com.puc.mensageria.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "send_jobs")
public class SendJob {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "message_id", nullable = false)
    private EmailMessage message;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SendJobStatus status = SendJobStatus.PENDING;

    @Column(name = "total_sent", nullable = false)
    private int totalSent = 0;

    @Column(name = "total_failed", nullable = false)
    private int totalFailed = 0;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    @Column(name = "started_at")
    private Instant startedAt;

    @Column(name = "finished_at")
    private Instant finishedAt;

    protected SendJob() {
    }

    public SendJob(EmailMessage message) {
        this.message = message;
    }

    public Long getId() {
        return id;
    }

    public EmailMessage getMessage() {
        return message;
    }

    public SendJobStatus getStatus() {
        return status;
    }

    public void setStatus(SendJobStatus status) {
        this.status = status;
    }

    public int getTotalSent() {
        return totalSent;
    }

    public void setTotalSent(int totalSent) {
        this.totalSent = totalSent;
    }

    public int getTotalFailed() {
        return totalFailed;
    }

    public void setTotalFailed(int totalFailed) {
        this.totalFailed = totalFailed;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(Instant startedAt) {
        this.startedAt = startedAt;
    }

    public Instant getFinishedAt() {
        return finishedAt;
    }

    public void setFinishedAt(Instant finishedAt) {
        this.finishedAt = finishedAt;
    }
}
