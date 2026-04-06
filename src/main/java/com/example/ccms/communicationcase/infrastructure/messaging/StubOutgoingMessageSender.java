package com.example.ccms.communicationcase.infrastructure.messaging;

import com.example.ccms.communicationcase.application.OutgoingMessageSender;
import com.example.ccms.communicationcase.application.SendMessageResult;
import com.example.ccms.communicationcase.domain.model.MessageChannel;
import org.springframework.stereotype.Component;

@Component
public class StubOutgoingMessageSender implements OutgoingMessageSender {

  @Override
  public SendMessageResult send(String phoneNumber, MessageChannel channel, String text) {
    if (text != null && text.startsWith("FAIL")) {
      return SendMessageResult.failure();
    }

    return SendMessageResult.success();
  }
}
