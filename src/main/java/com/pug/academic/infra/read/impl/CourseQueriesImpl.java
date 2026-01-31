package com.pug.academic.infra.read.impl;

import static com.pug.academic.infra.CourseMapper.toView;

import com.pug.academic.infra.persistence.CourseEntity;
import com.pug.academic.infra.persistence.SchoolEntity;
import com.pug.academic.infra.read.CourseQueries;
import com.pug.academic.infra.read.dtos.CourseView;
import com.pug.shared.infra.search.HibernateSearchUtils;
import com.pug.shared.utils.StringUtils;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/** Implementation of CourseQueries using JPA and Hibernate Search. */
@ApplicationScoped
@Transactional(Transactional.TxType.SUPPORTS)
public class CourseQueriesImpl implements CourseQueries {

  @Inject EntityManager entityManager;

  private static final String SELECT_BASE =
      """
                  select new com.pug.academic.infra.read.dtos.CourseView(
                    c.id, c.name,
                    new com.pug.academic.infra.read.dtos.SchoolView(s.id, s.name)
                  )
                  from CourseEntity c
                  left join SchoolEntity s on s.id = c.schoolId
                  """;

  private static final String ORDER_BY_NAME_ASC = " order by c.name asc";

  @Override
  public Optional<CourseView> findOptionalById(UUID id) {
    if (id == null) {
      return Optional.empty();
    }
    var q = entityManager.createQuery(SELECT_BASE + " where c.id = :id", CourseView.class);
    q.setParameter("id", id);
    return q.getResultStream().findFirst();
  }

  @Override
  public Optional<CourseView> findOptionalByName(String name) {
    if (StringUtils.isEmpty(name)) {
      return Optional.empty();
    }
    var q = entityManager.createQuery(SELECT_BASE + " where c.name = :name", CourseView.class);
    q.setParameter("name", name);
    return q.getResultStream().findFirst();
  }

  @Override
  public List<CourseView> listAllCourses() {
    return entityManager
        .createQuery(SELECT_BASE + ORDER_BY_NAME_ASC, CourseView.class)
        .getResultList();
  }

  @Override
  public List<CourseView> listAllBySchoolId(UUID schoolId) {
    if (schoolId == null) {
      return List.of();
    }
    var q =
        entityManager.createQuery(
            SELECT_BASE + " where c.schoolId = :schoolId" + ORDER_BY_NAME_ASC, CourseView.class);
    q.setParameter("schoolId", schoolId);
    return q.getResultList();
  }

  @Override
  public List<CourseView> searchByName(String key) {
    List<CourseEntity> hits =
        HibernateSearchUtils.searchByName(entityManager, CourseEntity.class, key);

    if (hits.isEmpty()) {
      return List.of();
    }

    List<UUID> schoolIds = hits.stream().map(CourseEntity::getSchoolId).distinct().toList();

    List<SchoolEntity> schools =
        entityManager
            .createQuery("from SchoolEntity where id in :ids", SchoolEntity.class)
            .setParameter("ids", schoolIds)
            .getResultList();

    Map<UUID, SchoolEntity> schoolMap =
        schools.stream().collect(Collectors.toMap(SchoolEntity::getId, s -> s));

    List<CourseView> out = new ArrayList<>(hits.size());
    for (CourseEntity c : hits) {
      SchoolEntity s = schoolMap.get(c.getSchoolId());
      out.add(toView(c, s));
    }
    return out;
  }
}
