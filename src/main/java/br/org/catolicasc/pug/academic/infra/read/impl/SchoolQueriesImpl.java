package br.org.catolicasc.pug.academic.infra.read.impl;

import br.org.catolicasc.pug.academic.infra.read.SchoolQueries;
import br.org.catolicasc.pug.academic.infra.read.dtos.SchoolView;
import br.org.catolicasc.pug.shared.infra.persistence.JpaSearchUtils;
import br.org.catolicasc.pug.shared.utils.CollectionUtils;
import br.org.catolicasc.pug.shared.utils.StringUtils;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Implementation of the {@link SchoolQueries} interface using JPA.
 *
 * <p>This application-scoped bean handles the execution of read-only queries. It uses JPQL
 * constructor expressions to directly project database rows into lightweight {@link SchoolView}
 * DTOs.
 */
@ApplicationScoped
@Transactional(Transactional.TxType.SUPPORTS)
public class SchoolQueriesImpl implements SchoolQueries {

  @Inject EntityManager entityManager;

  /** {@inheritDoc} */
  @Override
  public Optional<SchoolView> findOptionalById(UUID id) {
    if (id == null) {
      return Optional.empty();
    }
    var q =
        entityManager.createQuery(
            "select new br.org.catolicasc.pug.academic.infra.read.dtos.SchoolView("
                + "s.id, s.name, s.createdAt, s.updatedAt) "
                + "from SchoolEntity s where s.id = :id",
            SchoolView.class);
    q.setParameter("id", id);
    return q.getResultStream().findFirst();
  }

  /** {@inheritDoc} */
  @Override
  public List<SchoolView> listAllSchools() {
    var q =
        entityManager.createQuery(
            "select new br.org.catolicasc.pug.academic.infra.read.dtos.SchoolView("
                + "s.id, s.name, s.createdAt, s.updatedAt) "
                + "from SchoolEntity s order by s.name asc",
            SchoolView.class);
    return q.getResultList();
  }

  /** {@inheritDoc} */
  @Override
  public List<SchoolView> listByIds(List<UUID> ids) {
    if (CollectionUtils.isEmpty(ids)) {
      return List.of();
    }
    var q =
        entityManager.createQuery(
            "select new br.org.catolicasc.pug.academic.infra.read.dtos.SchoolView("
                + "s.id, s.name, s.createdAt, s.updatedAt) "
                + "from SchoolEntity s where s.id in :ids order by s.name asc",
            SchoolView.class);
    q.setParameter("ids", ids);
    return q.getResultList();
  }

  /** {@inheritDoc} */
  @Override
  public List<SchoolView> searchByName(String key) {
    if (StringUtils.isEmpty(key)) {
      return List.of();
    }
    var q =
        entityManager.createQuery(
            "select new br.org.catolicasc.pug.academic.infra.read.dtos.SchoolView("
                + "s.id, s.name, s.createdAt, s.updatedAt) "
                + "from SchoolEntity s where "
                + JpaSearchUtils.folded("s.name")
                + " like :pattern order by s.name asc",
            SchoolView.class);
    q.setParameter("pattern", JpaSearchUtils.containsPattern(key));
    return q.getResultList();
  }
}
