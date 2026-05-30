package br.org.catolicasc.pug.project.infra.read.impl;

import br.org.catolicasc.pug.project.infra.read.EnrollmentsQueries;
import br.org.catolicasc.pug.project.infra.read.dtos.EnrollmentView;
import br.org.catolicasc.pug.project.service.dtos.EnrollmentComplexSearchCriteria;
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

@ApplicationScoped
@Transactional(Transactional.TxType.SUPPORTS)
public class EnrollmentsQueriesImpl implements EnrollmentsQueries {

  private static final String SELECT_BASE =
      """
      select new br.org.catolicasc.pug.project.infra.read.dtos.EnrollmentView(
        en.id.projectId,
        p.name,
        en.id.studentId,
        u.name,
        a.email,
        fs.academicRegistration,
        fs.campus,
        fs.startDate,
        fs.dueDate,
        en.status,
        en.createdAt,
        en.updatedAt,
        en.acceptedAt,
        en.closingStatusAt
      )
      from EnrollmentEntity en
      join ProjectEntity p on p.id = en.id.projectId
      join FormerStudentEntity fs on fs.accountId = en.id.studentId
      join AccountEntity a on a.id = fs.accountId
      join UserEntity u on u.id = a.userId
      """;

  private static final String COUNT_BASE =
      """
      select count(en.id)
      from EnrollmentEntity en
      join ProjectEntity p on p.id = en.id.projectId
      join FormerStudentEntity fs on fs.accountId = en.id.studentId
      join AccountEntity a on a.id = fs.accountId
      join UserEntity u on u.id = a.userId
      """;

  private static final String ORDER_BY = " order by en.createdAt desc";

  @Inject EntityManager em;

  @Override
  public Optional<EnrollmentView> findOptionalByIds(UUID projectId, UUID studentId) {
    if (projectId == null || studentId == null) {
      return Optional.empty();
    }
    return em.createQuery(
            SELECT_BASE + " where en.id.projectId = :projectId and en.id.studentId = :studentId",
            EnrollmentView.class)
        .setParameter("projectId", projectId)
        .setParameter("studentId", studentId)
        .getResultStream()
        .findFirst();
  }

  @Override
  public List<EnrollmentView> listAll() {
    return em.createQuery(SELECT_BASE + ORDER_BY, EnrollmentView.class).getResultList();
  }

  @Override
  public List<EnrollmentView> listAllByProjectId(UUID projectId) {
    if (projectId == null) {
      return List.of();
    }
    return em.createQuery(
            SELECT_BASE + " where en.id.projectId = :projectId" + ORDER_BY, EnrollmentView.class)
        .setParameter("projectId", projectId)
        .getResultList();
  }

  @Override
  public List<EnrollmentView> listAllByStudentId(UUID studentId) {
    if (studentId == null) {
      return List.of();
    }
    return em.createQuery(
            SELECT_BASE + " where en.id.studentId = :studentId" + ORDER_BY, EnrollmentView.class)
        .setParameter("studentId", studentId)
        .getResultList();
  }

  @Override
  public PageResult<EnrollmentView> search(
      EnrollmentComplexSearchCriteria criteria, PageQuery pageQuery) {
    EnrollmentComplexSearchCriteria safeCriteria =
        criteria == null
            ? new EnrollmentComplexSearchCriteria(List.of(), List.of(), List.of(), null, null, null, null)
            : criteria;
    PageQuery safePageQuery = pageQuery == null ? new PageQuery(0, 25) : pageQuery;

    List<String> clauses = new ArrayList<>();
    if (CollectionUtils.isNotEmpty(safeCriteria.projectIds())) {
      clauses.add("en.id.projectId in :projectIds");
    }
    if (CollectionUtils.isNotEmpty(safeCriteria.studentIds())) {
      clauses.add("en.id.studentId in :studentIds");
    }
    if (CollectionUtils.isNotEmpty(safeCriteria.statuses())) {
      clauses.add("en.status in :statuses");
    }
    if (safeCriteria.dateFrom() != null) {
      clauses.add(
          "(en.createdAt >= :dateFrom or en.updatedAt >= :dateFrom or en.acceptedAt >= :dateFrom or en.closingStatusAt >= :dateFrom)");
    }
    if (safeCriteria.dateTo() != null) {
      clauses.add(
          "(en.createdAt <= :dateTo or en.updatedAt <= :dateTo or en.acceptedAt <= :dateTo or en.closingStatusAt <= :dateTo)");
    }
    if (safeCriteria.periodFrom() != null) {
      clauses.add("(fs.startDate >= :periodFrom or fs.dueDate >= :periodFrom)");
    }
    if (safeCriteria.periodTo() != null) {
      clauses.add("(fs.startDate <= :periodTo or fs.dueDate <= :periodTo)");
    }

    String whereClause = clauses.isEmpty() ? "" : " where " + String.join(" and ", clauses);

    TypedQuery<Long> countQuery = em.createQuery(COUNT_BASE + whereClause, Long.class);
    TypedQuery<EnrollmentView> dataQuery =
        em.createQuery(SELECT_BASE + whereClause + ORDER_BY, EnrollmentView.class);

    bindFilters(countQuery, safeCriteria);
    bindFilters(dataQuery, safeCriteria);

    long total = countQuery.getSingleResult();
    PageExecution execution = PageExecution.from(safePageQuery, total);
    List<EnrollmentView> content = execution.apply(dataQuery).getResultList();

    return new PageResult<>(
        content, execution.page(), execution.size(), execution.totalElements(), execution.totalPages());
  }

  private <T> void bindFilters(TypedQuery<T> query, EnrollmentComplexSearchCriteria criteria) {
    if (CollectionUtils.isNotEmpty(criteria.projectIds())) {
      query.setParameter("projectIds", criteria.projectIds());
    }
    if (CollectionUtils.isNotEmpty(criteria.studentIds())) {
      query.setParameter("studentIds", criteria.studentIds());
    }
    if (CollectionUtils.isNotEmpty(criteria.statuses())) {
      query.setParameter("statuses", criteria.statuses());
    }
    if (criteria.dateFrom() != null) {
      query.setParameter("dateFrom", criteria.dateFrom());
    }
    if (criteria.dateTo() != null) {
      query.setParameter("dateTo", criteria.dateTo());
    }
    if (criteria.periodFrom() != null) {
      query.setParameter("periodFrom", criteria.periodFrom());
    }
    if (criteria.periodTo() != null) {
      query.setParameter("periodTo", criteria.periodTo());
    }
  }
}
