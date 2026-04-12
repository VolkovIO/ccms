package com.example.ccms.communicationcase.infrastructure.integration.fakeprovider;

import com.example.ccms.communicationcase.domain.model.MessageChannel;

public record IncomingMessageCallbackRequest(MessageChannel channel, String text) {}
