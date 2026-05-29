package com.puc.mensageria.messaging;

import com.puc.mensageria.config.RabbitMQConstants;
import com.puc.mensageria.service.EmailProcessingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class EmailConsumer {

    private static final Logger log = LoggerFactory.getLogger(EmailConsumer.class);

    private final EmailProcessingService emailProcessingService;

    public EmailConsumer(EmailProcessingService emailProcessingService) {
        this.emailProcessingService = emailProcessingService;
    }

    @RabbitListener(queues = RabbitMQConstants.QUEUE)
    public void consume(SendEmailCommand command) {
        log.info("Mensagem recebida da fila '{}': {}", RabbitMQConstants.QUEUE, command);
        try {
            emailProcessingService.process(command);
        } catch (Exception ex) {
            log.error("Erro ao processar mensagem da fila (jobId={}): {}",
                    command.jobId(), ex.getMessage());
        }
    }
}
