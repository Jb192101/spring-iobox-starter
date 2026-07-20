package org.jedi_bachelor.ioboxstarter.controller;

import lombok.RequiredArgsConstructor;
import org.jedi_bachelor.ioboxstarter.mapper.OutboxMappedMessage;
import org.jedi_bachelor.ioboxstarter.mapper.OutboxMapper;
import org.jedi_bachelor.ioboxstarter.model.OutboxMessage;
import org.jedi_bachelor.ioboxstarter.service.OutboxService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/outbox")
@RequiredArgsConstructor
public class OutboxController {
    private final OutboxService outboxService;

    private final OutboxMapper outboxMapper;

    @GetMapping
    public ResponseEntity<?> getAllOutboxMessages() {
        List<OutboxMappedMessage> messageList = this.outboxService.getAllMessages().stream()
                .map(this.outboxMapper::toDto).toList();

        return ResponseEntity.ok(messageList);
    }
}
