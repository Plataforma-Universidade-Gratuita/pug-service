package com.pug.project.infra.read.impl;

import com.pug.project.infra.ProjectMapper;
import com.pug.project.infra.persistence.ProjectEntity;
import com.pug.project.infra.read.ProjectQueries;
import com.pug.project.infra.read.dtos.ProjectView;
import com.pug.project.infra.read.dtos.SchoolProjectView;
import com.pug.shared.infra.search.HibernateSearchUtils;
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
 * com.pug.project.infra.persistence.ProjectsBySchoolsEntity} for school-based relational queries.
 */
@ApplicationScoped
@Transactional(Transactional.TxType.SUPPORTS)
public class ProjectQueriesImpl implements ProjectQueries {

  @Inject EntityManager em;

  private static final String SELECT_BASE =
      """
                  select new com.pug.project.infra.read.dtos.ProjectView(
                    p.id, p.name,
                    new com.pug.partner.infra.read.dtos.EntityView(
                      ent.id, ent.cnpj, ent.name, ent.address,
                      new com.pug.geo.infra.read.dtos.CityView(c.id, c.name, c.ibgeCode),
                      ent.createdAt, ent.updatedAt
                    ),
                    p.description,
                    new com.pug.identity.infra.read.dtos.AccountView(
                      acc.id,
                      new com.pug.identity.infra.read.dtos.UserView(
                        u.id, u.cpf, u.name, u.createdAt, u.updatedAt
                      ),
                      acc.email, acc.accountType, acc.createdAt, acc.updatedAt, acc.active
                    ),
                    p.maxParticipants, p.offeredHours, p.status,
                    p.closedAt, p.createdAt, p.updatedAt
                  )
                  from ProjectEntity p
                  join EntityEntity ent on ent.id = p.entityId
                  join CityEntity c on c.id = ent.cityId
                  join AccountEntity acc on acc.id = p.createdBy
                  join UserEntity u on u.id = acc.userId
                  """;

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
  public SchoolProjectView listBySchool(UUID schoolId) {
    if (schoolId == null) {
      return null;
    }

    var school = em.find(com.pug.academic.infra.persistence.SchoolEntity.class, schoolId);
    if (school == null) {
      return null;
    }

    List<ProjectView> projects =
        em.createQuery(
                SELECT_BASE
                    + " join ProjectsBySchoolsEntity pbs on pbs.id.projectId = p.id "
                    + "where pbs.id.schoolId = :sid"
                    + ORDER_BY_NAME,
                ProjectView.class)
            .setParameter("sid", schoolId)
            .getResultList();

    return ProjectMapper.toSchoolProjectView(school, projects);
  }

  /** {@inheritDoc} */
  @Override
  public List<ProjectView> searchByName(String query) {
    String key = com.pug.shared.utils.StringUtils.fold(query);
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
