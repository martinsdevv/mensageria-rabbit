package com.puc.mensageria.service;

import com.puc.mensageria.domain.EmailMessage;
import com.puc.mensageria.domain.SendJob;
import com.puc.mensageria.dto.SendJobResponse;
import com.puc.mensageria.exception.BusinessRuleException;
import com.puc.mensageria.exception.ResourceNotFoundException;
import com.puc.mensageria.messaging.EmailProducer;
import com.puc.mensageria.messaging.SendEmailCommand;
import com.puc.mensageria.repository.EmailMessageRepository;
import com.puc.mensageria.repository.RecipientRepository;
import com.puc.mensageria.repository.SendJobRepository;
import java.time.Instant;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Service
public class SendService {

    private static final Logger log = LoggerFactory.getLogger(SendService.class);

    private final EmailMessageRepository messageRepository;
    private final RecipientRepository recipientRepository;
    private final SendJobRepository sendJobRepository;
    private final EmailProducer emailProducer;

    public SendService(
            EmailMessageRepository messageRepository,
            RecipientRepository recipientRepository,
            SendJobRepository sendJobRepository,
            EmailProducer emailProducer
    ) {
        this.messageRepository = messageRepository;
        this.recipientRepository = recipientRepository;
        this.sendJobRepository = sendJobRepository;
        this.emailProducer = emailProducer;
    }

    @Transactional(readOnly = true)
    public List<SendJobResponse> listJobs() {
        return sendJobRepository.findAllWithMessageOrderByCreatedAtDesc().stream()
                .map(SendJobResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public SendJobResponse findJob(Long id) {
        SendJob job = sendJobRepository.findByIdWithMessage(id)
                .orElseThrow(() -> new ResourceNotFoundException("Job nao encontrado: " + id));
        return SendJobResponse.from(job);
    }

    @Transactional
    public SendJobResponse requestSend(Long messageId) {
        EmailMessage message = messageRepository.findById(messageId)
                .orElseThrow(() -> new ResourceNotFoundException("Mensagem nao encontrada: " + messageId));

        if (recipientRepository.count() == 0) {
            throw new BusinessRuleException("Cadastre ao menos um destinatario antes de solicitar o envio");
        }

        SendJob job = sendJobRepository.save(new SendJob(message));

        SendEmailCommand command = new SendEmailCommand(
                job.getId(),
                message.getId(),
                Instant.now()
        );

        Long jobId = job.getId();
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                emailProducer.publish(command);
                log.info("Envio solicitado: jobId={}, messageId={} — mensagem publicada na fila apos commit",
                        jobId, message.getId());
            }
        });

        return SendJobResponse.from(sendJobRepository.findByIdWithMessage(job.getId()).orElseThrow());
    }
}
