package com.example.ccms.communicationcase.infrastructure.persistence;

import com.example.ccms.communicationcase.domain.model.CommunicationCase;
import com.example.ccms.communicationcase.domain.model.CommunicationCaseId;
import com.example.ccms.communicationcase.domain.repository.CommunicationCaseRepository;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

@Repository
@Profile("in-memory")
public class InMemoryCommunicationCaseRepository implements CommunicationCaseRepository {

  private final Map<CommunicationCaseId, CommunicationCase> storage = new ConcurrentHashMap<>();

  @Override
  public CommunicationCase save(CommunicationCase communicationCase) {
    storage.put(communicationCase.getId(), communicationCase);
    return communicationCase;
  }

  @Override
  public Optional<CommunicationCase> findById(CommunicationCaseId id) {
    return Optional.ofNullable(storage.get(id));
  }

  @Override
  public List<CommunicationCase> findAll() {
    return List.copyOf(storage.values());
  }
}
