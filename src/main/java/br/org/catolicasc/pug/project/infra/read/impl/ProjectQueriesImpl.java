package br.org.catolicasc.pug.project.infra.read.impl;

import br.org.catolicasc.pug.project.infra.read.ProjectQueries;
import br.org.catolicasc.pug.project.infra.read.dtos.ProjectView;
import br.org.catolicasc.pug.project.service.dtos.projects.ProjectComplexSearchCriteria;
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
 * JPQL-backed implementation of the {@link ProjectQueries} contract.
 *
 * <p>This query component joins partner entities directly in the read model so presenter responses
 * can expose the entity identifier and display name without issuing service-layer merge queries.
 */
@ApplicationScoped
@Transactional(Transactional.TxType.SUPPORTS)
public class ProjectQueriesImpl implements ProjectQueries {

  private static final String SELECT_BASE =
      """
      select new br.org.catolicasc.pug.project.infra.read.dtos.ProjectView(
        p.id,
        p.name,
        p.entityId,
        e.name,
        p.description,
        p.createdBy,
        p.maxParticipants,
        p.offeredHours,
        p.completedHours,
        p.status,
        p.closedAt,
        p.createdAt,
        p.updatedAt
      )
      from ProjectEntity p
      join EntityEntity e on e.id = p.entityId
      """;

  private static final String COUNT_BASE =
      """
      select count(p.id)
      from ProjectEntity p
      join EntityEntity e on e.id = p.entityId
      """;

  private static final String ORDER_BY = " order by p.name asc";

  @Inject EntityManager em;

  /** {@inheritDoc} */
  @Override
  public Optional<ProjectView> findOptionalById(UUID id) {
    if (id == null) {
      return Optional.empty();
    }

    return em.createQuery(SELECT_BASE + " where p.id = :id", ProjectView.class)
        .setParameter("id", id)
        .getResultStream()
        .findFirst();
  }

  /** {@inheritDoc} */
  @Override
  public List<ProjectView> listAll() {
    return em.createQuery(SELECT_BASE + ORDER_BY, ProjectView.class).getResultList();
  }

  /** {@inheritDoc} */
  @Override
  public List<ProjectView> listAllByCreatedBy(UUID accountId) {
    if (accountId == null) {
      return List.of();
    }

    return em.createQuery(
            SELECT_BASE + " where p.createdBy = :createdBy" + ORDER_BY, ProjectView.class)
        .setParameter("createdBy", accountId)
        .getResultList();
  }

  /** {@inheritDoc} */
  @Override
  public List<ProjectView> listAllByEntityId(UUID entityId) {
    if (entityId == null) {
      return List.of();
    }

    return em.createQuery(
            SELECT_BASE + " where p.entityId = :entityId" + ORDER_BY, ProjectView.class)
        .setParameter("entityId", entityId)
        .getResultList();
  }

  /** {@inheritDoc} */
  @Override
  public List<ProjectView> listAllByIds(List<UUID> ids) {
    if (CollectionUtils.isEmpty(ids)) {
      return List.of();
    }

    return em.createQuery(SELECT_BASE + " where p.id in :ids" + ORDER_BY, ProjectView.class)
        .setParameter("ids", ids)
        .getResultList();
  }

  /** {@inheritDoc} */
  @Override
  public PageResult<ProjectView> search(
      ProjectComplexSearchCriteria criteria, PageQuery pageQuery) {
    ProjectComplexSearchCriteria safeCriteria =
        criteria == null
            ? new ProjectComplexSearchCriteria(
                null, List.of(), null, List.of(), null, null, List.of(), null, null)
            : criteria;
    PageQuery safePageQuery = pageQuery == null ? new PageQuery(0, 25) : pageQuery;

    List<String> clauses = new ArrayList<>();

    if (StringUtils.isNotEmpty(safeCriteria.name())) {
      clauses.add(JpaSearchUtils.containsClause("p.name", "namePattern"));
    }
    if (CollectionUtils.isNotEmpty(safeCriteria.entityIds())) {
      clauses.add("p.entityId in :entityIds");
    }
    if (StringUtils.isNotEmpty(safeCriteria.description())) {
      clauses.add(JpaSearchUtils.containsClause("p.description", "descriptionPattern"));
    }
    if (CollectionUtils.isNotEmpty(safeCriteria.createdByIds())) {
      clauses.add("p.createdBy in :createdByIds");
    }
    if (safeCriteria.dateFrom() != null) {
      clauses.add(
          "(p.createdAt >= :dateFrom or p.updatedAt >= :dateFrom or p.closedAt >= :dateFrom)");
    }
    if (safeCriteria.dateTo() != null) {
      clauses.add("(p.createdAt <= :dateTo or p.updatedAt <= :dateTo or p.closedAt <= :dateTo)");
    }
    if (CollectionUtils.isNotEmpty(safeCriteria.statuses())) {
      clauses.add("p.status in :statuses");
    }
    if (safeCriteria.maxOfferedHours() != null) {
      clauses.add("p.offeredHours <= :maxOfferedHours");
    }
    if (safeCriteria.minOfferedHours() != null) {
      clauses.add("p.offeredHours >= :minOfferedHours");
    }

    String whereClause = clauses.isEmpty() ? "" : " where " + String.join(" and ", clauses);

    TypedQuery<Long> countQuery = em.createQuery(COUNT_BASE + whereClause, Long.class);
    TypedQuery<ProjectView> dataQuery =
        em.createQuery(SELECT_BASE + whereClause + ORDER_BY, ProjectView.class);

    bindFilters(countQuery, safeCriteria);
    bindFilters(dataQuery, safeCriteria);

    long totalElements = countQuery.getSingleResult();
    PageExecution pageExecution = PageExecution.from(safePageQuery, totalElements);
    List<ProjectView> content = pageExecution.apply(dataQuery).getResultList();

    return new PageResult<>(
        content,
        pageExecution.page(),
        pageExecution.size(),
        pageExecution.totalElements(),
        pageExecution.totalPages());
  }

  private <T> void bindFilters(TypedQuery<T> query, ProjectComplexSearchCriteria criteria) {
    if (StringUtils.isNotEmpty(criteria.name())) {
      JpaSearchUtils.bindContains(query, "namePattern", criteria.name());
    }
    if (CollectionUtils.isNotEmpty(criteria.entityIds())) {
      query.setParameter("entityIds", criteria.entityIds());
    }
    if (StringUtils.isNotEmpty(criteria.description())) {
      JpaSearchUtils.bindContains(query, "descriptionPattern", criteria.description());
    }
    if (CollectionUtils.isNotEmpty(criteria.createdByIds())) {
      query.setParameter("createdByIds", criteria.createdByIds());
    }
    if (criteria.dateFrom() != null) {
      query.setParameter("dateFrom", criteria.dateFrom());
    }
    if (criteria.dateTo() != null) {
      query.setParameter("dateTo", criteria.dateTo());
    }
    if (CollectionUtils.isNotEmpty(criteria.statuses())) {
      query.setParameter("statuses", criteria.statuses().stream().map(Enum::name).toList());
    }
    if (criteria.maxOfferedHours() != null) {
      query.setParameter("maxOfferedHours", criteria.maxOfferedHours());
    }
    if (criteria.minOfferedHours() != null) {
      query.setParameter("minOfferedHours", criteria.minOfferedHours());
    }
  }
}
