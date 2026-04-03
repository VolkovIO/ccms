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
    this.id = UUID.randomUUID();

    if (attemptedBy == null || attemptedBy.isBlank()) {
      throw new IllegalArgumentException("attemptedBy must not be blank");
    }

    this.attemptedBy = attemptedBy;
    this.attemptedAt = Objects.requireNonNull(attemptedAt, "attemptedAt must not be null");
    this.result = Objects.requireNonNull(result, "result must not be null");
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
