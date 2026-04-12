package com.example.ccms.communicationcase.infrastructure.integration.fakeprovider;

import com.example.ccms.communicationcase.domain.model.MessageChannel;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record FakeProviderReplyRequest(
    @Schema(example = "8f8f2b0d-3e2c-4e55-9d51-5b6a8a3c1f77") @NotBlank String communicationCaseId,
    @Schema(example = "TELEGRAM") @NotNull MessageChannel channel,
    @Schema(example = "I will come tomorrow") @NotBlank String messageText) {}
