package com.example.ccms.communicationcase.web;

import com.example.ccms.communicationcase.domain.model.CallAttemptResult;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RegisterCallAttemptRequest(
    @NotBlank String attemptedBy,
    @Schema(example = "REACHED/NOT_REACHED") @NotNull CallAttemptResult result) {}
