package com.example.ccms.communicationcase.domain.model;

public record CustomerSnapshot(
    String fullName,
    String phoneNumber
) {
  public CustomerSnapshot {
    if (fullName == null || fullName.isBlank()) {
      throw new IllegalArgumentException("fullName must not be blank");
    }
    if (phoneNumber == null || phoneNumber.isBlank()) {
      throw new IllegalArgumentException("phoneNumber must not be blank");
    }
  }
}
