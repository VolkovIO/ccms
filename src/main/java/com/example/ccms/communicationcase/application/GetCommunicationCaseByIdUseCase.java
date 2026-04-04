package com.example.ccms.communicationcase.application;

import com.example.ccms.communicationcase.domain.model.CallAttempt;
import com.example.ccms.communicationcase.domain.model.CommunicationCase;
import com.example.ccms.communicationcase.domain.model.CommunicationCaseId;
import com.example.ccms.communicationcase.domain.model.Message;
import com.example.ccms.communicationcase.domain.repository.CommunicationCaseRepository;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class GetCommunicationCaseByIdUseCase {

  private final CommunicationCaseRepository repository;

  public GetCommunicationCaseByIdUseCase(CommunicationCaseRepository repository) {
    this.repository = repository;
  }

  public CommunicationCaseDetails getById(GetCommunicationCaseByIdQuery query) {
    CommunicationCaseId id = new CommunicationCaseId(UUID.fromString(query.communicationCaseId()));

    CommunicationCase communicationCase = repository.findById(id)
        .orElseThrow(() -> new CommunicationCaseNotFoundException(
            "Communication case not found: " + query.communicationCaseId()
        ));

    List<CommunicationCaseDetails.CallAttemptDetails> callAttemptDetails = communicationCase.getCallAttempts()
        .stream()
        .map(this::mapCallAttempt)
        .toList();

    List<CommunicationCaseDetails.MessageDetails> messageDetails = communicationCase.getMessages()
        .stream()
        .map(this::mapMessage)
        .toList();

    return new CommunicationCaseDetails(
        communicationCase.getId().toString(),
        communicationCase.getCustomer().fullName(),
        communicationCase.getCustomer().phoneNumber(),
        communicationCase.getExternalOrderReference().sourceSystem(),
        communicationCase.getExternalOrderReference().externalOrderId(),
        communicationCase.getExternalOrderReference().orderSummary(),
        communicationCase.getContactReason(),
        communicationCase.getStatus(),
        communicationCase.getCreatedBy(),
        communicationCase.getOpenedAt(),
        communicationCase.getClosedAt(),
        callAttemptDetails,
        messageDetails
    );
  }

  private CommunicationCaseDetails.CallAttemptDetails mapCallAttempt(CallAttempt callAttempt) {
    return new CommunicationCaseDetails.CallAttemptDetails(
        callAttempt.getAttemptedBy(),
        callAttempt.getAttemptedAt(),
        callAttempt.getResult().name()
    );
  }

  private CommunicationCaseDetails.MessageDetails mapMessage(Message message) {
    return new CommunicationCaseDetails.MessageDetails(
        message.getDirection().name(),
        message.getChannel().name(),
        message.getText(),
        message.getDeliveryStatus() != null ? message.getDeliveryStatus().name() : null,
        message.getCreatedAt()
    );
  }
}
