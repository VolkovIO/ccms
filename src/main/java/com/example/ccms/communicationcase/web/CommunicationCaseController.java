package com.example.ccms.communicationcase.web;

import com.example.ccms.communicationcase.application.CloseCommunicationCaseCommand;
import com.example.ccms.communicationcase.application.CloseCommunicationCaseUseCase;
import com.example.ccms.communicationcase.application.CommunicationCaseDetails;
import com.example.ccms.communicationcase.application.CommunicationCaseListItem;
import com.example.ccms.communicationcase.application.GetCommunicationCaseByIdQuery;
import com.example.ccms.communicationcase.application.GetCommunicationCaseByIdUseCase;
import com.example.ccms.communicationcase.application.OpenCommunicationCaseCommand;
import com.example.ccms.communicationcase.application.OpenCommunicationCaseUseCase;
import com.example.ccms.communicationcase.application.ReceiveIncomingMessageCommand;
import com.example.ccms.communicationcase.application.ReceiveIncomingMessageUseCase;
import com.example.ccms.communicationcase.application.RegisterCallAttemptCommand;
import com.example.ccms.communicationcase.application.RegisterCallAttemptUseCase;
import com.example.ccms.communicationcase.application.SearchCommunicationCasesQuery;
import com.example.ccms.communicationcase.application.SearchCommunicationCasesUseCase;
import com.example.ccms.communicationcase.application.SendOutgoingMessageCommand;
import com.example.ccms.communicationcase.application.SendOutgoingMessageUseCase;
import com.example.ccms.communicationcase.domain.model.CommunicationCaseId;
import com.example.ccms.communicationcase.domain.model.CommunicationCaseStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/communication-cases")
@Tag(name = "Communication Cases")
public class CommunicationCaseController {

  private final OpenCommunicationCaseUseCase openCommunicationCaseUseCase;
  private final GetCommunicationCaseByIdUseCase getCommunicationCaseByIdUseCase;
  private final RegisterCallAttemptUseCase registerCallAttemptUseCase;
  private final SendOutgoingMessageUseCase sendOutgoingMessageUseCase;
  private final ReceiveIncomingMessageUseCase receiveIncomingMessageUseCase;
  private final CloseCommunicationCaseUseCase closeCommunicationCaseUseCase;
  private final SearchCommunicationCasesUseCase searchCommunicationCasesUseCase;

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  @Operation(summary = "Open communication case")
  public OpenCommunicationCaseResponse openCommunicationCase(
      @Valid @RequestBody OpenCommunicationCaseRequest request) {
    CommunicationCaseId communicationCaseId =
        openCommunicationCaseUseCase.open(
            new OpenCommunicationCaseCommand(
                request.fullName(),
                request.phoneNumber(),
                request.sourceSystem(),
                request.externalOrderId(),
                request.orderSummary(),
                request.contactReason(),
                request.createdBy()));

    return new OpenCommunicationCaseResponse(communicationCaseId.toString());
  }

  @GetMapping("/{id}")
  @Operation(summary = "Get communication case by id")
  public CommunicationCaseDetails getCommunicationCaseById(@PathVariable String id) {
    return getCommunicationCaseByIdUseCase.getById(new GetCommunicationCaseByIdQuery(id));
  }

  @PostMapping("/{id}/call-attempts")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @Operation(summary = "Register call attempt")
  public void registerCallAttempt(
      @PathVariable String id, @Valid @RequestBody RegisterCallAttemptRequest request) {
    registerCallAttemptUseCase.register(
        new RegisterCallAttemptCommand(id, request.attemptedBy(), request.result()));
  }

  @PostMapping("/{id}/messages/outgoing")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @Operation(summary = "Send outgoing message")
  public void sendOutgoingMessage(
      @PathVariable String id, @Valid @RequestBody SendOutgoingMessageRequest request) {
    sendOutgoingMessageUseCase.send(
        new SendOutgoingMessageCommand(id, request.channel(), request.text()));
  }

  @PostMapping("/{id}/messages/incoming")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @Operation(summary = "Receive incoming message")
  public void receiveIncomingMessage(
      @PathVariable String id, @Valid @RequestBody ReceiveIncomingMessageRequest request) {
    receiveIncomingMessageUseCase.receive(
        new ReceiveIncomingMessageCommand(id, request.channel(), request.text()));
  }

  @PostMapping("/{id}/close")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @Operation(summary = "Close communication case")
  public void closeCommunicationCase(@PathVariable String id) {
    closeCommunicationCaseUseCase.close(new CloseCommunicationCaseCommand(id));
  }

  @GetMapping
  @Operation(
      summary = "List and search communication cases",
      description =
          """
          Returns a list of communication cases.

          Search can be performed by any combination of parameters:
          - without parameters: returns all cases
          - by customer name or its part
          - by phone number or its part
          - by status

          Example requests:
          - GET /api/communication-cases
          - GET /api/communication-cases?customerName=Ivan
          - GET /api/communication-cases?phoneNumber=+7900
          - GET /api/communication-cases?status=CLOSED
          - GET /api/communication-cases?customerName=Ivan&status=OPEN
          """)
  public List<CommunicationCaseListItem> searchCommunicationCases(
      @RequestParam(required = false) String customerName,
      @RequestParam(required = false) String phoneNumber,
      @RequestParam(required = false) CommunicationCaseStatus status) {
    return searchCommunicationCasesUseCase.search(
        new SearchCommunicationCasesQuery(customerName, phoneNumber, status));
  }
}
