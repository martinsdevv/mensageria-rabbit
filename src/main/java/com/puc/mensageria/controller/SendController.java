package com.puc.mensageria.controller;

import com.puc.mensageria.dto.SendJobResponse;
import com.puc.mensageria.service.SendService;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class SendController {

    private final SendService sendService;

    public SendController(SendService sendService) {
        this.sendService = sendService;
    }

    @PostMapping("/send/{messageId}")
    public ResponseEntity<SendJobResponse> requestSend(@PathVariable Long messageId) {
        SendJobResponse job = sendService.requestSend(messageId);
        return ResponseEntity
                .accepted()
                .location(URI.create("/api/jobs/" + job.id()))
                .body(job);
    }

    @GetMapping("/jobs")
    public List<SendJobResponse> listJobs() {
        return sendService.listJobs();
    }

    @GetMapping("/jobs/{id}")
    public SendJobResponse findJob(@PathVariable Long id) {
        return sendService.findJob(id);
    }
}
