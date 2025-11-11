package com.pug.academic.infra.queries;

import com.pug.academic.infra.read.StudentQueries;
import com.pug.academic.infra.read.dtos.StudentView;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/** JPA implementation for StudentQueries. */
@ApplicationScoped
@Transactional(Transactional.TxType.SUPPORTS)
public class StudentQueriesImpl implements StudentQueries {

  @Inject EntityManager entityManager;

  private static final String SELECT_VIEW =
      "select new com.pug.academic.infra.read.dtos.StudentView("
          + "  new com.pug.identity.infra.read.dtos.UserView("
          + "    u.id, u.cpf, u.name, u.email, u.accountType, u.createdAt"
          + "  ),"
          + "  s.academicRegistration,"
          + "  s.campus,"
          + "  new com.pug.academic.infra.read.dtos.CourseView("
          + "    c.id, c.name,"
          + "    new com.pug.academic.infra.read.dtos.SchoolView(sc.id, sc.name)"
          + "  ),"
          + "  s.requiredHours,"
          + "  s.completedHours,"
          + "  s.startDate,"
          + "  s.dueDate"
          + ") "
          + "from com.pug.academic.infra.persistence.StudentEntity s "
          + "join com.pug.identity.infra.persistence.UserEntity u on u.id = s.userId "
          + "join com.pug.academic.infra.persistence.CourseEntity c on c.id = s.courseId "
          + "join com.pug.academic.infra.persistence.SchoolEntity sc on sc.id = c.schoolId ";

  @Override
  public Optional<StudentView> findOptionalById(UUID userId) {
    if (userId == null) {
      return Optional.empty();
    }
    var q = entityManager.createQuery(SELECT_VIEW + "where s.userId = :id", StudentView.class);
    q.setParameter("id", userId);
    return q.getResultStream().findFirst();
  }

  @Override
  public Optional<StudentView> findOptionalByAcademicRegistration(String academicRegistration) {
    if (academicRegistration == null || academicRegistration.isBlank()) {
      return Optional.empty();
    }
    var q =
        entityManager.createQuery(
            SELECT_VIEW + "where s.academicRegistration = :ar", StudentView.class);
    q.setParameter("ar", academicRegistration);
    return q.getResultStream().findFirst();
  }

  @Override
  public List<StudentView> listAllByIds(Iterable<UUID> userIds) {
    if (userIds == null || !userIds.iterator().hasNext()) {
      return List.of();
    }
    var q =
        entityManager.createQuery(
            SELECT_VIEW + "where s.userId in :ids order by u.name asc", StudentView.class);
    q.setParameter("ids", userIds);
    return q.getResultList();
  }

  @Override
  public List<StudentView> listAllStudents() {
    var q = entityManager.createQuery(SELECT_VIEW + "order by u.name asc", StudentView.class);
    return q.getResultList();
  }

  @Override
  public List<StudentView> listAllByCourseId(UUID courseId) {
    if (courseId == null) {
      return List.of();
    }
    var q =
        entityManager.createQuery(
            SELECT_VIEW + "where s.courseId = :cid order by u.name asc", StudentView.class);
    q.setParameter("cid", courseId);
    return q.getResultList();
  }
}
