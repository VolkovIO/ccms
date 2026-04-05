package com.example.ccms.communicationcase.web;

import com.example.ccms.communicationcase.domain.model.CallAttemptResult;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RegisterCallAttemptRequest(
    @NotBlank String attemptedBy, @NotNull CallAttemptResult result) {}
