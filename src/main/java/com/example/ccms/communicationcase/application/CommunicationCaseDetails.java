package com.example.ccms.communicationcase.application;

import com.example.ccms.communicationcase.domain.model.CommunicationCaseStatus;
import com.example.ccms.communicationcase.domain.model.ContactReason;
import java.time.Instant;
import java.util.List;

public record CommunicationCaseDetails(
    String id,
    String customerFullName,
    String customerPhoneNumber,
    String sourceSystem,
    String externalOrderId,
    String orderSummary,
    ContactReason contactReason,
    CommunicationCaseStatus status,
    String createdBy,
    Instant openedAt,
    Instant closedAt,
    List<CallAttemptDetails> callAttempts,
    List<MessageDetails> messages
) {

  public record CallAttemptDetails(
      String attemptedBy,
      Instant attemptedAt,
      String result
  ) {

  }

  public record MessageDetails(
      String direction,
      String channel,
      String text,
      String deliveryStatus,
      Instant createdAt
  ) {

  }
}
