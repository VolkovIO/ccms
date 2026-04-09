package com.example.ccms.communicationcase.application.query;

import com.example.ccms.communicationcase.application.CommunicationCaseListItem;
import com.example.ccms.communicationcase.application.SearchCommunicationCasesQuery;
import java.util.List;

@SuppressWarnings("PMD.ImplicitFunctionalInterface")
public interface CommunicationCaseQueryRepository {

  List<CommunicationCaseListItem> search(SearchCommunicationCasesQuery query);
}
