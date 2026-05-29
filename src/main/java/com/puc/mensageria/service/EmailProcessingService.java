package com.puc.mensageria.service;

import com.puc.mensageria.domain.EmailMessage;
import com.puc.mensageria.domain.Recipient;
import com.puc.mensageria.domain.SendJob;
import com.puc.mensageria.domain.SendJobStatus;
import com.puc.mensageria.messaging.SendEmailCommand;
import com.puc.mensageria.repository.RecipientRepository;
import com.puc.mensageria.repository.SendJobRepository;
import java.time.Instant;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EmailProcessingService {

    private static final Logger log = LoggerFactory.getLogger(EmailProcessingService.class);

    private final SendJobRepository sendJobRepository;
    private final RecipientRepository recipientRepository;
    private final MailService mailService;
    private final long sendDelayMs;

    public EmailProcessingService(
            SendJobRepository sendJobRepository,
            RecipientRepository recipientRepository,
            MailService mailService,
            @org.springframework.beans.factory.annotation.Value("${app.mail.send-delay-ms:2000}") long sendDelayMs
    ) {
        this.sendJobRepository = sendJobRepository;
        this.recipientRepository = recipientRepository;
        this.mailService = mailService;
        this.sendDelayMs = sendDelayMs;
    }

    @Transactional
    public void process(SendEmailCommand command) {
        SendJob job = sendJobRepository.findByIdWithMessage(command.jobId()).orElse(null);

        if (job == null) {
            log.warn("Job {} nao encontrado no banco — mensagem descartada (possivel race condition ou job invalido)",
                    command.jobId());
            return;
        }

        if (job.getStatus() != SendJobStatus.PENDING) {
            log.warn("Job {} ja processado (status={}), ignorando mensagem duplicada",
                    job.getId(), job.getStatus());
            return;
        }

        job.setStatus(SendJobStatus.PROCESSING);
        job.setStartedAt(Instant.now());
        sendJobRepository.save(job);

        log.info("Iniciando processamento do job {} para mensagem {}", job.getId(), command.messageId());

        EmailMessage emailMessage = job.getMessage();
        List<Recipient> recipients = recipientRepository.findAll();

        int sent = 0;
        int failed = 0;

        for (int i = 0; i < recipients.size(); i++) {
            Recipient recipient = recipients.get(i);

            if (i > 0) {
                pauseBetweenSends();
            }

            if (sendWithRetry(recipient.getEmail(), emailMessage.getSubject(), emailMessage.getBody(), job.getId())) {
                sent++;
            } else {
                failed++;
            }
        }

        job.setTotalSent(sent);
        job.setTotalFailed(failed);
        job.setFinishedAt(Instant.now());
        job.setStatus(failed > 0 && sent == 0 ? SendJobStatus.FAILED : SendJobStatus.COMPLETED);
        sendJobRepository.save(job);

        log.info("Job {} finalizado: {} enviados, {} falhas, status={}",
                job.getId(), sent, failed, job.getStatus());
    }

    private void pauseBetweenSends() {
        try {
            Thread.sleep(sendDelayMs);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }

    private boolean sendWithRetry(String to, String subject, String body, Long jobId) {
        int maxAttempts = 4;

        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                mailService.send(to, subject, body);
                return true;
            } catch (Exception ex) {
                boolean rateLimited = isRateLimitError(ex);
                if (rateLimited && attempt < maxAttempts) {
                    long backoffMs = sendDelayMs * attempt;
                    log.warn("Rate limit Mailtrap para {} (job {}), tentativa {}/{} — aguardando {}ms",
                            to, jobId, attempt, maxAttempts, backoffMs);
                    sleepQuietly(backoffMs);
                    continue;
                }
                log.error("Falha ao enviar e-mail para {} (job {}): {}", to, jobId, ex.getMessage());
                return false;
            }
        }
        return false;
    }

    private boolean isRateLimitError(Exception ex) {
        String message = ex.getMessage();
        return message != null && message.contains("Too many emails per second");
    }

    private void sleepQuietly(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }
}
