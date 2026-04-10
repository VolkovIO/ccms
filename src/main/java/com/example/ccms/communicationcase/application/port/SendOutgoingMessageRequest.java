package com.example.ccms.communicationcase.application.port;

import com.example.ccms.communicationcase.domain.model.MessageChannel;

public record SendOutgoingMessageRequest(String phoneNumber, MessageChannel channel, String text) {}
