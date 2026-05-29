package com.puc.mensageria.controller;

import com.puc.mensageria.dto.CreateRecipientRequest;
import com.puc.mensageria.dto.RecipientResponse;
import com.puc.mensageria.service.RecipientService;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/recipients")
public class RecipientController {

    private final RecipientService recipientService;

    public RecipientController(RecipientService recipientService) {
        this.recipientService = recipientService;
    }

    @GetMapping
    public List<RecipientResponse> list() {
        return recipientService.listAll();
    }

    @PostMapping
    public ResponseEntity<RecipientResponse> create(@Valid @RequestBody CreateRecipientRequest request) {
        RecipientResponse created = recipientService.create(request);
        return ResponseEntity
                .created(URI.create("/api/recipients/" + created.id()))
                .body(created);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        recipientService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
