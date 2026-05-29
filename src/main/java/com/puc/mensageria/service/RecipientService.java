package com.puc.mensageria.service;

import com.puc.mensageria.domain.Recipient;
import com.puc.mensageria.dto.CreateRecipientRequest;
import com.puc.mensageria.dto.RecipientResponse;
import com.puc.mensageria.exception.DuplicateResourceException;
import com.puc.mensageria.exception.ResourceNotFoundException;
import com.puc.mensageria.repository.RecipientRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RecipientService {

    private final RecipientRepository recipientRepository;

    public RecipientService(RecipientRepository recipientRepository) {
        this.recipientRepository = recipientRepository;
    }

    @Transactional(readOnly = true)
    public List<RecipientResponse> listAll() {
        return recipientRepository.findAll().stream()
                .map(RecipientResponse::from)
                .toList();
    }

    @Transactional
    public RecipientResponse create(CreateRecipientRequest request) {
        if (recipientRepository.existsByEmailIgnoreCase(request.email())) {
            throw new DuplicateResourceException("E-mail ja cadastrado: " + request.email());
        }

        Recipient recipient = new Recipient(
                request.email().trim().toLowerCase(),
                request.name()
        );

        return RecipientResponse.from(recipientRepository.save(recipient));
    }

    @Transactional
    public void delete(Long id) {
        if (!recipientRepository.existsById(id)) {
            throw new ResourceNotFoundException("Destinatario nao encontrado: " + id);
        }
        recipientRepository.deleteById(id);
    }
}
