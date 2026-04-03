package com.example.ccms.communicationcase.application;

import com.example.ccms.communicationcase.domain.model.ContactReason;

public record OpenCommunicationCaseCommand(
    String fullName,
    String phoneNumber,
    String sourceSystem,
    String externalOrderId,
    String orderSummary,
    ContactReason contactReason,
    String createdBy
) {
}
