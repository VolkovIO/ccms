package com.example.ccms.communicationcase.application.port;

public record SendMessageResult(boolean successful) {

  public static SendMessageResult success() {
    return new SendMessageResult(true);
  }

  public static SendMessageResult failure() {
    return new SendMessageResult(false);
  }
}
