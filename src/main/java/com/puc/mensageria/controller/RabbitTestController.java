package com.puc.mensageria.controller;

import com.puc.mensageria.config.RabbitMQConstants;
import com.puc.mensageria.messaging.EmailProducer;
import com.puc.mensageria.messaging.SendEmailCommand;
import java.time.Instant;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/rabbit")
public class RabbitTestController {

    private final EmailProducer emailProducer;

    public RabbitTestController(EmailProducer emailProducer) {
        this.emailProducer = emailProducer;
    }

    @PostMapping("/test")
    public ResponseEntity<Map<String, Object>> publishTestMessage() {
        SendEmailCommand command = new SendEmailCommand(0L, 1L, Instant.now());
        emailProducer.publish(command);

        return ResponseEntity.accepted().body(Map.of(
                "message", "Mensagem publicada na fila com sucesso",
                "exchange", RabbitMQConstants.EXCHANGE,
                "routingKey", RabbitMQConstants.ROUTING_KEY,
                "queue", RabbitMQConstants.QUEUE,
                "payload", command
        ));
    }
}
