package com.pug.academic.infra.read.impl;

import com.pug.academic.infra.read.IStudentQueries;
import com.pug.academic.infra.read.dtos.StudentAcc;
import com.pug.academic.infra.read.dtos.StudentView;
import com.pug.identity.infra.persistence.UserEntity;
import com.pug.shared.infra.search.HibernateSearchUtils;
import com.pug.shared.utils.CollectionUtils;
import com.pug.shared.utils.StringUtils;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static com.pug.academic.infra.StudentMapper.toView;

/**
 * Implementation of StudentQueries using JPA and Hibernate Search.
 */
@ApplicationScoped
@Transactional(Transactional.TxType.SUPPORTS)
public class StudentQueries implements IStudentQueries {

  @Inject
  EntityManager em;

  private static final String SELECT_BASE =
          """
                  select new com.pug.academic.infra.read.dtos.StudentView(
                    new com.pug.identity.infra.read.dtos.AccountView(
                      acc.id,
                      new com.pug.identity.infra.read.dtos.UserView(u.id, u.cpf, u.name, u.createdAt),
                      acc.email, acc.accountType, acc.createdAt
                    ),
                    s.academicRegistration,
                    s.campus,
                    new com.pug.academic.infra.read.dtos.CourseView(
                      c.id, c.name,
                      new com.pug.academic.infra.read.dtos.SchoolView(sch.id, sch.name)
                    ),
                    s.requiredHours, s.completedHours,
                    s.startDate, s.dueDate
                  )
                  from StudentEntity s
                  join AccountEntity acc on acc.id = s.accountId
                  join UserEntity u on u.id = acc.userId
                  join CourseEntity c on c.id = s.courseId
                  left join SchoolEntity sch on sch.id = c.schoolId
                  """;

  private static final String ORDER_BY_PERSON_NAME_ASC = " order by u.name asc";

  @Override
  public Optional<StudentView> findOptionalById(UUID accountId) {
    if (accountId == null) {
      return Optional.empty();
    }
    var q = em.createQuery(SELECT_BASE + " where s.accountId = :id", StudentView.class);
    q.setParameter("id", accountId);
    return q.getResultStream().findFirst();
  }

  @Override
  public Optional<StudentView> findOptionalByAcademicRegistration(String academicRegistration) {
    if (StringUtils.isEmpty(academicRegistration)) {
      return Optional.empty();
    }
    var q = em.createQuery(SELECT_BASE + " where s.academicRegistration = :reg", StudentView.class);
    q.setParameter("reg", academicRegistration);
    return q.getResultStream().findFirst();
  }

  @Override
  public List<StudentView> listAllByIds(Iterable<UUID> accountIds) {
    if (CollectionUtils.isEmpty(accountIds)) {
      return List.of();
    }
    var q = em.createQuery(SELECT_BASE + " where s.accountId in :ids" + ORDER_BY_PERSON_NAME_ASC, StudentView.class);
    q.setParameter("ids", accountIds);
    return q.getResultList();
  }

  @Override
  public List<StudentView> listAllStudents() {
    return em.createQuery(SELECT_BASE + ORDER_BY_PERSON_NAME_ASC, StudentView.class).getResultList();
  }

  @Override
  public List<StudentView> listAllByCourseId(UUID courseId) {
    if (courseId == null) {
      return List.of();
    }
    var q = em.createQuery(SELECT_BASE + " where s.courseId = :cid" + ORDER_BY_PERSON_NAME_ASC, StudentView.class);
    q.setParameter("cid", courseId);
    return q.getResultList();
  }

  @Override
  public List<StudentView> searchByName(String key) {
    List<UserEntity> userHits = HibernateSearchUtils.searchByName(em, UserEntity.class, key);
    if (userHits.isEmpty()) {
      return List.of();
    }

    List<UUID> userIds = userHits.stream().map(UserEntity::getId).toList();

    var rows =
            em.createQuery(
                            """
                                    select new com.pug.academic.infra.read.dtos.StudentAcc(s, acc, c, sch)
                                    from StudentEntity s
                                    join AccountEntity acc on acc.id = s.accountId
                                    join CourseEntity c on c.id = s.courseId
                                    left join SchoolEntity sch on sch.id = c.schoolId
                                    where acc.userId in :ids
                                    """,
                            StudentAcc.class)
                    .setParameter("ids", userIds)
                    .getResultList();

    Map<UUID, List<StudentAcc>> byUser = new HashMap<>();
    for (StudentAcc row : rows) {
      if (row.acc() != null && row.acc().getUserId() != null) {
        byUser.computeIfAbsent(row.acc().getUserId(), k -> new ArrayList<>()).add(row);
      }
    }

    List<StudentView> out = new ArrayList<>();
    for (UserEntity u : userHits) {
      List<StudentAcc> pairs = byUser.get(u.getId());
      if (pairs == null) {
        continue;
      }
      for (StudentAcc row : pairs) {
        if (row.s() != null && row.acc() != null && row.c() != null) {
          out.add(toView(row.s(), row.acc(), u, row.c(), row.sch()));
        }
      }
    }
    return out;
  }
}