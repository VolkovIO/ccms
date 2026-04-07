package com.example.ccms.communicationcase.application;

import com.example.ccms.communicationcase.domain.model.CommunicationCaseStatus;
import com.example.ccms.communicationcase.domain.model.ContactReason;
import java.time.Instant;

public record CommunicationCaseListItem(
    String id,
    String customerFullName,
    String customerPhoneNumber,
    String sourceSystem,
    String externalOrderId,
    String orderSummary,
    ContactReason contactReason,
    CommunicationCaseStatus status,
    Instant openedAt,
    Instant closedAt) {}
