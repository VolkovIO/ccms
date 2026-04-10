package com.example.ccms.communicationcase.application.command;

import com.example.ccms.communicationcase.application.exception.CommunicationCaseNotFoundException;
import com.example.ccms.communicationcase.domain.model.CommunicationCase;
import com.example.ccms.communicationcase.domain.model.CommunicationCaseId;
import com.example.ccms.communicationcase.domain.repository.CommunicationCaseRepository;
import java.time.Instant;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CloseCommunicationCaseUseCase {

  private final CommunicationCaseRepository repository;

  public void close(CloseCommunicationCaseCommand command) {
    CommunicationCaseId id =
        new CommunicationCaseId(UUID.fromString(command.communicationCaseId()));

    CommunicationCase communicationCase =
        repository
            .findById(id)
            .orElseThrow(
                () ->
                    new CommunicationCaseNotFoundException(
                        "Communication case not found: " + command.communicationCaseId()));

    communicationCase.close(Instant.now());

    repository.save(communicationCase);
  }
}
