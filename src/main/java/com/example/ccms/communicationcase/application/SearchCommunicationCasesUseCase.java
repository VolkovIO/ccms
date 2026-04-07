package com.example.ccms.communicationcase.application;

import com.example.ccms.communicationcase.domain.model.CommunicationCase;
import com.example.ccms.communicationcase.domain.repository.CommunicationCaseRepository;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SearchCommunicationCasesUseCase {

  private final CommunicationCaseRepository repository;

  public List<CommunicationCaseListItem> search(SearchCommunicationCasesQuery query) {
    return repository.findAll().stream()
        .filter(communicationCase -> matchesCustomerName(communicationCase, query.customerName()))
        .filter(communicationCase -> matchesPhoneNumber(communicationCase, query.phoneNumber()))
        .filter(communicationCase -> matchesStatus(communicationCase, query.status()))
        .sorted(Comparator.comparing(CommunicationCase::getOpenedAt).reversed())
        .map(this::toListItem)
        .toList();
  }

  private boolean matchesCustomerName(CommunicationCase communicationCase, String customerName) {
    if (customerName == null || customerName.isBlank()) {
      return true;
    }

    return communicationCase
        .getCustomer()
        .fullName()
        .toLowerCase(Locale.ROOT)
        .contains(customerName.toLowerCase(Locale.ROOT));
  }

  private boolean matchesPhoneNumber(CommunicationCase communicationCase, String phoneNumber) {
    if (phoneNumber == null || phoneNumber.isBlank()) {
      return true;
    }

    return communicationCase.getCustomer().phoneNumber().contains(phoneNumber);
  }

  private boolean matchesStatus(
      CommunicationCase communicationCase,
      com.example.ccms.communicationcase.domain.model.CommunicationCaseStatus status) {
    if (status == null) {
      return true;
    }

    return communicationCase.getStatus() == status;
  }

  private CommunicationCaseListItem toListItem(CommunicationCase communicationCase) {
    return new CommunicationCaseListItem(
        communicationCase.getId().toString(),
        communicationCase.getCustomer().fullName(),
        communicationCase.getCustomer().phoneNumber(),
        communicationCase.getExternalOrderReference().sourceSystem(),
        communicationCase.getExternalOrderReference().externalOrderId(),
        communicationCase.getExternalOrderReference().orderSummary(),
        communicationCase.getContactReason(),
        communicationCase.getStatus(),
        communicationCase.getOpenedAt(),
        communicationCase.getClosedAt());
  }
}
