package com.example.ccms.communicationcase.application;

import com.example.ccms.communicationcase.domain.model.CommunicationCase;
import com.example.ccms.communicationcase.domain.model.CommunicationCaseId;
import com.example.ccms.communicationcase.domain.repository.CommunicationCaseRepository;
import java.time.Instant;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class RegisterCallAttemptUseCase {

  private final CommunicationCaseRepository repository;

  public RegisterCallAttemptUseCase(CommunicationCaseRepository repository) {
    this.repository = repository;
  }

  public void register(RegisterCallAttemptCommand command) {
    CommunicationCaseId id =
        new CommunicationCaseId(UUID.fromString(command.communicationCaseId()));

    CommunicationCase communicationCase =
        repository
            .findById(id)
            .orElseThrow(
                () ->
                    new CommunicationCaseNotFoundException(
                        "Communication case not found: " + command.communicationCaseId()));

    communicationCase.registerCallAttempt(command.attemptedBy(), command.result(), Instant.now());

    repository.save(communicationCase);
  }
}
