package com.example.ccms.communicationcase.infrastructure.integration.webhook;

import com.example.ccms.communicationcase.application.command.ReceiveIncomingMessageCommand;
import com.example.ccms.communicationcase.application.command.ReceiveIncomingMessageUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/integrations/messages/incoming")
@RequiredArgsConstructor
public class IncomingMessageWebhookController {

  private final ReceiveIncomingMessageUseCase receiveIncomingMessageUseCase;

  @PostMapping
  public ResponseEntity<Void> receiveIncomingMessage(
      @Valid @RequestBody IncomingMessageWebhookRequest request) {

    log.info(
        "Received incoming message webhook: communicationCaseId={}, channel={}",
        request.communicationCaseId(),
        request.channel());

    receiveIncomingMessageUseCase.receive(
        new ReceiveIncomingMessageCommand(
            request.communicationCaseId(), request.channel(), request.messageText()));

    return ResponseEntity.ok().build();
  }
}
