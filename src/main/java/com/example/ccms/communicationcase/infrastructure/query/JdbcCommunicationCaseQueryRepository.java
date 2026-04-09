package com.example.ccms.communicationcase.infrastructure.query;

import com.example.ccms.communicationcase.application.query.CommunicationCaseListItem;
import com.example.ccms.communicationcase.application.query.CommunicationCaseQueryRepository;
import com.example.ccms.communicationcase.application.query.SearchCommunicationCasesQuery;
import com.example.ccms.communicationcase.domain.model.CommunicationCaseStatus;
import com.example.ccms.communicationcase.domain.model.ContactReason;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@Profile("jdbc")
@RequiredArgsConstructor
public class JdbcCommunicationCaseQueryRepository implements CommunicationCaseQueryRepository {

  private final JdbcTemplate jdbcTemplate;

  @Override
  public List<CommunicationCaseListItem> search(SearchCommunicationCasesQuery query) {
    SqlQuery sqlQuery = buildSearchQuery(query);

    return jdbcTemplate.query(sqlQuery.sql(), this::mapRow, sqlQuery.args().toArray());
  }

  private CommunicationCaseListItem mapRow(ResultSet rs, int ignoredRowNum) throws SQLException {
    return new CommunicationCaseListItem(
        rs.getString("id"),
        rs.getString("customer_full_name"),
        rs.getString("customer_phone_number"),
        rs.getString("source_system"),
        rs.getString("external_order_id"),
        rs.getString("order_summary"),
        ContactReason.valueOf(rs.getString("contact_reason")),
        CommunicationCaseStatus.valueOf(rs.getString("status")),
        Objects.requireNonNull(
                rs.getObject("opened_at", OffsetDateTime.class), "opened_at must not be null")
            .toInstant(),
        Optional.ofNullable(rs.getObject("closed_at", OffsetDateTime.class))
            .map(OffsetDateTime::toInstant)
            .orElse(null));
  }

  private SqlQuery buildSearchQuery(SearchCommunicationCasesQuery query) {
    List<Object> args = new ArrayList<>();
    List<String> conditions = new ArrayList<>();

    if (hasText(query.phoneNumber())) {
      conditions.add("lower(customer_phone_number) like ?");
      args.add(like(query.phoneNumber()));
    }

    if (hasText(query.customerName())) {
      conditions.add("lower(customer_full_name) like ?");
      args.add(like(query.customerName()));
    }

    if (query.status() != null) {
      conditions.add("status = ?");
      args.add(query.status().name());
    }

    StringBuilder sql = new StringBuilder(Sql.BASE_SEARCH);

    if (!conditions.isEmpty()) {
      sql.append(" where ").append(String.join(" and ", conditions));
    }

    sql.append(Sql.ORDER_BY);

    return new SqlQuery(sql.toString(), args);
  }

  private static boolean hasText(String value) {
    return value != null && !value.isBlank();
  }

  private static String like(String value) {
    return "%" + value.trim().toLowerCase(Locale.ROOT) + "%";
  }

  private record SqlQuery(String sql, List<Object> args) {}

  private static final class Sql {

    private static final String BASE_SEARCH =
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
                opened_at,
                closed_at
              from communication_case
            """;

    private static final String ORDER_BY =
        """
             order by opened_at desc, id desc
            """;

    private Sql() {}
  }
}
