package br.org.catolicasc.pug.academic.infra.read.impl;

import br.org.catolicasc.pug.academic.infra.read.FormerStudentsQueries;
import br.org.catolicasc.pug.academic.infra.read.dtos.FormerStudentComplexSearchView;
import br.org.catolicasc.pug.academic.infra.read.dtos.FormerStudentView;
import br.org.catolicasc.pug.academic.service.dtos.formerstudents.FormerStudentComplexSearchCriteria;
import br.org.catolicasc.pug.shared.infra.persistence.JpaSearchUtils;
import br.org.catolicasc.pug.shared.service.dtos.PageExecution;
import br.org.catolicasc.pug.shared.service.dtos.PageQuery;
import br.org.catolicasc.pug.shared.service.dtos.PageResult;
import br.org.catolicasc.pug.shared.utils.CollectionUtils;
import br.org.catolicasc.pug.shared.utils.StringUtils;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/** JPA-backed implementation of former-student read queries. */
@ApplicationScoped
@Transactional(Transactional.TxType.SUPPORTS)
public class FormerStudentsQueriesImpl implements FormerStudentsQueries {

  @Inject EntityManager em;

  private static final String SELECT_BASE =
      """
      select new br.org.catolicasc.pug.academic.infra.read.dtos.FormerStudentView(
        s.accountId,
        s.academicRegistration,
        s.campus,
        s.courseId,
        s.requiredHours,
        s.completedHours,
        s.concluded,
        s.startDate,
        s.dueDate,
        s.createdAt,
        s.updatedAt
      )
      from FormerStudentEntity s
      """;

  private static final String COMPLEX_SEARCH_SELECT =
      """
      select new br.org.catolicasc.pug.academic.infra.read.dtos.FormerStudentComplexSearchView(
        new br.org.catolicasc.pug.identity.infra.read.dtos.AccountComplexSearchView(
          acc.id,
          u.id,
          u.name,
          acc.email,
          acc.accountType,
          acc.createdAt,
          acc.updatedAt,
          acc.active
        ),
        s.academicRegistration,
        s.campus,
        s.requiredHours,
        s.completedHours,
        s.concluded,
        s.startDate,
        s.dueDate,
        s.createdAt,
        s.updatedAt,
        new br.org.catolicasc.pug.academic.infra.read.dtos.CourseComplexSearchView(
          c.id,
          c.name,
          new br.org.catolicasc.pug.academic.infra.read.dtos.SchoolComplexSearchView(
            sch.id,
            sch.name
          )
        )
      )
      from FormerStudentEntity s
        join AccountEntity acc on acc.id = s.accountId
        join UserEntity u on u.id = acc.userId
        join CourseEntity c on c.id = s.courseId
        join SchoolEntity sch on sch.id = c.schoolId
      """;

  private static final String ORDER_BY_PERSON_NAME_ASC = " order by u.name asc";

  @Override
  public Optional<FormerStudentView> findOptionalById(UUID accountId) {
    if (accountId == null) {
      return Optional.empty();
    }
    return em.createQuery(SELECT_BASE + " where s.accountId = :id", FormerStudentView.class)
        .setParameter("id", accountId)
        .getResultStream()
        .findFirst();
  }

  @Override
  public List<FormerStudentView> listAllByIds(List<UUID> accountIds) {
    if (CollectionUtils.isEmpty(accountIds)) {
      return List.of();
    }
    return em.createQuery(
            SELECT_BASE + " where s.accountId in :ids order by s.academicRegistration asc",
            FormerStudentView.class)
        .setParameter("ids", accountIds)
        .getResultList();
  }

  @Override
  public List<FormerStudentView> listAllFormerStudents() {
    return em.createQuery(
            SELECT_BASE + " order by s.academicRegistration asc", FormerStudentView.class)
        .getResultList();
  }

