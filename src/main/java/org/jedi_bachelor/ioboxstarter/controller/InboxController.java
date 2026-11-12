package org.jedi_bachelor.ioboxstarter.controller;

import org.jedi_bachelor.ioboxstarter.mapper.InboxMappedMessage;
import org.jedi_bachelor.ioboxstarter.mapper.InboxMapper;
import org.jedi_bachelor.ioboxstarter.service.InboxService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/inbox")
public class InboxController {

    private final InboxService inboxService;

    private final InboxMapper inboxMapper;

    public InboxController(InboxService inboxService, InboxMapper inboxMapper) {
        this.inboxService = inboxService;
        this.inboxMapper = inboxMapper;
    }

    @GetMapping
    @SuppressWarnings("UnusedDeclaration")
    public ResponseEntity<?> getAllInboxMessages() {
        List<InboxMappedMessage> messageList = this.inboxService.getAllMessages().stream()
                .map(this.inboxMapper::toDto).toList();

        return ResponseEntity.ok(messageList);
    }
}
