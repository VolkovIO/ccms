package com.example.ccms.communicationcase.infrastructure.integration.fakeprovider;

import com.example.ccms.communicationcase.domain.model.MessageChannel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record FakeProviderSendMessageRequest(
    @NotBlank String recipient, @NotNull MessageChannel channel, @NotBlank String messageText) {}
