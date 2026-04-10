package com.example.ccms.communicationcase.application.command;

import com.example.ccms.communicationcase.application.exception.CommunicationCaseNotFoundException;
import com.example.ccms.communicationcase.application.port.OutgoingMessageSender;
import com.example.ccms.communicationcase.application.port.SendMessageResult;
import com.example.ccms.communicationcase.domain.model.CommunicationCase;
import com.example.ccms.communicationcase.domain.model.CommunicationCaseId;
import com.example.ccms.communicationcase.domain.model.Message;
import com.example.ccms.communicationcase.domain.repository.CommunicationCaseRepository;
import java.time.Instant;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SendOutgoingMessageUseCase {

  private final CommunicationCaseRepository repository;
  private final OutgoingMessageSender outgoingMessageSender;

  public void send(SendOutgoingMessageCommand command) {
    CommunicationCaseId id =
        new CommunicationCaseId(UUID.fromString(command.communicationCaseId()));

    CommunicationCase communicationCase =
        repository
            .findById(id)
            .orElseThrow(
                () ->
                    new CommunicationCaseNotFoundException(
                        "Communication case not found: " + command.communicationCaseId()));

    Message message =
        communicationCase.prepareOutgoingMessage(command.channel(), command.text(), Instant.now());

    communicationCase.requestMessage(message);

    SendMessageResult result =
        outgoingMessageSender.send(
            communicationCase.getCustomer().phoneNumber(), command.channel(), command.text());

    if (result.successful()) {
      communicationCase.markMessageSent(message);
    } else {
      communicationCase.markMessageFailed(message);
    }

    repository.save(communicationCase);
  }
}
