package com.example.ccms.communicationcase.domain.model;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@SuppressWarnings("PMD.CyclomaticComplexity")
public final class CommunicationCase {

  private final CommunicationCaseId id;
  private final CustomerSnapshot customer;
  private final ExternalOrderReference externalOrderReference;
  private final ContactReason contactReason;
  private final Instant openedAt;
  private final String createdBy;

  private CommunicationCaseStatus status;
  private Instant closedAt;

  private final List<CallAttempt> callAttempts = new ArrayList<>();
  private final List<Message> messages = new ArrayList<>();

  private CommunicationCase(
      CommunicationCaseId id,
      CustomerSnapshot customer,
      ExternalOrderReference externalOrderReference,
      ContactReason contactReason,
      Instant openedAt,
      String createdBy) {
    this.id = Objects.requireNonNull(id, "id must not be null");
    this.customer = Objects.requireNonNull(customer, "customer must not be null");
    this.externalOrderReference =
        Objects.requireNonNull(externalOrderReference, "externalOrderReference must not be null");
    this.contactReason = Objects.requireNonNull(contactReason, "contactReason must not be null");
    this.openedAt = Objects.requireNonNull(openedAt, "openedAt must not be null");

    if (createdBy == null || createdBy.isBlank()) {
      throw new IllegalArgumentException("createdBy must not be blank");
    }

    this.createdBy = createdBy;
    this.status = CommunicationCaseStatus.OPEN;
  }

  public static CommunicationCase open(
      CustomerSnapshot customer,
      ExternalOrderReference externalOrderReference,
      ContactReason contactReason,
      String createdBy,
      Instant openedAt) {
    return new CommunicationCase(
        CommunicationCaseId.newId(),
        customer,
        externalOrderReference,
        contactReason,
        openedAt,
        createdBy);
  }

  public static CommunicationCase restore(
      CommunicationCaseId id,
      CustomerSnapshot customer,
      ExternalOrderReference externalOrderReference,
      ContactReason contactReason,
      CommunicationCaseStatus status,
      String createdBy,
      Instant openedAt,
      Instant closedAt,
      List<CallAttempt> callAttempts,
      List<Message> messages) {

    CommunicationCase communicationCase =
        new CommunicationCase(
            id, customer, externalOrderReference, contactReason, openedAt, createdBy);

    communicationCase.status = Objects.requireNonNull(status, "status must not be null");
    communicationCase.closedAt = closedAt;
    communicationCase.callAttempts.addAll(List.copyOf(callAttempts));
    communicationCase.messages.addAll(List.copyOf(messages));

    return communicationCase;
  }

  public void registerCallAttempt(
      String attemptedBy, CallAttemptResult result, Instant attemptedAt) {
    ensureNotClosed();
    callAttempts.add(new CallAttempt(attemptedBy, attemptedAt, result));
  }

  public Message prepareOutgoingMessage(MessageChannel channel, String text, Instant createdAt) {
    ensureNotClosed();
    Message message = Message.prepareOutgoing(channel, text, createdAt);
    messages.add(message);
    return message;
  }

  public void requestMessage(Message message) {
    ensureNotClosed();
    ensureMessageBelongsToCase(message);
    message.markRequested();
  }

  public void markMessageSent(Message message) {
    ensureNotClosed();
    ensureMessageBelongsToCase(message);
    message.markSent();
    this.status = CommunicationCaseStatus.WAITING_FOR_CUSTOMER;
  }

  public void markMessageFailed(Message message) {
    ensureNotClosed();
    ensureMessageBelongsToCase(message);
    message.markFailed();
    this.status = CommunicationCaseStatus.FOLLOW_UP_REQUIRED;
  }

  public Message receiveIncomingMessage(MessageChannel channel, String text, Instant receivedAt) {
    ensureNotClosed();
    Message incoming = Message.receiveIncoming(channel, text, receivedAt);
    messages.add(incoming);
    this.status = CommunicationCaseStatus.CUSTOMER_REPLIED;
    return incoming;
  }

  public void markFollowUpRequired() {
    ensureNotClosed();
    this.status = CommunicationCaseStatus.FOLLOW_UP_REQUIRED;
  }

  public void close(Instant closedAt) {
    ensureNotClosed();
    this.status = CommunicationCaseStatus.CLOSED;
    this.closedAt = Objects.requireNonNull(closedAt, "closedAt must not be null");
  }

  private void ensureNotClosed() {
    if (status == CommunicationCaseStatus.CLOSED) {
      throw new IllegalStateException("Communication case is already closed");
    }
  }

  private void ensureMessageBelongsToCase(Message message) {
    if (!messages.contains(message)) {
      throw new IllegalArgumentException("Message does not belong to this communication case");
    }
  }

  public CommunicationCaseId getId() {
    return id;
  }

  public CustomerSnapshot getCustomer() {
    return customer;
  }

  public ExternalOrderReference getExternalOrderReference() {
    return externalOrderReference;
  }

  public ContactReason getContactReason() {
    return contactReason;
  }

  public CommunicationCaseStatus getStatus() {
    return status;
  }

  public Instant getOpenedAt() {
    return openedAt;
  }

  public Instant getClosedAt() {
    return closedAt;
  }

  public String getCreatedBy() {
    return createdBy;
  }

  public List<CallAttempt> getCallAttempts() {
    return Collections.unmodifiableList(callAttempts);
  }

  public List<Message> getMessages() {
    return Collections.unmodifiableList(messages);
  }
}
