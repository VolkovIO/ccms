package com.example.ccms.communicationcase.infrastructure.integration.fakeprovider;

public record FakeProviderCallbackResult(boolean successful, int statusCode, String errorMessage) {

  public static FakeProviderCallbackResult success() {
    return new FakeProviderCallbackResult(true, 200, null);
  }

  public static FakeProviderCallbackResult failure(int statusCode, String errorMessage) {
    return new FakeProviderCallbackResult(false, statusCode, errorMessage);
  }
}
