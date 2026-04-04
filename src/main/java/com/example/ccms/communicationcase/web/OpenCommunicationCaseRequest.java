package com.example.ccms.communicationcase.web;

import com.example.ccms.communicationcase.domain.model.ContactReason;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record OpenCommunicationCaseRequest(
    @Schema(example = "Ivan Petrov") @NotBlank String fullName,
    @Schema(example = "+79001234567") @NotBlank String phoneNumber,
    @Schema(example = "1C") @NotBlank String sourceSystem,
    @Schema(example = "ORDER-123") @NotBlank String externalOrderId,
    @Schema(example = "Washing machine ready for pickup") @NotBlank String orderSummary,
    @NotNull ContactReason contactReason,
    @Schema(example = "operator1") @NotBlank String createdBy) {}
