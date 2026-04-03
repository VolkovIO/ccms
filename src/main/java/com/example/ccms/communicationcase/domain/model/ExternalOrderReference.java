package com.example.ccms.communicationcase.domain.model;

public record ExternalOrderReference(
    String sourceSystem,
    String externalOrderId,
    String orderSummary
) {
  public ExternalOrderReference {
    if (sourceSystem == null || sourceSystem.isBlank()) {
      throw new IllegalArgumentException("sourceSystem must not be blank");
    }
    if (externalOrderId == null || externalOrderId.isBlank()) {
      throw new IllegalArgumentException("externalOrderId must not be blank");
    }
    if (orderSummary == null || orderSummary.isBlank()) {
      throw new IllegalArgumentException("orderSummary must not be blank");
    }
  }
}
