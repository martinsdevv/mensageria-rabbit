package com.puc.mensageria.controller;

import com.puc.mensageria.dto.CreateMessageRequest;
import com.puc.mensageria.dto.MessageResponse;
import com.puc.mensageria.service.MessageService;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/messages")
public class MessageController {

    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @GetMapping
    public List<MessageResponse> list() {
        return messageService.listAll();
    }

    @GetMapping("/{id}")
    public MessageResponse findById(@PathVariable Long id) {
        return messageService.findById(id);
    }

    @PostMapping
    public ResponseEntity<MessageResponse> create(@Valid @RequestBody CreateMessageRequest request) {
        MessageResponse created = messageService.create(request);
        return ResponseEntity
                .created(URI.create("/api/messages/" + created.id()))
                .body(created);
    }
}
