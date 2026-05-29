package com.puc.mensageria.messaging;

import com.puc.mensageria.config.RabbitMQConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class EmailProducer {

    private static final Logger log = LoggerFactory.getLogger(EmailProducer.class);

    private final RabbitTemplate rabbitTemplate;

    public EmailProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publish(SendEmailCommand command) {
        log.info("Publicando na exchange '{}' com routing key '{}': {}",
                RabbitMQConstants.EXCHANGE,
                RabbitMQConstants.ROUTING_KEY,
                command);

        rabbitTemplate.convertAndSend(
                RabbitMQConstants.EXCHANGE,
                RabbitMQConstants.ROUTING_KEY,
                command
        );
    }
}
