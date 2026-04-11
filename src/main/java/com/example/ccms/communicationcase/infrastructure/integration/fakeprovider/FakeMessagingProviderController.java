package com.example.ccms.communicationcase.infrastructure.integration.fakeprovider;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/fake-provider/messages")
@Profile("fake-provider")
@Tag(name = "Fake Messaging Provider")
public class FakeMessagingProviderController {

  private final FakeProviderCallbackClient fakeProviderCallbackClient;

  @PostMapping
  @Operation(
      summary = "Accept outgoing message in fake provider",
      description =
          """
          Simulates external messaging provider accepting an outgoing message.

          Example request:
          - POST /fake-provider/messages

          The provider returns:
          - accepted=true for normal messages
          - accepted=false when messageText contains FAIL
          """)
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

  @PostMapping("/reply")
  @Operation(
      summary = "Simulate incoming reply from fake provider",
      description =
          """
          Simulates provider callback with an incoming customer reply.

          Example request:
          - POST /fake-provider/messages/reply

          This endpoint internally calls:
          - POST /api/communication-cases/{id}/messages/incoming
          """)
  public ResponseEntity<?> simulateReply(@Valid @RequestBody FakeProviderReplyRequest request) {

    log.info(
        "Fake provider simulating reply: communicationCaseId={}, channel={}",
        request.communicationCaseId(),
        request.channel());

    FakeProviderCallbackResult result =
        fakeProviderCallbackClient.sendIncomingMessageCallback(
            request.communicationCaseId(),
            new IncomingMessageCallbackRequest(request.channel(), request.messageText()));

    if (result.successful()) {
      return ResponseEntity.ok().build();
    }

    return ResponseEntity.status(result.statusCode())
        .body(Map.of("message", result.errorMessage()));
  }
}
