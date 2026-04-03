package com.example.ccms.communicationcase.application;

import com.example.ccms.communicationcase.domain.model.*;
import com.example.ccms.communicationcase.domain.repository.CommunicationCaseRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class OpenCommunicationCaseUseCase {

  private final CommunicationCaseRepository repository;

  public OpenCommunicationCaseUseCase(CommunicationCaseRepository repository) {
    this.repository = repository;
  }

  public CommunicationCaseId open(OpenCommunicationCaseCommand command) {

    CustomerSnapshot customer = new CustomerSnapshot(
        command.fullName(),
        command.phoneNumber()
    );

    ExternalOrderReference orderReference = new ExternalOrderReference(
        command.sourceSystem(),
        command.externalOrderId(),
        command.orderSummary()
    );

    CommunicationCase communicationCase = CommunicationCase.open(
        customer,
        orderReference,
        command.contactReason(),
        command.createdBy(),
        Instant.now()
    );

    repository.save(communicationCase);

    return communicationCase.getId();
  }
}
