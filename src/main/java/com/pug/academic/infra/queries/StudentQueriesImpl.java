package com.pug.academic.infra.queries;

import com.pug.academic.infra.persistence.StudentEntity;
import com.pug.academic.infra.read.StudentQueries;
import com.pug.academic.infra.read.dtos.CourseView;
import com.pug.academic.infra.read.dtos.StudentView;
import com.pug.identity.infra.persistence.AccountEntity;
import com.pug.identity.infra.persistence.UserEntity;
import com.pug.identity.infra.read.dtos.AccountView;
import com.pug.identity.infra.read.dtos.UserView;
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
import java.util.stream.Collectors;

/**
 * JPA implementation for StudentQueries.
 */
@ApplicationScoped
@Transactional(Transactional.TxType.SUPPORTS)
public class StudentQueriesImpl implements StudentQueries {

  @Inject
  EntityManager entityManager;

  private static final String SELECT_VIEW =
          "select new com.pug.academic.infra.read.dtos.StudentView("
                  + "  new com.pug.identity.infra.read.dtos.AccountView("
                  + "    acc.id,"
                  + "    new com.pug.identity.infra.read.dtos.UserView(u.id, u.cpf, u.name, u.createdAt),"
                  + "    acc.email,"
                  + "    acc.accountType,"
                  + "    acc.createdAt"
                  + "  ),"
                  + "  s.academicRegistration,"
                  + "  s.campus.toString(),"
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
                  + "join com.pug.identity.infra.persistence.AccountEntity acc on acc.id = s.accountId "
                  + "join com.pug.identity.infra.persistence.UserEntity u on u.id = acc.userId "
                  + "join com.pug.academic.infra.persistence.CourseEntity c on c.id = s.courseId "
                  + "join com.pug.academic.infra.persistence.SchoolEntity sc on sc.id = c.schoolId ";

  /**
   * Helper to convert entities to StudentView. Used in searchByName.
   *
   * @param studentEntity The StudentEntity.
   * @param accountView   The AccountView.
   * @param courseView    The CourseView.
   * @return The StudentView.
   */
  private static StudentView toView(StudentEntity studentEntity, AccountView accountView, CourseView courseView) {
    if (studentEntity == null) {
      return null;
    }
    return new StudentView(
            accountView,
            studentEntity.getAcademicRegistration(),
            studentEntity.getCampus().toString(),
            courseView,
            studentEntity.getRequiredHours(),
            studentEntity.getCompletedHours(),
            studentEntity.getStartDate(),
            studentEntity.getDueDate()
    );
  }

  @Override
  public Optional<StudentView> findOptionalById(UUID accountId) {
    if (accountId == null) {
      return Optional.empty();
    }
    var q = entityManager.createQuery(SELECT_VIEW + "where s.accountId = :id", StudentView.class);
    q.setParameter("id", accountId);
    return q.getResultStream().findFirst();
  }

  @Override
  public Optional<StudentView> findOptionalByAcademicRegistration(String academicRegistration) {
    if (StringUtils.isEmpty(academicRegistration)) {
      return Optional.empty();
    }
    var q =
            entityManager.createQuery(
                    SELECT_VIEW + "where s.academicRegistration = :ar", StudentView.class);
    q.setParameter("ar", academicRegistration);
    return q.getResultStream().findFirst();
  }

  @Override
  public List<StudentView> listAllByIds(Iterable<UUID> accountIds) {
    if (CollectionUtils.isEmpty(accountIds)) {
      return List.of();
    }
    var q =
            entityManager.createQuery(
                    SELECT_VIEW + "where s.accountId in :ids order by u.name asc", StudentView.class);
    q.setParameter("ids", accountIds);
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

  @Override
  public List<StudentView> searchByName(String key) {
    List<UserEntity> userHits =
            HibernateSearchUtils.searchByName(entityManager, UserEntity.class, key);

    if (userHits.isEmpty()) {
      return List.of();
    }

    List<UUID> userIds = userHits.stream().map(UserEntity::getId).toList();

    List<AccountEntity> accountEntities =
            entityManager
                    .createQuery("from AccountEntity acc where acc.userId in :userIds", AccountEntity.class)
                    .setParameter("userIds", userIds)
                    .getResultList();

    if (accountEntities.isEmpty()) {
      return List.of();
    }

    Map<UUID, UserView> userViewMap = userHits.stream()
            .collect(Collectors.toMap(UserEntity::getId, u -> new UserView(u.getId(), u.getCpf(), u.getName(), u.getCreatedAt())));
    Map<UUID, AccountView> accountViewMap = accountEntities.stream()
            .collect(Collectors.toMap(AccountEntity::getId, acc -> new AccountView(
                    acc.getId(),
                    userViewMap.get(acc.getUserId()),
                    acc.getEmail(),
                    acc.getAccountType(),
                    acc.getCreatedAt()
            )));

    List<UUID> accountIds = accountEntities.stream().map(AccountEntity::getId).toList();

    List<StudentEntity> studentEntities =
            entityManager
                    .createQuery("from StudentEntity s where s.accountId in :accountIds", StudentEntity.class)
                    .setParameter("accountIds", accountIds)
                    .getResultList();

    if (studentEntities.isEmpty()) {
      return List.of();
    }

    List<UUID> courseIds = studentEntities.stream()
            .map(StudentEntity::getCourseId)
            .distinct()
            .toList();

    Map<UUID, CourseView> courseViewMap = new HashMap<>();
    if (!courseIds.isEmpty()) {
      List<CourseView> courseViews = entityManager.createQuery(
                      "select new com.pug.academic.infra.read.dtos.CourseView("
                              + "c.id, c.name,"
                              + "new com.pug.academic.infra.read.dtos.SchoolView(sc.id, sc.name)) "
                              + "from CourseEntity c join SchoolEntity sc on sc.id = c.schoolId "
                              + "where c.id in :cids",
                      CourseView.class)
              .setParameter("cids", courseIds)
              .getResultList();
      courseViewMap = courseViews.stream().collect(Collectors.toMap(CourseView::id, cv -> cv));
    }

    List<StudentView> out = new ArrayList<>();
    for (StudentEntity studentEntity : studentEntities) {
      AccountView associatedAccountView = accountViewMap.get(studentEntity.getAccountId());
      CourseView associatedCourseView = courseViewMap.get(studentEntity.getCourseId());

      if (associatedAccountView != null && associatedCourseView != null) {
        out.add(toView(studentEntity, associatedAccountView, associatedCourseView));
      }
    }
    return out;
  }
}