package com.pug.academic.infra.queries;

import com.pug.academic.presenter.dtos.CourseView;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/** Implementation of CourseQueries using JPA. */
@ApplicationScoped
public class CourseQueriesImpl implements CourseQueries {

  @Inject EntityManager em;

  @Override
  public Optional<CourseView> findById(UUID id) {
    var q =
        em.createQuery(
            """
                    select new com.pug.academic.presenter.dtos.CourseView(
                      c.id, c.name,
                      new com.pug.academic.presenter.dtos.SchoolResponse(s.id, s.name)
                    )
                    from CourseEntity c
                      join SchoolEntity s on s.id = c.schoolId
                    where c.id = :id
                    """,
            CourseView.class);
    q.setParameter("id", id);
    return q.getResultList().stream().findFirst();
  }

  @Override
  public List<CourseView> listAll() {
    return em.createQuery(
            """
                    select new com.pug.academic.presenter.dtos.CourseView(
                      c.id, c.name,
                      new com.pug.academic.presenter.dtos.SchoolResponse(s.id, s.name)
                    )
                    from CourseEntity c
                      join SchoolEntity s on s.id = c.schoolId
                    order by c.name
                    """,
            CourseView.class)
        .getResultList();
  }

  @Override
  public List<CourseView> listAllBySchoolId(UUID schoolId) {
    var q =
        em.createQuery(
            """
                    select new com.pug.academic.presenter.dtos.CourseView(
                      c.id, c.name,
                      new com.pug.academic.presenter.dtos.SchoolResponse(s.id, s.name)
                    )
                    from CourseEntity c
                      join SchoolEntity s on s.id = c.schoolId
                    where c.schoolId = :schoolId
                    order by c.name
                    """,
            CourseView.class);
    q.setParameter("schoolId", schoolId);
    return q.getResultList();
  }
}
