package com.example.ccms.communicationcase.application;

import com.example.ccms.communicationcase.domain.model.CallAttemptResult;

public record RegisterCallAttemptCommand(
    String communicationCaseId, String attemptedBy, CallAttemptResult result) {}
