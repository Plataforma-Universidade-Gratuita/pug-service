package com.pug.project.infra.read.impl;

import com.pug.project.infra.ProjectMapper;
import com.pug.project.infra.read.ProjectsBySchoolsQueries;
import com.pug.project.infra.read.dtos.ProjectView;
import com.pug.project.infra.read.dtos.SchoolProjectView;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.UUID;

/**
 * Implementation of the {@link ProjectsBySchoolsQueries} interface.
 *
 * <p>This query implementation provides specialized access to the relational mapping between
 * Projects and Schools. It leverages the {@link EntityManager} to execute efficient JPQL queries,
 * either by consolidating a full school-project projection or by resolving specific association
 * identifiers.
 */
@ApplicationScoped
@Transactional(Transactional.TxType.SUPPORTS)
public class ProjectsBySchoolsQueriesImpl implements ProjectsBySchoolsQueries {

  @Inject EntityManager em;

  private static final String SELECT_BASE =
      """
                     select new com.pug.project.infra.read.dtos.ProjectView(
                       p.id, p.name, p.entityId, p.description, p.createdBy,
                       p.maxParticipants, p.offeredHours,\s
                       com.pug.project.domain.enums.ProjectStatus.valueOf(p.status),
                       p.closedAt, p.createdAt, p.updatedAt
                     )
                     from ProjectEntity p
                    \s""";

  /** {@inheritDoc} */
  @Override
  public SchoolProjectView listBySchool(UUID schoolId) {
    if (schoolId == null) {
      return null;
    }

    var school = em.find(com.pug.academic.infra.persistence.SchoolEntity.class, schoolId);
    List<ProjectView> projects =
        em.createQuery(
                SELECT_BASE
                    + " join ProjectsBySchoolsEntity pbs on pbs.id.projectId = p.id "
                    + "where pbs.id.schoolId = :sid order by p.name asc",
                ProjectView.class)
            .setParameter("sid", schoolId)
            .getResultList();

    return ProjectMapper.toSchoolProjectView(school, projects);
  }

  /** {@inheritDoc} */
  @Override
  public List<UUID> listAllSchoolsIdsByProjectId(UUID projectId) {
    if (projectId == null) {
      return List.of();
    }

    return em.createQuery(
            "select pbs.id.schoolId from ProjectsBySchoolsEntity pbs where pbs.id.projectId = :pid",
            UUID.class)
        .setParameter("pid", projectId)
        .getResultList();
  }
}
