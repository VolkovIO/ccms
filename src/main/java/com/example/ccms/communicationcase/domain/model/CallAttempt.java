package com.example.ccms.communicationcase.domain.model;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public final class CallAttempt {

  private final UUID id;
  private final String attemptedBy;
  private final Instant attemptedAt;
  private final CallAttemptResult result;

  public CallAttempt(String attemptedBy, Instant attemptedAt, CallAttemptResult result) {
    this(UUID.randomUUID(), attemptedBy, attemptedAt, result);
  }

  private CallAttempt(UUID id, String attemptedBy, Instant attemptedAt, CallAttemptResult result) {
    this.id = Objects.requireNonNull(id, "id must not be null");

    if (attemptedBy == null || attemptedBy.isBlank()) {
      throw new IllegalArgumentException("attemptedBy must not be blank");
    }

    this.attemptedBy = attemptedBy;
    this.attemptedAt = Objects.requireNonNull(attemptedAt, "attemptedAt must not be null");
    this.result = Objects.requireNonNull(result, "result must not be null");
  }

  public static CallAttempt restore(
      UUID id, String attemptedBy, Instant attemptedAt, CallAttemptResult result) {
    return new CallAttempt(id, attemptedBy, attemptedAt, result);
  }

  public UUID getId() {
    return id;
  }

  public String getAttemptedBy() {
    return attemptedBy;
  }

  public Instant getAttemptedAt() {
    return attemptedAt;
  }

  public CallAttemptResult getResult() {
    return result;
  }
}
