package com.example.ccms.communicationcase.infrastructure.messaging;

import com.example.ccms.communicationcase.application.port.OutgoingMessageSendRequest;
import com.example.ccms.communicationcase.application.port.OutgoingMessageSender;
import com.example.ccms.communicationcase.application.port.SendMessageResult;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Profile("stub-messaging")
public class StubOutgoingMessageSender implements OutgoingMessageSender {

  @Override
  public SendMessageResult send(OutgoingMessageSendRequest request) {
    log.info(
        "Stub sending message: channel={}, phoneNumber={}, text={}",
        request.channel(),
        request.phoneNumber(),
        request.text());

    if (request.text() != null && request.text().contains("FAIL")) {
      log.warn(
          "Stub provider rejected message: channel={}, phoneNumber={}",
          request.channel(),
          request.phoneNumber());

      return SendMessageResult.failure("Stub provider rejected message");
    }

    return SendMessageResult.success("stub-" + UUID.randomUUID());
  }
}
