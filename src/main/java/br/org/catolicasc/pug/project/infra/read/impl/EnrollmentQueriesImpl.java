package br.org.catolicasc.pug.project.infra.read.impl;

import br.org.catolicasc.pug.project.infra.persistence.EnrollmentEntity;
import br.org.catolicasc.pug.project.infra.read.EnrollmentQueries;
import br.org.catolicasc.pug.project.infra.read.dtos.EnrollmentView;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Implementation of the {@link EnrollmentQueries} interface using JPA constructor expressions.
 *
 * <p>This application-scoped bean executes read-only operations for enrollments. It projects {@link
 * EnrollmentEntity} rows into lightweight {@link EnrollmentView}
 * DTOs containing only identifiers and lifecycle metadata, leaving project and student details to
 * be resolved on demand via dedicated endpoints.
 */
@ApplicationScoped
@Transactional(Transactional.TxType.SUPPORTS)
public class EnrollmentQueriesImpl implements EnrollmentQueries {

  @Inject EntityManager em;

  private static final String SELECT_BASE =
      """
                  select new com.pug.project.infra.read.dtos.EnrollmentView(
                    en.id.projectId,
                    en.id.studentId,
                    com.pug.project.domain.enums.EnrollmentStatus.valueOf(en.status),
                    en.createdAt,
                    en.updatedAt,
                    en.acceptedAt,
                    en.closingStatusAt
                  )
                  from EnrollmentEntity en
                  """;

  private static final String ORDER_BY_DATE = " order by en.createdAt desc";

  /** {@inheritDoc} */
  @Override
  public Optional<EnrollmentView> findOptionalByIds(UUID projectId, UUID studentId) {
    if (projectId == null || studentId == null) {
      return Optional.empty();
    }
    var q =
        em.createQuery(
                SELECT_BASE + " where en.id.projectId = :pid and en.id.studentId = :sid",
                EnrollmentView.class)
            .setParameter("pid", projectId)
            .setParameter("sid", studentId);
    return q.getResultStream().findFirst();
  }

  /** {@inheritDoc} */
  @Override
  public List<EnrollmentView> listAllEnrollments() {
    return em.createQuery(SELECT_BASE + ORDER_BY_DATE, EnrollmentView.class).getResultList();
  }

  /** {@inheritDoc} */
  @Override
  public List<EnrollmentView> listByProjectId(UUID projectId) {
    if (projectId == null) {
      return List.of();
    }
    var q =
        em.createQuery(
                SELECT_BASE + " where en.id.projectId = :pid" + ORDER_BY_DATE, EnrollmentView.class)
            .setParameter("pid", projectId);
    return q.getResultList();
  }

  /** {@inheritDoc} */
  @Override
  public List<EnrollmentView> listByStudentId(UUID studentId) {
    if (studentId == null) {
      return List.of();
    }
    var q =
        em.createQuery(
                SELECT_BASE + " where en.id.studentId = :sid" + ORDER_BY_DATE, EnrollmentView.class)
            .setParameter("sid", studentId);
    return q.getResultList();
  }
}
