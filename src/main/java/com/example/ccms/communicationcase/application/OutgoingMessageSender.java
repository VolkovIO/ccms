package com.example.ccms.communicationcase.application;

import com.example.ccms.communicationcase.domain.model.MessageChannel;

@SuppressWarnings("PMD.ImplicitFunctionalInterface")
public interface OutgoingMessageSender {

  SendMessageResult send(String phoneNumber, MessageChannel channel, String text);
}
