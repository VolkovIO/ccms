package com.example.ccms.communicationcase.application;

import com.example.ccms.communicationcase.domain.model.CommunicationCase;
import com.example.ccms.communicationcase.domain.model.CommunicationCaseId;
import com.example.ccms.communicationcase.domain.repository.CommunicationCaseRepository;
import java.time.Instant;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReceiveIncomingMessageUseCase {

  private final CommunicationCaseRepository repository;

  public void receive(ReceiveIncomingMessageCommand command) {
    CommunicationCaseId id =
        new CommunicationCaseId(UUID.fromString(command.communicationCaseId()));

    CommunicationCase communicationCase =
        repository
            .findById(id)
            .orElseThrow(
                () ->
                    new CommunicationCaseNotFoundException(
                        "Communication case not found: " + command.communicationCaseId()));

    communicationCase.receiveIncomingMessage(command.channel(), command.text(), Instant.now());

    repository.save(communicationCase);
  }
}
