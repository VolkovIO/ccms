package com.example.ccms.communicationcase.domain.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.Instant;
import org.junit.jupiter.api.Test;

class CommunicationCaseTest {

  @Test
  void shouldOpenCommunicationCaseWithOpenStatus() {
    CustomerSnapshot customer = new CustomerSnapshot(
        "Ivan Petrov",
        "+79001234567"
    );

    ExternalOrderReference orderReference = new ExternalOrderReference(
        "1C",
        "ORDER-123",
        "Washing machine ready for pickup"
    );

    CommunicationCase communicationCase = CommunicationCase.open(
        customer,
        orderReference,
        ContactReason.READY_FOR_PICKUP,
        "operator1",
        Instant.parse("2026-04-03T10:00:00Z")
    );

    assertNotNull(communicationCase.getId());
    assertEquals(CommunicationCaseStatus.OPEN, communicationCase.getStatus());
    assertEquals("Ivan Petrov", communicationCase.getCustomer().fullName());
    assertEquals("+79001234567", communicationCase.getCustomer().phoneNumber());
    assertEquals(ContactReason.READY_FOR_PICKUP, communicationCase.getContactReason());
    assertEquals("operator1", communicationCase.getCreatedBy());
    assertEquals(0, communicationCase.getCallAttempts().size());
    assertEquals(0, communicationCase.getMessages().size());
  }

  @Test
  void shouldRegisterCallAttempt() {
    CommunicationCase communicationCase = newCase();

    communicationCase.registerCallAttempt(
        "operator1",
        CallAttemptResult.NOT_REACHED,
        Instant.parse("2026-04-03T10:10:00Z")
    );

    assertEquals(1, communicationCase.getCallAttempts().size());

    CallAttempt attempt = communicationCase.getCallAttempts().getFirst();
    assertEquals("operator1", attempt.getAttemptedBy());
    assertEquals(CallAttemptResult.NOT_REACHED, attempt.getResult());
  }

  @Test
  void shouldPrepareRequestAndMarkOutgoingMessageSent() {
    CommunicationCase communicationCase = newCase();

    Message message = communicationCase.prepareOutgoingMessage(
        MessageChannel.TEST,
        "We called you but could not reach you. Please reply.",
        Instant.parse("2026-04-03T10:15:00Z")
    );

    assertEquals(1, communicationCase.getMessages().size());
    assertEquals(MessageDeliveryStatus.PREPARED, message.getDeliveryStatus());
    assertEquals(MessageDirection.OUTBOUND, message.getDirection());

    communicationCase.requestMessage(message);
    assertEquals(MessageDeliveryStatus.REQUESTED, message.getDeliveryStatus());

    communicationCase.markMessageSent(message);
    assertEquals(MessageDeliveryStatus.SENT, message.getDeliveryStatus());
    assertEquals(CommunicationCaseStatus.WAITING_FOR_CUSTOMER, communicationCase.getStatus());
  }

  @Test
  void shouldMoveToFollowUpRequiredWhenMessageFails() {
    CommunicationCase communicationCase = newCase();

    Message message = communicationCase.prepareOutgoingMessage(
        MessageChannel.TEST,
        "Please contact us regarding your repair order.",
        Instant.parse("2026-04-03T10:20:00Z")
    );

    communicationCase.requestMessage(message);
    communicationCase.markMessageFailed(message);

    assertEquals(MessageDeliveryStatus.FAILED, message.getDeliveryStatus());
    assertEquals(CommunicationCaseStatus.FOLLOW_UP_REQUIRED, communicationCase.getStatus());
  }

  @Test
  void shouldReceiveIncomingMessageAndUpdateStatus() {
    CommunicationCase communicationCase = newCase();

    Message incoming = communicationCase.receiveIncomingMessage(
        MessageChannel.TEST,
        "I will come tomorrow",
        Instant.parse("2026-04-03T11:00:00Z")
    );

    assertEquals(MessageDirection.INBOUND, incoming.getDirection());
    assertNull(incoming.getDeliveryStatus());
    assertEquals(CommunicationCaseStatus.CUSTOMER_REPLIED, communicationCase.getStatus());
    assertEquals(1, communicationCase.getMessages().size());
  }

  @Test
  void shouldCloseCommunicationCase() {
    CommunicationCase communicationCase = newCase();

    Instant closedAt = Instant.parse("2026-04-03T12:00:00Z");
    communicationCase.close(closedAt);

    assertEquals(CommunicationCaseStatus.CLOSED, communicationCase.getStatus());
    assertEquals(closedAt, communicationCase.getClosedAt());
  }

  @Test
  void shouldNotAllowChangesAfterCaseIsClosed() {
    CommunicationCase communicationCase = newCase();
    communicationCase.close(Instant.parse("2026-04-03T12:00:00Z"));

    IllegalStateException exception = assertThrows(
        IllegalStateException.class,
        () -> communicationCase.registerCallAttempt(
            "operator1",
            CallAttemptResult.NOT_REACHED,
            Instant.parse("2026-04-03T12:10:00Z")
        )
    );

    assertEquals("Communication case is already closed", exception.getMessage());
  }

  private CommunicationCase newCase() {
    return CommunicationCase.open(
        new CustomerSnapshot("Ivan Petrov", "+79001234567"),
        new ExternalOrderReference("1C", "ORDER-123", "TV repair status update"),
        ContactReason.UNREACHABLE_BY_PHONE,
        "operator1",
        Instant.parse("2026-04-03T10:00:00Z")
    );
  }
}