package com.example.ccms.communicationcase.domain.repository;

import com.example.ccms.communicationcase.domain.model.CommunicationCase;
import com.example.ccms.communicationcase.domain.model.CommunicationCaseId;
import java.util.Optional;

public interface CommunicationCaseRepository {

  CommunicationCase save(CommunicationCase communicationCase);

  Optional<CommunicationCase> findById(CommunicationCaseId id);
}
