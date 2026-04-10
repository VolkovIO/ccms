package com.example.ccms.communicationcase.application.port;

@SuppressWarnings("PMD.ImplicitFunctionalInterface")
public interface OutgoingMessageSender {

  SendMessageResult send(SendOutgoingMessageRequest request);
}
