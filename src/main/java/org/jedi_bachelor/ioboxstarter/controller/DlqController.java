package org.jedi_bachelor.ioboxstarter.controller;

import java.util.List;
import org.jedi_bachelor.ioboxstarter.mapper.DeadLetterMappedMessage;
import org.jedi_bachelor.ioboxstarter.mapper.DeadLetterMappingUtil;
import org.jedi_bachelor.ioboxstarter.service.DlqService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/dead-letters")
public class DlqController {

  private final DlqService dlqService;

  public DlqController(DlqService dlqService) {
    this.dlqService = dlqService;
  }

  @GetMapping
  public ResponseEntity<?> getAllMessages() {
    List<DeadLetterMappedMessage> letters =
        dlqService.findAll().stream().map(DeadLetterMappingUtil::toDto).toList();

    return ResponseEntity.ok(letters);
  }
}
