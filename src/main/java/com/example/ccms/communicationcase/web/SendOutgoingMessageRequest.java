package com.example.ccms.communicationcase.web;

import com.example.ccms.communicationcase.domain.model.MessageChannel;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SendOutgoingMessageRequest(
    @Schema(example = "TELEGRAM") @NotNull MessageChannel channel,
    @Schema(example = "Your order is ready / Or start with FAIL...") @NotBlank String text) {}
