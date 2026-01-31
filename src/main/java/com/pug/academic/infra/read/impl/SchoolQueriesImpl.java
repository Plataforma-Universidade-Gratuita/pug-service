package com.pug.academic.infra.read.impl;

import static com.pug.academic.infra.SchoolMapper.toView;

import com.pug.academic.infra.persistence.SchoolEntity;
import com.pug.academic.infra.read.SchoolQueries;
import com.pug.academic.infra.read.dtos.SchoolView;
import com.pug.shared.infra.search.HibernateSearchUtils;
import com.pug.shared.utils.StringUtils;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/** Implementation of SchoolQueries using JPA and Hibernate Search. */
@ApplicationScoped
@Transactional(Transactional.TxType.SUPPORTS)
public class SchoolQueriesImpl implements SchoolQueries {

  @Inject EntityManager entityManager;

  @Override
  public Optional<SchoolView> findOptionalById(UUID id) {
    if (id == null) {
      return Optional.empty();
    }
    var q =
        entityManager.createQuery(
            "select new com.pug.academic.infra.read.dtos.SchoolView("
                + "s.id, s.name) "
                + "from SchoolEntity s where s.id = :id",
            SchoolView.class);
    q.setParameter("id", id);
    return q.getResultStream().findFirst();
  }

  @Override
  public Optional<SchoolView> findOptionalByName(String name) {
    if (StringUtils.isEmpty(name)) {
      return Optional.empty();
    }
    var q =
        entityManager.createQuery(
            "select new com.pug.academic.infra.read.dtos.SchoolView("
                + "s.id, s.name) "
                + "from SchoolEntity s where s.name = :name",
            SchoolView.class);
    q.setParameter("name", name);
    return q.getResultStream().findFirst();
  }

  @Override
  public List<SchoolView> listAllSchools() {
    var q =
        entityManager.createQuery(
            "select new com.pug.academic.infra.read.dtos.SchoolView("
                + "s.id, s.name) "
                + "from SchoolEntity s order by s.name asc",
            SchoolView.class);
    return q.getResultList();
  }

  @Override
  public List<SchoolView> searchByName(String key) {
    List<SchoolEntity> hits =
        HibernateSearchUtils.searchByName(entityManager, SchoolEntity.class, key);

    if (hits.isEmpty()) {
      return List.of();
    }

    List<SchoolView> out = new ArrayList<>(hits.size());
    for (SchoolEntity e : hits) {
      out.add(toView(e));
    }
    return out;
  }
}
