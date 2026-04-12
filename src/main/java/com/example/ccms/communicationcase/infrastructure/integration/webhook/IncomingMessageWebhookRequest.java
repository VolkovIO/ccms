package com.example.ccms.communicationcase.infrastructure.integration.webhook;

import com.example.ccms.communicationcase.domain.model.MessageChannel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record IncomingMessageWebhookRequest(
    @NotBlank String communicationCaseId,
    @NotNull MessageChannel channel,
    @NotBlank String messageText) {}
