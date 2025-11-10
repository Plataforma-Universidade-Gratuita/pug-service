// src/main/java/com/pug/academic/infra/queries/StudentQueriesImpl.java
package com.pug.academic.infra.queries;

import com.pug.academic.presenter.dtos.StudentView;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class StudentQueriesImpl implements StudentQueries {

  @Inject EntityManager em;

  @Override
  public Optional<StudentView> findById(UUID userId) {
    var q =
        em.createQuery(
            """
                    select new com.pug.academic.presenter.dtos.StudentView(
                      s.userId, u.cpf, u.name, u.email,
                      s.academicRegistration, s.campus,
                      new com.pug.academic.presenter.dtos.CourseResponse(
                        c.id, c.name,
                        new com.pug.academic.presenter.dtos.SchoolResponse(sc.id, sc.name)
                      ),
                      s.requiredHours, s.completedHours, s.startDate, s.dueDate
                    )
                    from StudentEntity s
                      join UserEntity u on u.id = s.userId
                      join CourseEntity c on c.id = s.courseId
                      join SchoolEntity sc on sc.id = c.schoolId
                    where s.userId = :id
                    """,
            StudentView.class);
    q.setParameter("id", userId);
    return q.getResultList().stream().findFirst();
  }

  @Override
  public List<StudentView> listAll() {
    return em.createQuery(
            """
                select new com.pug.academic.presenter.dtos.StudentView(
                  s.userId, u.cpf, u.name, u.email,
                  s.academicRegistration, s.campus,
                  new com.pug.academic.presenter.dtos.CourseResponse(
                    c.id, c.name,
                    new com.pug.academic.presenter.dtos.SchoolResponse(sc.id, sc.name)
                  ),
                  s.requiredHours, s.completedHours, s.startDate, s.dueDate
                )
                from StudentEntity s
                  join UserEntity u on u.id = s.userId
                  join CourseEntity c on c.id = s.courseId
                  join SchoolEntity sc on sc.id = c.schoolId
                order by u.name
                """,
            StudentView.class)
        .getResultList();
  }

  @Override
  public List<StudentView> listAllByCourseId(UUID courseId) {
    var q =
        em.createQuery(
            """
                    select new com.pug.academic.presenter.dtos.StudentView(
                      s.userId, u.cpf, u.name, u.email,
                      s.academicRegistration, s.campus,
                      new com.pug.academic.presenter.dtos.CourseResponse(
                        c.id, c.name,
                        new com.pug.academic.presenter.dtos.SchoolResponse(sc.id, sc.name)
                      ),
                      s.requiredHours, s.completedHours, s.startDate, s.dueDate
                    )
                    from StudentEntity s
                      join UserEntity u on u.id = s.userId
                      join CourseEntity c on c.id = s.courseId
                      join SchoolEntity sc on sc.id = c.schoolId
                    where s.courseId = :courseId
                    order by u.name
                    """,
            StudentView.class);
    q.setParameter("courseId", courseId);
    return q.getResultList();
  }
}
