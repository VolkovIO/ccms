package com.example.ccms.communicationcase.infrastructure.persistence;

import com.example.ccms.communicationcase.domain.model.CallAttempt;
import com.example.ccms.communicationcase.domain.model.CallAttemptResult;
import com.example.ccms.communicationcase.domain.model.CommunicationCase;
import com.example.ccms.communicationcase.domain.model.CommunicationCaseId;
import com.example.ccms.communicationcase.domain.model.CommunicationCaseStatus;
import com.example.ccms.communicationcase.domain.model.ContactReason;
import com.example.ccms.communicationcase.domain.model.CustomerSnapshot;
import com.example.ccms.communicationcase.domain.model.ExternalOrderReference;
import com.example.ccms.communicationcase.domain.model.Message;
import com.example.ccms.communicationcase.domain.model.MessageChannel;
import com.example.ccms.communicationcase.domain.model.MessageDeliveryStatus;
import com.example.ccms.communicationcase.domain.model.MessageDirection;
import com.example.ccms.communicationcase.domain.repository.CommunicationCaseRepository;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Profile("jdbc")
@RequiredArgsConstructor
public class JdbcCommunicationCaseRepository implements CommunicationCaseRepository {

  private final JdbcTemplate jdbcTemplate;

  @Override
  @Transactional
  public CommunicationCase save(CommunicationCase communicationCase) {
    upsertCommunicationCase(communicationCase);
    replaceCallAttempts(communicationCase);
    replaceMessages(communicationCase);
    return communicationCase;
  }

  @Override
  @Transactional(readOnly = true)
  public Optional<CommunicationCase> findById(CommunicationCaseId id) {
    List<CommunicationCaseRow> rows =
        jdbcTemplate.query(Sql.FIND_BY_ID, communicationCaseRowMapper(), id.value());

    if (rows.isEmpty()) {
      return Optional.empty();
    }

    CommunicationCaseRow row = rows.get(0);

    List<CallAttempt> callAttempts = loadCallAttempts(row.id());
    List<Message> messages = loadMessages(row.id());

    return Optional.of(toDomain(row, callAttempts, messages));
  }

  @Override
  @Transactional(readOnly = true)
  public List<CommunicationCase> findAll() {
    List<CommunicationCaseRow> rows =
        jdbcTemplate.query(Sql.FIND_ALL, communicationCaseRowMapper());

    return rows.stream()
        .map(row -> toDomain(row, loadCallAttempts(row.id()), loadMessages(row.id())))
        .toList();
  }

  private void upsertCommunicationCase(CommunicationCase communicationCase) {
    jdbcTemplate.update(
        Sql.UPSERT_COMMUNICATION_CASE,
        communicationCase.getId().value(),
        communicationCase.getCustomer().fullName(),
        communicationCase.getCustomer().phoneNumber(),
        communicationCase.getExternalOrderReference().sourceSystem(),
        communicationCase.getExternalOrderReference().externalOrderId(),
        communicationCase.getExternalOrderReference().orderSummary(),
        communicationCase.getContactReason().name(),
        communicationCase.getStatus().name(),
        communicationCase.getCreatedBy(),
        Timestamp.from(communicationCase.getOpenedAt()),
        toTimestamp(communicationCase.getClosedAt()));
  }

  private void replaceCallAttempts(CommunicationCase communicationCase) {
    UUID communicationCaseId = communicationCase.getId().value();

    jdbcTemplate.update(Sql.DELETE_CALL_ATTEMPTS_BY_CASE_ID, communicationCaseId);

    for (CallAttempt callAttempt : communicationCase.getCallAttempts()) {
      jdbcTemplate.update(
          Sql.INSERT_CALL_ATTEMPT,
          callAttempt.getId(),
          communicationCaseId,
          callAttempt.getAttemptedBy(),
          Timestamp.from(callAttempt.getAttemptedAt()),
          callAttempt.getResult().name());
    }
  }

  private void replaceMessages(CommunicationCase communicationCase) {
    UUID communicationCaseId = communicationCase.getId().value();

    jdbcTemplate.update(Sql.DELETE_MESSAGES_BY_CASE_ID, communicationCaseId);

    for (Message message : communicationCase.getMessages()) {
      jdbcTemplate.update(
          Sql.INSERT_MESSAGE,
          message.getId(),
          communicationCaseId,
          message.getDirection().name(),
          message.getChannel().name(),
          message.getText(),
          message.getDeliveryStatus() != null ? message.getDeliveryStatus().name() : null,
          Timestamp.from(message.getCreatedAt()));
    }
  }

  private List<CallAttempt> loadCallAttempts(UUID communicationCaseId) {
    return jdbcTemplate.query(
        Sql.FIND_CALL_ATTEMPTS_BY_CASE_ID,
        (rs, rowNum) ->
            CallAttempt.restore(
                rs.getObject("id", UUID.class),
                rs.getString("attempted_by"),
                rs.getTimestamp("attempted_at").toInstant(),
                CallAttemptResult.valueOf(rs.getString("result"))),
        communicationCaseId);
  }

