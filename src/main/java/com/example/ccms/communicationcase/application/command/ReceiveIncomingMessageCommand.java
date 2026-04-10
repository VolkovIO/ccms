package com.example.ccms.communicationcase.application.command;

import com.example.ccms.communicationcase.domain.model.MessageChannel;

public record ReceiveIncomingMessageCommand(
    String communicationCaseId, MessageChannel channel, String text) {}
