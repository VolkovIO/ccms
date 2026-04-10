package com.example.ccms.communicationcase.application.query;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SearchCommunicationCasesUseCase {

  private final CommunicationCaseQueryRepository communicationCaseQueryRepository;

  public List<CommunicationCaseListItem> search(SearchCommunicationCasesQuery query) {
    return communicationCaseQueryRepository.search(query);
  }
}