  @Override
  public PageResult<FormerStudentComplexSearchView> search(
      PageQuery pageQuery, FormerStudentComplexSearchCriteria criteria) {
    String academicRegistration = criteria == null ? null : criteria.academicRegistration();
    List<?> campi = criteria == null ? List.of() : criteria.campi();
    List<UUID> courseIds = criteria == null ? List.of() : criteria.courseIds();
    OffsetDateTime dateFrom = criteria == null ? null : criteria.dateFrom();
    OffsetDateTime dateTo = criteria == null ? null : criteria.dateTo();
    String email = criteria == null ? null : criteria.email();
    boolean includeConcluded = criteria != null && criteria.includeConcluded();
    String name = criteria == null ? null : criteria.name();
    LocalDate periodFrom = criteria == null ? null : criteria.periodFrom();
    LocalDate periodTo = criteria == null ? null : criteria.periodTo();
    String cpf = criteria == null ? null : criteria.cpf();
    List<UUID> schoolIds = criteria == null ? List.of() : criteria.schoolIds();
    boolean activeOnly = criteria == null || criteria.activeOnly();

    List<String> clauses = new ArrayList<>();
    if (activeOnly) {
      clauses.add("acc.active = true");
    }
    if (!includeConcluded) {
      clauses.add("s.concluded = false");
    }
    if (StringUtils.isNotEmpty(academicRegistration)) {
      clauses.add(
          JpaSearchUtils.containsClause("s.academicRegistration", "academicRegistrationPattern"));
    }
    if (CollectionUtils.isNotEmpty(campi)) {
      clauses.add("s.campus in :campi");
    }
    if (CollectionUtils.isNotEmpty(courseIds)) {
      clauses.add("c.id in :courseIds");
    }
    if (dateFrom != null) {
      clauses.add(
          "(s.createdAt >= :dateFrom or s.updatedAt >= :dateFrom or acc.createdAt >= :dateFrom"
              + " or acc.updatedAt >= :dateFrom or u.createdAt >= :dateFrom or u.updatedAt >= :dateFrom"
              + " or c.createdAt >= :dateFrom or c.updatedAt >= :dateFrom or sch.createdAt >= :dateFrom"
              + " or sch.updatedAt >= :dateFrom)");
    }
    if (dateTo != null) {
      clauses.add(
          "(s.createdAt <= :dateTo or s.updatedAt <= :dateTo or acc.createdAt <= :dateTo"
              + " or acc.updatedAt <= :dateTo or u.createdAt <= :dateTo or u.updatedAt <= :dateTo"
              + " or c.createdAt <= :dateTo or c.updatedAt <= :dateTo or sch.createdAt <= :dateTo"
              + " or sch.updatedAt <= :dateTo)");
    }
    if (StringUtils.isNotEmpty(email)) {
      clauses.add(JpaSearchUtils.containsClause("acc.email", "emailPattern"));
    }
    if (StringUtils.isNotEmpty(name)) {
      clauses.add(JpaSearchUtils.containsClause("u.name", "namePattern"));
    }
    if (periodFrom != null) {
      clauses.add("(s.startDate >= :periodFrom or s.dueDate >= :periodFrom)");
    }
    if (periodTo != null) {
      clauses.add("(s.startDate <= :periodTo or s.dueDate <= :periodTo)");
    }
    if (StringUtils.isNotEmpty(cpf)) {
      clauses.add(JpaSearchUtils.containsClause("u.cpf", "cpfPattern"));
    }
    if (CollectionUtils.isNotEmpty(schoolIds)) {
      clauses.add("sch.id in :schoolIds");
    }

    String whereClause = clauses.isEmpty() ? "" : " where " + String.join(" and ", clauses);

    TypedQuery<Long> countQuery =
        em.createQuery(
            "select count(s.accountId) from FormerStudentEntity s"
                + " join AccountEntity acc on acc.id = s.accountId"
                + " join UserEntity u on u.id = acc.userId"
                + " join CourseEntity c on c.id = s.courseId"
                + " join SchoolEntity sch on sch.id = c.schoolId"
                + whereClause,
            Long.class);
    bindSearchParameters(
        countQuery,
        academicRegistration,
        campi,
        courseIds,
        dateFrom,
        dateTo,
        email,
        name,
        periodFrom,
        periodTo,
        cpf,
        schoolIds);

    long totalElements = countQuery.getSingleResult();
    PageExecution pageExecution = PageExecution.from(pageQuery, totalElements);

    TypedQuery<FormerStudentComplexSearchView> dataQuery =
        em.createQuery(
            COMPLEX_SEARCH_SELECT + whereClause + ORDER_BY_PERSON_NAME_ASC,
            FormerStudentComplexSearchView.class);
    bindSearchParameters(
        dataQuery,
        academicRegistration,
        campi,
        courseIds,
        dateFrom,
        dateTo,
        email,
        name,
        periodFrom,
        periodTo,
        cpf,
        schoolIds);

    return new PageResult<>(
        pageExecution.apply(dataQuery).getResultList(),
        pageExecution.page(),
        pageExecution.size(),
        pageExecution.totalElements(),
        pageExecution.totalPages());
  }

  private <T> void bindSearchParameters(
      TypedQuery<T> query,
      String academicRegistration,
      List<?> campi,
      List<UUID> courseIds,
      OffsetDateTime dateFrom,
      OffsetDateTime dateTo,
      String email,
      String name,
      LocalDate periodFrom,
      LocalDate periodTo,
      String cpf,
      List<UUID> schoolIds) {
    if (StringUtils.isNotEmpty(academicRegistration)) {
      JpaSearchUtils.bindContains(query, "academicRegistrationPattern", academicRegistration);
    }
    if (CollectionUtils.isNotEmpty(campi)) {
      query.setParameter("campi", campi);
    }
    if (CollectionUtils.isNotEmpty(courseIds)) {
      query.setParameter("courseIds", courseIds);
    }
    if (dateFrom != null) {
      query.setParameter("dateFrom", dateFrom);
    }
    if (dateTo != null) {
      query.setParameter("dateTo", dateTo);
    }
    if (StringUtils.isNotEmpty(email)) {
      JpaSearchUtils.bindContains(query, "emailPattern", email);
    }
    if (StringUtils.isNotEmpty(name)) {
      JpaSearchUtils.bindContains(query, "namePattern", name);
    }
    if (periodFrom != null) {
      query.setParameter("periodFrom", periodFrom);
    }
    if (periodTo != null) {
      query.setParameter("periodTo", periodTo);
    }
    if (StringUtils.isNotEmpty(cpf)) {
      JpaSearchUtils.bindContains(query, "cpfPattern", cpf);
    }
    if (CollectionUtils.isNotEmpty(schoolIds)) {
      query.setParameter("schoolIds", schoolIds);
    }
  }
}
