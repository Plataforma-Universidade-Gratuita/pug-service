package br.org.catolicasc.pug.project.infra.read.impl;

import br.org.catolicasc.pug.project.infra.persistence.ProjectsBySchoolsEntity;
import br.org.catolicasc.pug.project.infra.persistence.ProjectEntity;
import br.org.catolicasc.pug.project.infra.read.ProjectQueries;
import br.org.catolicasc.pug.project.infra.read.dtos.ProjectView;
import br.org.catolicasc.pug.shared.infra.search.HibernateSearchUtils;
import br.org.catolicasc.pug.shared.utils.StringUtils;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Implementation of the {@link ProjectQueries} interface.
 *
 * <p>Uses JPQL constructor expressions to join the project graph and {@link
 * ProjectsBySchoolsEntity} for school-based relational queries.
 */
@ApplicationScoped
@Transactional(Transactional.TxType.SUPPORTS)
public class ProjectQueriesImpl implements ProjectQueries {

  @Inject EntityManager em;

  private static final String SELECT_BASE =
      """
                       select new com.pug.project.infra.read.dtos.ProjectView(
                         p.id, p.name, p.entityId, p.description, p.createdBy,
                         p.maxParticipants, p.offeredHours, p.completedHours,
                         com.pug.project.domain.enums.ProjectStatus.valueOf(p.status),
                         p.closedAt, p.createdAt, p.updatedAt
                       )
                       from ProjectEntity p
                      \s""";

  private static final String ORDER_BY_NAME = " order by p.name asc";

  /** {@inheritDoc} */
  @Override
  public Optional<ProjectView> findOptionalById(UUID id) {
    if (id == null) {
      return Optional.empty();
    }
    var q = em.createQuery(SELECT_BASE + " where p.id = :id", ProjectView.class);
    q.setParameter("id", id);
    return q.getResultStream().findFirst();
  }

  /** {@inheritDoc} */
  @Override
  public List<ProjectView> listAllProjects() {
    return em.createQuery(SELECT_BASE + ORDER_BY_NAME, ProjectView.class).getResultList();
  }

  /** {@inheritDoc} */
  @Override
  public List<ProjectView> listByCreatedBy(UUID accountId) {
    if (accountId == null) {
      return List.of();
    }
    return em.createQuery(
            SELECT_BASE + " where p.createdBy = :accId" + ORDER_BY_NAME, ProjectView.class)
        .setParameter("accId", accountId)
        .getResultList();
  }

  /** {@inheritDoc} */
  @Override
  public List<ProjectView> listByEntityId(UUID entityId) {
    if (entityId == null) {
      return List.of();
    }
    return em.createQuery(
            SELECT_BASE + " where p.entityId = :eid" + ORDER_BY_NAME, ProjectView.class)
        .setParameter("eid", entityId)
        .getResultList();
  }

  /** {@inheritDoc} */
  @Override
  public List<ProjectView> listByIds(List<UUID> ids) {
    if (ids == null) {
      return List.of();
    }
    return em.createQuery(SELECT_BASE + " where p.id in :ids" + ORDER_BY_NAME, ProjectView.class)
        .setParameter("ids", ids)
        .getResultList();
  }

  /** {@inheritDoc} */
  @Override
  public List<ProjectView> searchByName(String query) {
    String key = StringUtils.fold(query);
    List<ProjectEntity> hits = HibernateSearchUtils.searchByName(em, ProjectEntity.class, key);

    if (hits.isEmpty()) {
      return List.of();
    }
    List<UUID> ids = hits.stream().map(ProjectEntity::getId).toList();

    return em.createQuery(SELECT_BASE + " where p.id in :ids" + ORDER_BY_NAME, ProjectView.class)
        .setParameter("ids", ids)
        .getResultList();
  }
}
