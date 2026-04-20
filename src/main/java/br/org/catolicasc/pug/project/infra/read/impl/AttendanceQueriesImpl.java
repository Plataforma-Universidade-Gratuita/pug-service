package br.org.catolicasc.pug.project.infra.read.impl;

import br.org.catolicasc.pug.project.infra.read.AttendanceQueries;
import br.org.catolicasc.pug.project.infra.read.dtos.AttendanceView;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Implementation of the {@link AttendanceQueries} interface using JPA constructor expressions.
 *
 * <p>Projects attendance data into lightweight DTOs without nested relations.
 */
@ApplicationScoped
@Transactional(Transactional.TxType.SUPPORTS)
public class AttendanceQueriesImpl implements AttendanceQueries {

  @Inject EntityManager em;

  private static final String SELECT_BASE =
      """
          select new br.org.catolicasc.pug.project.infra.read.dtos.AttendanceView(
            a.id, a.projectId, a.studentId, a.duration, a.qrValidationHash,\s
            br.org.catolicasc.pug.project.domain.enums.AttendanceStatus.valueOf(a.status),
            a.validatedBy, a.validatedAt, a.createdAt, a.updatedAt
          )
          from AttendanceEntity a
         \s""";

  private static final String ORDER_BY_DATE = " order by a.createdAt desc";

  /** {@inheritDoc} */
  @Override
  public Optional<AttendanceView> findOptionalById(UUID id) {
    if (id == null) {
      return Optional.empty();
    }
    var q = em.createQuery(SELECT_BASE + " where a.id = :id", AttendanceView.class);
    q.setParameter("id", id);
    return q.getResultStream().findFirst();
  }

  /** {@inheritDoc} */
  @Override
  public List<AttendanceView> listByEnrollmentId(UUID projectId, UUID studentId) {
    if (projectId == null || studentId == null) {
      return List.of();
    }
    var q =
        em.createQuery(
            SELECT_BASE + " where a.projectId = :pid and a.studentId = :sid" + ORDER_BY_DATE,
            AttendanceView.class);
    q.setParameter("pid", projectId);
    q.setParameter("sid", studentId);
    return q.getResultList();
  }

  /** {@inheritDoc} */
  @Override
  public List<AttendanceView> listByProjectId(UUID projectId) {
    if (projectId == null) {
      return List.of();
    }
    var q =
        em.createQuery(
            SELECT_BASE + " where a.projectId = :pid" + ORDER_BY_DATE, AttendanceView.class);
    q.setParameter("pid", projectId);
    return q.getResultList();
  }

  /** {@inheritDoc} */
  @Override
  public List<AttendanceView> listByStudentId(UUID studentId) {
    if (studentId == null) {
      return List.of();
    }
    var q =
        em.createQuery(
            SELECT_BASE + " where a.studentId = :sid" + ORDER_BY_DATE, AttendanceView.class);
    q.setParameter("sid", studentId);
    return q.getResultList();
  }

  /** {@inheritDoc} */
  @Override
  public List<AttendanceView> listViews() {
    return em.createQuery(SELECT_BASE + ORDER_BY_DATE, AttendanceView.class).getResultList();
  }
}
