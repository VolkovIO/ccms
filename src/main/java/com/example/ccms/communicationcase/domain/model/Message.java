package com.example.ccms.communicationcase.domain.model;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public final class Message {

  private final UUID id;
  private final MessageDirection direction;
  private final MessageChannel channel;
  private final String text;
  private MessageDeliveryStatus deliveryStatus;
  private final Instant createdAt;

  private Message(
      MessageDirection direction,
      MessageChannel channel,
      String text,
      MessageDeliveryStatus deliveryStatus,
      Instant createdAt) {
    this.id = UUID.randomUUID();
    this.direction = Objects.requireNonNull(direction, "direction must not be null");
    this.channel = Objects.requireNonNull(channel, "channel must not be null");

    if (text == null || text.isBlank()) {
      throw new IllegalArgumentException("text must not be blank");
    }

    this.text = text;
    this.deliveryStatus = deliveryStatus;
    this.createdAt = Objects.requireNonNull(createdAt, "createdAt must not be null");
  }

  public static Message prepareOutgoing(MessageChannel channel, String text, Instant createdAt) {
    return new Message(
        MessageDirection.OUTBOUND, channel, text, MessageDeliveryStatus.PREPARED, createdAt);
  }

  public static Message receiveIncoming(MessageChannel channel, String text, Instant createdAt) {
    return new Message(MessageDirection.INBOUND, channel, text, null, createdAt);
  }

  public void markRequested() {
    ensureOutgoing();
    this.deliveryStatus = MessageDeliveryStatus.REQUESTED;
  }

  public void markSent() {
    ensureOutgoing();
    this.deliveryStatus = MessageDeliveryStatus.SENT;
  }

  public void markFailed() {
    ensureOutgoing();
    this.deliveryStatus = MessageDeliveryStatus.FAILED;
  }

  private void ensureOutgoing() {
    if (direction != MessageDirection.OUTBOUND) {
      throw new IllegalStateException("Only outgoing messages can change delivery status");
    }
  }

  public UUID getId() {
    return id;
  }

  public MessageDirection getDirection() {
    return direction;
  }

  public MessageChannel getChannel() {
    return channel;
  }

  public String getText() {
    return text;
  }

  public MessageDeliveryStatus getDeliveryStatus() {
    return deliveryStatus;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }
}
