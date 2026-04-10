package com.example.ccms.communicationcase.infrastructure.messaging;

import com.example.ccms.communicationcase.application.port.OutgoingMessageSender;
import com.example.ccms.communicationcase.application.port.SendMessageResult;
import com.example.ccms.communicationcase.application.port.SendOutgoingMessageRequest;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile({"in-memory", "jdbc"})
public class StubOutgoingMessageSender implements OutgoingMessageSender {

  @Override
  public SendMessageResult send(SendOutgoingMessageRequest request) {
    if (request.text() != null && request.text().contains("FAIL")) {
      return SendMessageResult.failure("Stub provider rejected message");
    }

    return SendMessageResult.success("stub-message-id");
  }
}
