package org.jedi_bachelor.ioboxstarter.controller;

import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class DlqController {
    private final DlqService dlqService;

    private final DeadLetterMapper deadLetterMapper;

    @GetMapping
    public ResponseEntity<?> getAllMessages() {
        List<DeadLetterMappedMessage> letters = this.dlqService.findAll().stream()
                .map(this.deadLetterMapper::toDto).toList();

        return ResponseEntity.ok(letters);
    }
}
