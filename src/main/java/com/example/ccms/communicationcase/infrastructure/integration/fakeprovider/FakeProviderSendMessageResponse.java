package com.example.ccms.communicationcase.infrastructure.integration.fakeprovider;

public record FakeProviderSendMessageResponse(
    boolean accepted, String providerMessageId, String failureReason) {

  public static FakeProviderSendMessageResponse accepted(String providerMessageId) {
    return new FakeProviderSendMessageResponse(true, providerMessageId, null);
  }

  public static FakeProviderSendMessageResponse rejected(String failureReason) {
    return new FakeProviderSendMessageResponse(false, null, failureReason);
  }
}
