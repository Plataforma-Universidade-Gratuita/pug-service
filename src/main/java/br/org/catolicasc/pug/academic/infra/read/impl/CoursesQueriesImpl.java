package br.org.catolicasc.pug.academic.infra.read.impl;

import br.org.catolicasc.pug.academic.infra.read.CoursesQueries;
import br.org.catolicasc.pug.academic.infra.read.dtos.CourseView;
import br.org.catolicasc.pug.academic.service.dtos.courses.CourseComplexSearchCriteria;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Implementation of the {@link CoursesQueries} interface using JPA.
 *
 * <p>This application-scoped bean handles read-only course queries. It uses JPQL constructor
 * expressions to project course rows, together with their linked areaOfExpertise data, into
 * lightweight {@link CourseView} DTOs in a single round-trip.
 */
@ApplicationScoped
@Transactional(Transactional.TxType.SUPPORTS)
public class CoursesQueriesImpl implements CoursesQueries {

  private static final String SELECT_BASE =
      """
      select new br.org.catolicasc.pug.academic.infra.read.dtos.CourseView(
        c.id,
        c.name,
        new br.org.catolicasc.pug.academic.infra.read.dtos.AreaOfExpertiseView(
          s.id,
          s.name,
          s.createdAt,
          s.updatedAt
        ),
        c.createdAt,
        c.updatedAt
      )
      from CourseEntity c
      left join AreaOfExpertiseEntity s on s.id = c.areaOfExpertiseId
      """;

  private static final String ORDER_BY_NAME_ASC = " order by c.name asc";

  @Inject EntityManager entityManager;

  /** {@inheritDoc} */
  @Override
  public Optional<CourseView> findOptionalById(UUID id) {
    if (id == null) {
      return Optional.empty();
    }
    var query = entityManager.createQuery(SELECT_BASE + " where c.id = :id", CourseView.class);
    query.setParameter("id", id);
    return query.getResultStream().findFirst();
  }

  /** {@inheritDoc} */
  @Override
  public List<CourseView> listAllByIds(List<UUID> ids) {
    if (CollectionUtils.isEmpty(ids)) {
      return List.of();
    }
    return entityManager
        .createQuery(SELECT_BASE + " where c.id in :ids" + ORDER_BY_NAME_ASC, CourseView.class)
        .setParameter("ids", ids)
        .getResultList();
  }

  /** {@inheritDoc} */
  @Override
  public List<CourseView> listAllCourses() {
    return entityManager
        .createQuery(SELECT_BASE + ORDER_BY_NAME_ASC, CourseView.class)
        .getResultList();
  }

  /** {@inheritDoc} */
  @Override
  public PageResult<CourseView> search(PageQuery pageQuery, CourseComplexSearchCriteria criteria) {
    List<String> clauses = new ArrayList<>();
    String name = criteria == null ? null : criteria.name();
    List<UUID> areaOfExpertiseIds = criteria == null ? List.of() : criteria.areaOfExpertiseIds();

    if (StringUtils.isNotEmpty(name)) {
      clauses.add(JpaSearchUtils.containsClause("c.name", "namePattern"));
    }
    if (CollectionUtils.isNotEmpty(areaOfExpertiseIds)) {
      clauses.add("c.areaOfExpertiseId in :areaOfExpertiseIds");
    }

    String whereClause = clauses.isEmpty() ? "" : " where " + String.join(" and ", clauses);
    var countQuery =
        entityManager.createQuery(
            "select count(c.id) from CourseEntity c" + whereClause, Long.class);
    bindSearchParameters(countQuery, name, areaOfExpertiseIds);
    long totalElements = countQuery.getSingleResult();
    PageExecution pageExecution = PageExecution.from(pageQuery, totalElements);

    var dataQuery =
        entityManager.createQuery(SELECT_BASE + whereClause + ORDER_BY_NAME_ASC, CourseView.class);
    bindSearchParameters(dataQuery, name, areaOfExpertiseIds);

    return new PageResult<>(
        pageExecution.apply(dataQuery).getResultList(),
        pageExecution.page(),
        pageExecution.size(),
        pageExecution.totalElements(),
        pageExecution.totalPages());
  }

  private <T> void bindSearchParameters(
      TypedQuery<T> query, String name, List<UUID> areaOfExpertiseIds) {
    if (StringUtils.isNotEmpty(name)) {
      JpaSearchUtils.bindContains(query, "namePattern", name);
    }
    if (CollectionUtils.isNotEmpty(areaOfExpertiseIds)) {
      query.setParameter("areaOfExpertiseIds", areaOfExpertiseIds);
    }
  }
}
