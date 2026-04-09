package com.example.ccms.communicationcase.application.query;

import com.example.ccms.communicationcase.domain.model.CommunicationCaseStatus;

public record SearchCommunicationCasesQuery(
    String customerName, String phoneNumber, CommunicationCaseStatus status) {}
