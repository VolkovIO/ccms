package com.example.ccms.communicationcase.application.query;

import java.util.List;

@SuppressWarnings("PMD.ImplicitFunctionalInterface")
public interface CommunicationCaseQueryRepository {

  List<CommunicationCaseListItem> search(SearchCommunicationCasesQuery query);
}
