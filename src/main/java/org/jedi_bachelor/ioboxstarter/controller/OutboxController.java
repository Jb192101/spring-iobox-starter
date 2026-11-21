package org.jedi_bachelor.ioboxstarter.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.jedi_bachelor.ioboxstarter.mapper.OutboxMappedMessage;
import org.jedi_bachelor.ioboxstarter.mapper.OutboxMappingUtil;
import org.jedi_bachelor.ioboxstarter.service.OutboxService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/outbox")
@RequiredArgsConstructor
public class OutboxController {

  private final OutboxService outboxService;

  @GetMapping
  public ResponseEntity<?> getAllOutboxMessages() {
    List<OutboxMappedMessage> messageList =
        outboxService.getAllMessages().stream().map(OutboxMappingUtil::toDto).toList();

    return ResponseEntity.ok(messageList);
  }
}
