package com.example.ccms.communicationcase.infrastructure.query;

import com.example.ccms.communicationcase.application.query.CommunicationCaseListItem;
import com.example.ccms.communicationcase.application.query.CommunicationCaseQueryRepository;
import com.example.ccms.communicationcase.application.query.SearchCommunicationCasesQuery;
import com.example.ccms.communicationcase.domain.model.CommunicationCase;
import com.example.ccms.communicationcase.domain.repository.CommunicationCaseRepository;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

@Repository
@Profile("in-memory")
@RequiredArgsConstructor
public class InMemoryCommunicationCaseQueryRepository implements CommunicationCaseQueryRepository {

  private final CommunicationCaseRepository communicationCaseRepository;

  @Override
  public List<CommunicationCaseListItem> search(SearchCommunicationCasesQuery query) {
    return communicationCaseRepository.findAll().stream()
        .filter(communicationCase -> matchesPhone(communicationCase, query))
        .filter(communicationCase -> matchesCustomerName(communicationCase, query))
        .filter(communicationCase -> matchesStatus(communicationCase, query))
        .sorted(
            Comparator.comparing(CommunicationCase::getOpenedAt)
                .reversed()
                .thenComparing(
                    communicationCase -> communicationCase.getId().value(),
                    Comparator.reverseOrder()))
        .map(this::toListItem)
        .toList();
  }

  private boolean matchesPhone(
      CommunicationCase communicationCase, SearchCommunicationCasesQuery query) {

    return query.phoneNumber() == null
        || query.phoneNumber().isBlank()
        || communicationCase
            .getCustomer()
            .phoneNumber()
            .toLowerCase(Locale.ROOT)
            .contains(query.phoneNumber().trim().toLowerCase(Locale.ROOT));
  }

  private boolean matchesCustomerName(
      CommunicationCase communicationCase, SearchCommunicationCasesQuery query) {

    return query.customerName() == null
        || query.customerName().isBlank()
        || communicationCase
            .getCustomer()
            .fullName()
            .toLowerCase(Locale.ROOT)
            .contains(query.customerName().trim().toLowerCase(Locale.ROOT));
  }

  private boolean matchesStatus(
      CommunicationCase communicationCase, SearchCommunicationCasesQuery query) {

    return query.status() == null || communicationCase.getStatus() == query.status();
  }

  private CommunicationCaseListItem toListItem(CommunicationCase communicationCase) {
    return new CommunicationCaseListItem(
        communicationCase.getId().value().toString(),
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
