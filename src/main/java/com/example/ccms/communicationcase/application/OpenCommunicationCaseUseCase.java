package com.example.ccms.communicationcase.application;

import com.example.ccms.communicationcase.domain.model.CommunicationCase;
import com.example.ccms.communicationcase.domain.model.CommunicationCaseId;
import com.example.ccms.communicationcase.domain.model.CustomerSnapshot;
import com.example.ccms.communicationcase.domain.model.ExternalOrderReference;
import com.example.ccms.communicationcase.domain.repository.CommunicationCaseRepository;
import java.time.Instant;
import org.springframework.stereotype.Service;

@Service
public class OpenCommunicationCaseUseCase {

  private final CommunicationCaseRepository repository;

  public OpenCommunicationCaseUseCase(CommunicationCaseRepository repository) {
    this.repository = repository;
  }

  public CommunicationCaseId open(OpenCommunicationCaseCommand command) {

    CustomerSnapshot customer = new CustomerSnapshot(command.fullName(), command.phoneNumber());

    ExternalOrderReference orderReference =
        new ExternalOrderReference(
            command.sourceSystem(), command.externalOrderId(), command.orderSummary());

    CommunicationCase communicationCase =
        CommunicationCase.open(
            customer, orderReference, command.contactReason(), command.createdBy(), Instant.now());

    repository.save(communicationCase);

    return communicationCase.getId();
  }
}
