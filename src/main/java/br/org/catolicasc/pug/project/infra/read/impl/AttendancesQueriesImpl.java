package br.org.catolicasc.pug.project.infra.read.impl;

import br.org.catolicasc.pug.project.infra.read.AttendancesQueries;
import br.org.catolicasc.pug.project.infra.read.dtos.AttendanceView;
import br.org.catolicasc.pug.project.service.dtos.AttendanceComplexSearchCriteria;
import br.org.catolicasc.pug.shared.service.dtos.PageExecution;
import br.org.catolicasc.pug.shared.service.dtos.PageQuery;
import br.org.catolicasc.pug.shared.service.dtos.PageResult;
import br.org.catolicasc.pug.shared.utils.CollectionUtils;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * JPQL-backed implementation of the attendance read-side contract.
 *
 * <p>The queries are assembled with the joins required to return presentation-ready projections
 * directly from the database layer.
 */
@ApplicationScoped
@Transactional(Transactional.TxType.SUPPORTS)
public class AttendancesQueriesImpl implements AttendancesQueries {

  private static final String SELECT_BASE =
      """
      select new br.org.catolicasc.pug.project.infra.read.dtos.AttendanceView(
        a.id,
        a.projectId,
        p.name,
        a.formerStudentId,
        su.name,
        sa.email,
        fs.academicRegistration,
        fs.campus,
        a.duration,
        a.qrValidationHash,
        a.status,
        a.validatedBy,
        vu.name,
        va.email,
        a.validatedAt,
        a.createdAt,
        a.updatedAt
      )
      from AttendanceEntity a
      join ProjectEntity p on p.id = a.projectId
      join FormerStudentEntity fs on fs.accountId = a.formerStudentId
      join AccountEntity sa on sa.id = fs.accountId
      join UserEntity su on su.id = sa.userId
      left join AccountEntity va on va.id = a.validatedBy
      left join UserEntity vu on vu.id = va.userId
      """;

  private static final String COUNT_BASE =
      """
      select count(a.id)
      from AttendanceEntity a
      join ProjectEntity p on p.id = a.projectId
      join FormerStudentEntity fs on fs.accountId = a.formerStudentId
      join AccountEntity sa on sa.id = fs.accountId
      join UserEntity su on su.id = sa.userId
      left join AccountEntity va on va.id = a.validatedBy
      left join UserEntity vu on vu.id = va.userId
      """;

  private static final String ORDER_BY = " order by a.createdAt desc";

  /** {@inheritDoc} */
  @Inject EntityManager em;

  @Override
  public Optional<AttendanceView> findOptionalById(UUID id) {
    if (id == null) {
      return Optional.empty();
    }
    return em.createQuery(SELECT_BASE + " where a.id = :id", AttendanceView.class)
        .setParameter("id", id)
        .getResultStream()
        .findFirst();
  }

  /** {@inheritDoc} */
  @Override
  public List<AttendanceView> listAll() {
    return em.createQuery(SELECT_BASE + ORDER_BY, AttendanceView.class).getResultList();
  }

  /** {@inheritDoc} */
  @Override
  public List<AttendanceView> listAllByIds(List<UUID> ids) {
    if (CollectionUtils.isEmpty(ids)) {
      return List.of();
    }
    return em.createQuery(SELECT_BASE + " where a.id in :ids" + ORDER_BY, AttendanceView.class)
        .setParameter("ids", ids)
        .getResultList();
  }

  /** {@inheritDoc} */
  @Override
  public PageResult<AttendanceView> search(
      AttendanceComplexSearchCriteria criteria, PageQuery pageQuery) {
    AttendanceComplexSearchCriteria safeCriteria =
        criteria == null
            ? new AttendanceComplexSearchCriteria(
                List.of(), List.of(), List.of(), List.of(), null, null, null, null)
            : criteria;
    PageQuery safePageQuery = pageQuery == null ? new PageQuery(0, 25) : pageQuery;

    List<String> clauses = new ArrayList<>();
    if (CollectionUtils.isNotEmpty(safeCriteria.projectIds())) {
      clauses.add("a.projectId in :projectIds");
    }
    if (CollectionUtils.isNotEmpty(safeCriteria.formerStudentIds())) {
      clauses.add("a.formerStudentId in :formerStudentIds");
    }
    if (CollectionUtils.isNotEmpty(safeCriteria.statuses())) {
      clauses.add("a.status in :statuses");
    }
    if (CollectionUtils.isNotEmpty(safeCriteria.validatedByIds())) {
      clauses.add("a.validatedBy in :validatedByIds");
    }
    if (safeCriteria.durationFrom() != null) {
      clauses.add("a.duration >= :durationFrom");
    }
    if (safeCriteria.durationTo() != null) {
      clauses.add("a.duration <= :durationTo");
    }
    if (safeCriteria.dateFrom() != null) {
      clauses.add(
          "(a.createdAt >= :dateFrom or a.updatedAt >= :dateFrom or a.validatedAt >= :dateFrom)");
    }
    if (safeCriteria.dateTo() != null) {
      clauses.add("(a.createdAt <= :dateTo or a.updatedAt <= :dateTo or a.validatedAt <= :dateTo)");
    }

    String whereClause = clauses.isEmpty() ? "" : " where " + String.join(" and ", clauses);

    TypedQuery<Long> countQuery = em.createQuery(COUNT_BASE + whereClause, Long.class);
    TypedQuery<AttendanceView> dataQuery =
        em.createQuery(SELECT_BASE + whereClause + ORDER_BY, AttendanceView.class);

    bindFilters(countQuery, safeCriteria);
    bindFilters(dataQuery, safeCriteria);

    long total = countQuery.getSingleResult();
    PageExecution execution = PageExecution.from(safePageQuery, total);
    List<AttendanceView> content = execution.apply(dataQuery).getResultList();

    return new PageResult<>(
        content,
        execution.page(),
        execution.size(),
        execution.totalElements(),
        execution.totalPages());
  }

  private <T> void bindFilters(TypedQuery<T> query, AttendanceComplexSearchCriteria criteria) {
    if (CollectionUtils.isNotEmpty(criteria.projectIds())) {
      query.setParameter("projectIds", criteria.projectIds());
    }
    if (CollectionUtils.isNotEmpty(criteria.formerStudentIds())) {
      query.setParameter("formerStudentIds", criteria.formerStudentIds());
    }
    if (CollectionUtils.isNotEmpty(criteria.statuses())) {
      query.setParameter("statuses", criteria.statuses().stream().map(Enum::name).toList());
    }
    if (CollectionUtils.isNotEmpty(criteria.validatedByIds())) {
      query.setParameter("validatedByIds", criteria.validatedByIds());
    }
    if (criteria.durationFrom() != null) {
      query.setParameter("durationFrom", criteria.durationFrom());
    }
    if (criteria.durationTo() != null) {
      query.setParameter("durationTo", criteria.durationTo());
    }
    if (criteria.dateFrom() != null) {
      query.setParameter("dateFrom", criteria.dateFrom());
    }
    if (criteria.dateTo() != null) {
      query.setParameter("dateTo", criteria.dateTo());
    }
  }
}
