package com.puc.mensageria.service;

import com.puc.mensageria.domain.EmailMessage;
import com.puc.mensageria.dto.CreateMessageRequest;
import com.puc.mensageria.dto.MessageResponse;
import com.puc.mensageria.exception.ResourceNotFoundException;
import com.puc.mensageria.repository.EmailMessageRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MessageService {

    private final EmailMessageRepository messageRepository;

    public MessageService(EmailMessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    @Transactional(readOnly = true)
    public List<MessageResponse> listAll() {
        return messageRepository.findAll().stream()
                .map(MessageResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public MessageResponse findById(Long id) {
        EmailMessage message = messageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Mensagem nao encontrada: " + id));
        return MessageResponse.from(message);
    }

    @Transactional
    public MessageResponse create(CreateMessageRequest request) {
        EmailMessage message = new EmailMessage(
                request.subject().trim(),
                request.body().trim()
        );
        return MessageResponse.from(messageRepository.save(message));
    }
}