  private List<Message> loadMessages(UUID communicationCaseId) {
    return jdbcTemplate.query(
        Sql.FIND_MESSAGES_BY_CASE_ID,
        (rs, rowNum) ->
            Message.restore(
                rs.getObject("id", UUID.class),
                MessageDirection.valueOf(rs.getString("direction")),
                MessageChannel.valueOf(rs.getString("channel")),
                rs.getString("text"),
                mapDeliveryStatus(rs.getString("delivery_status")),
                rs.getTimestamp("created_at").toInstant()),
        communicationCaseId);
  }

  private CommunicationCase toDomain(
      CommunicationCaseRow row, List<CallAttempt> callAttempts, List<Message> messages) {

    return CommunicationCase.restore(
        new CommunicationCaseId(row.id()),
        new CustomerSnapshot(row.customerFullName(), row.customerPhoneNumber()),
        new ExternalOrderReference(row.sourceSystem(), row.externalOrderId(), row.orderSummary()),
        row.contactReason(),
        row.status(),
        row.createdBy(),
        row.openedAt(),
        row.closedAt(),
        callAttempts,
        messages);
  }

  private RowMapper<CommunicationCaseRow> communicationCaseRowMapper() {
    return (rs, rowNum) ->
        new CommunicationCaseRow(
            rs.getObject("id", UUID.class),
            rs.getString("customer_full_name"),
            rs.getString("customer_phone_number"),
            rs.getString("source_system"),
            rs.getString("external_order_id"),
            rs.getString("order_summary"),
            ContactReason.valueOf(rs.getString("contact_reason")),
            CommunicationCaseStatus.valueOf(rs.getString("status")),
            rs.getString("created_by"),
            rs.getTimestamp("opened_at").toInstant(),
            toInstant(rs.getTimestamp("closed_at")));
  }

  private static Timestamp toTimestamp(Instant instant) {
    return instant != null ? Timestamp.from(instant) : null;
  }

  @SuppressWarnings("PMD.ReplaceJavaUtilDate")
  private static Instant toInstant(Timestamp timestamp) {
    return timestamp != null ? timestamp.toInstant() : null;
  }

  private static MessageDeliveryStatus mapDeliveryStatus(String value) {
    return value != null ? MessageDeliveryStatus.valueOf(value) : null;
  }

  private record CommunicationCaseRow(
      UUID id,
      String customerFullName,
      String customerPhoneNumber,
      String sourceSystem,
      String externalOrderId,
      String orderSummary,
      ContactReason contactReason,
      CommunicationCaseStatus status,
      String createdBy,
      Instant openedAt,
      Instant closedAt) {}

  private static final class Sql {

    private static final String UPSERT_COMMUNICATION_CASE =
        """
            insert into communication_case (
                id,
                customer_full_name,
                customer_phone_number,
                source_system,
                external_order_id,
                order_summary,
                contact_reason,
                status,
                created_by,
                opened_at,
                closed_at
            ) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            on conflict (id) do update set
                customer_full_name = excluded.customer_full_name,
                customer_phone_number = excluded.customer_phone_number,
                source_system = excluded.source_system,
                external_order_id = excluded.external_order_id,
                order_summary = excluded.order_summary,
                contact_reason = excluded.contact_reason,
                status = excluded.status,
                created_by = excluded.created_by,
                opened_at = excluded.opened_at,
                closed_at = excluded.closed_at
            """;

    private static final String DELETE_CALL_ATTEMPTS_BY_CASE_ID =
        """
            delete from call_attempt
             where communication_case_id = ?
            """;

    private static final String INSERT_CALL_ATTEMPT =
        """
            insert into call_attempt (
                id,
                communication_case_id,
                attempted_by,
                attempted_at,
                result
            ) values (?, ?, ?, ?, ?)
            """;

    private static final String DELETE_MESSAGES_BY_CASE_ID =
        """
            delete from message
             where communication_case_id = ?
            """;

    private static final String INSERT_MESSAGE =
        """
            insert into message (
                id,
                communication_case_id,
                direction,
                channel,
                text,
                delivery_status,
                created_at
            ) values (?, ?, ?, ?, ?, ?, ?)
            """;

    private static final String FIND_BY_ID =
        """
            select
                id,
                customer_full_name,
                customer_phone_number,
                source_system,
                external_order_id,
                order_summary,
                contact_reason,
                status,
                created_by,
                opened_at,
                closed_at
              from communication_case
             where id = ?
            """;

    private static final String FIND_ALL =
        """
            select
                id,
                customer_full_name,
                customer_phone_number,
                source_system,
                external_order_id,
                order_summary,
                contact_reason,
                status,
                created_by,
                opened_at,
                closed_at
              from communication_case
             order by opened_at desc, id desc
            """;

    private static final String FIND_CALL_ATTEMPTS_BY_CASE_ID =
        """
            select
                id,
                attempted_by,
                attempted_at,
                result
              from call_attempt
             where communication_case_id = ?
             order by attempted_at asc, id asc
            """;

    private static final String FIND_MESSAGES_BY_CASE_ID =
        """
            select
                id,
                direction,
                channel,
                text,
                delivery_status,
                created_at
              from message
             where communication_case_id = ?
             order by created_at asc, id asc
            """;

    private Sql() {}
  }
}
