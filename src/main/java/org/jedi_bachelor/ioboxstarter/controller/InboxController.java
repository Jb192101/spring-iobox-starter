package org.jedi_bachelor.ioboxstarter.controller;

import lombok.RequiredArgsConstructor;
import org.jedi_bachelor.ioboxstarter.mapper.InboxMappedMessage;
import org.jedi_bachelor.ioboxstarter.mapper.InboxMapper;
import org.jedi_bachelor.ioboxstarter.model.InboxMessage;
import org.jedi_bachelor.ioboxstarter.service.InboxService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/inbox")
@RequiredArgsConstructor
public class InboxController {
    private final InboxService inboxService;

    private final InboxMapper inboxMapper;

    @GetMapping
    public ResponseEntity<?> getAllInboxMessages() {
        List<InboxMappedMessage> messageList = this.inboxService.getAllMessages().stream()
                .map(this.inboxMapper::toDto).toList();

        return ResponseEntity.ok(messageList);
    }
}
