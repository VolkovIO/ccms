package com.example.ccms.communicationcase.web;

import com.example.ccms.communicationcase.domain.model.MessageChannel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ReceiveIncomingMessageRequest(
    @NotNull MessageChannel channel, @NotBlank String text) {}
