package com.example.ccms.communicationcase.domain.model;

import java.util.Objects;
import java.util.UUID;

public record CommunicationCaseId(UUID value) {

  public CommunicationCaseId {
    Objects.requireNonNull(value, "value must not be null");
  }

  public static CommunicationCaseId newId() {
    return new CommunicationCaseId(UUID.randomUUID());
  }

  @Override
  public String toString() {
    return value.toString();
  }
}
