package org.jedi_bachelor.ioboxstarter.controller;

import java.util.List;
import org.jedi_bachelor.ioboxstarter.mapper.InboxMappedMessage;
import org.jedi_bachelor.ioboxstarter.mapper.InboxMappingUtil;
import org.jedi_bachelor.ioboxstarter.service.InboxService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/inbox")
public class InboxController {

    private final InboxService inboxService;

  public InboxController(InboxService inboxService) {
        this.inboxService = inboxService;
    }

    @GetMapping
    public ResponseEntity<?> getAllInboxMessages() {
    List<InboxMappedMessage> messageList =
        inboxService.getAllMessages().stream().map(InboxMappingUtil::toDto).toList();

        return ResponseEntity.ok(messageList);
    }
}
