package org.jedi_bachelor.ioboxstarter.controller;

import org.jedi_bachelor.ioboxstarter.mapper.DeadLetterMappedMessage;
import org.jedi_bachelor.ioboxstarter.mapper.DeadLetterMapper;
import org.jedi_bachelor.ioboxstarter.service.DlqService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/dead-letters")
public class DlqController {

    private final DlqService dlqService;

    private final DeadLetterMapper deadLetterMapper;

    public DlqController(DlqService dlqService, DeadLetterMapper deadLetterMapper) {
        this.dlqService = dlqService;
        this.deadLetterMapper = deadLetterMapper;
    }

    @GetMapping
    public ResponseEntity<?> getAllMessages() {
        List<DeadLetterMappedMessage> letters = this.dlqService.findAll().stream()
                .map(this.deadLetterMapper::toDto).toList();

        return ResponseEntity.ok(letters);
    }
}
