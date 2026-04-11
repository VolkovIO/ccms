package com.example.ccms.communicationcase.infrastructure.integration.fakeprovider;

import jakarta.validation.Valid;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/fake-provider/messages")
@Profile("fake-provider")
public class FakeMessagingProviderController {

  @PostMapping
  public ResponseEntity<FakeProviderSendMessageResponse> sendMessage(
      @Valid @RequestBody FakeProviderSendMessageRequest request) {

    log.info(
        "Fake provider received message: channel={}, recipient={}, text={}",
        request.channel(),
        request.recipient(),
        request.messageText());

    if (request.messageText() != null && request.messageText().contains("FAIL")) {
      log.warn(
          "Fake provider rejected message: channel={}, recipient={}",
          request.channel(),
          request.recipient());

      return ResponseEntity.ok(
          FakeProviderSendMessageResponse.rejected("Fake provider rejected message"));
    }

    return ResponseEntity.ok(FakeProviderSendMessageResponse.accepted("fake-" + UUID.randomUUID()));
  }
}
