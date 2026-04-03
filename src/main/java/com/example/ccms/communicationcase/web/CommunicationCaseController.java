package com.example.ccms.communicationcase.web;

import com.example.ccms.communicationcase.application.OpenCommunicationCaseCommand;
import com.example.ccms.communicationcase.application.OpenCommunicationCaseUseCase;
import com.example.ccms.communicationcase.domain.model.CommunicationCaseId;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/communication-cases")
@Tag(name = "Communication Cases")
public class CommunicationCaseController {

  private final OpenCommunicationCaseUseCase openCommunicationCaseUseCase;

  public CommunicationCaseController(OpenCommunicationCaseUseCase openCommunicationCaseUseCase) {
    this.openCommunicationCaseUseCase = openCommunicationCaseUseCase;
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  @Operation(summary = "Open communication case")
  public OpenCommunicationCaseResponse openCommunicationCase(
      @Valid @RequestBody OpenCommunicationCaseRequest request
  ) {
    CommunicationCaseId communicationCaseId = openCommunicationCaseUseCase.open(
        new OpenCommunicationCaseCommand(
            request.fullName(),
            request.phoneNumber(),
            request.sourceSystem(),
            request.externalOrderId(),
            request.orderSummary(),
            request.contactReason(),
            request.createdBy()
        )
    );

    return new OpenCommunicationCaseResponse(communicationCaseId.toString());
  }
}
