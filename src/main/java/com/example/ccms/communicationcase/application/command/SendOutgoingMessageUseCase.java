package com.example.ccms.communicationcase.application.command;

import com.example.ccms.communicationcase.application.exception.CommunicationCaseNotFoundException;
import com.example.ccms.communicationcase.application.port.OutgoingMessageSender;
import com.example.ccms.communicationcase.application.port.SendMessageResult;
import com.example.ccms.communicationcase.application.port.SendOutgoingMessageRequest;
import com.example.ccms.communicationcase.domain.model.CommunicationCase;
import com.example.ccms.communicationcase.domain.model.CommunicationCaseId;
import com.example.ccms.communicationcase.domain.model.Message;
import com.example.ccms.communicationcase.domain.repository.CommunicationCaseRepository;
import java.time.Instant;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SendOutgoingMessageUseCase {

  private final CommunicationCaseRepository repository;
  private final OutgoingMessageSender outgoingMessageSender;

  /**
   * Use case for sending an outgoing message using a two-phase state transition.
   *
   * <p>Flow:
   *
   * <ul>
   *   <li>Phase 1: Persist message with status {@code REQUESTED}
   *   <li>Phase 2: Invoke external provider
   *   <li>Phase 3: Persist final status ({@code SENT} or {@code FAILED})
   * </ul>
   *
   * <p><b>Consistency note</b>
   *
   * <p>This process is not atomic across system boundaries. If a failure occurs during the second
   * persistence step, the external provider may have already accepted the message, while the local
   * database still reflects the {@code REQUESTED} state.
   *
   * <p>This results in a distributed consistency gap between the local system and the external
   * provider.
   *
   * <p>For MVP purposes, this limitation is accepted.
   *
   * <p>Typical production-grade solutions include:
   *
   * <ul>
   *   <li>Outbox pattern (guaranteed delivery)
   *   <li>Retry mechanisms
   *   <li>Reconciliation/background jobs
   *   <li>Provider-driven callbacks (webhooks)
   * </ul>
   */
  public void send(SendOutgoingMessageCommand command) {
    CommunicationCaseId id =
        new CommunicationCaseId(UUID.fromString(command.communicationCaseId()));

    CommunicationCase communicationCase =
        repository
            .findById(id)
            .orElseThrow(
                () ->
                    new CommunicationCaseNotFoundException(
                        "Communication case not found: " + command.communicationCaseId()));

    Message message =
        communicationCase.prepareOutgoingMessage(command.channel(), command.text(), Instant.now());

    communicationCase.requestMessage(message);
    repository.save(communicationCase);

    SendMessageResult result =
        outgoingMessageSender.send(
            new SendOutgoingMessageRequest(
                communicationCase.getCustomer().phoneNumber(), command.channel(), command.text()));

    if (result.successful()) {
      communicationCase.markMessageSent(message);
    } else {
      communicationCase.markMessageFailed(message);
    }

    repository.save(communicationCase);
  }
}
