package com.example.ccms.communicationcase.application.port;

public record SendMessageResult(
    boolean successful, String providerMessageId, String failureReason) {

  public static SendMessageResult success(String providerMessageId) {
    return new SendMessageResult(true, providerMessageId, null);
  }

  public static SendMessageResult failure(String failureReason) {
    return new SendMessageResult(false, null, failureReason);
  }
}
