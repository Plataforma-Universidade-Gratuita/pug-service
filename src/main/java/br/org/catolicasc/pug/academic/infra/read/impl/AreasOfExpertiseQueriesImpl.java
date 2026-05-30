package br.org.catolicasc.pug.academic.infra.read.impl;

import br.org.catolicasc.pug.academic.infra.read.AreasOfExpertiseQueries;
import br.org.catolicasc.pug.academic.infra.read.dtos.AreaOfExpertiseView;
import br.org.catolicasc.pug.academic.service.dtos.areasofexpertise.AreaOfExpertiseComplexSearchCriteria;
import br.org.catolicasc.pug.shared.infra.persistence.JpaSearchUtils;
import br.org.catolicasc.pug.shared.service.dtos.PageExecution;
import br.org.catolicasc.pug.shared.service.dtos.PageQuery;
import br.org.catolicasc.pug.shared.service.dtos.PageResult;
import br.org.catolicasc.pug.shared.utils.CollectionUtils;
import br.org.catolicasc.pug.shared.utils.StringUtils;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/** JPA implementation of the area-of-expertise query contract. */
@ApplicationScoped
@Transactional(Transactional.TxType.SUPPORTS)
public class AreasOfExpertiseQueriesImpl implements AreasOfExpertiseQueries {

  @Inject EntityManager entityManager;

  /** {@inheritDoc} */
  @Override
  public Optional<AreaOfExpertiseView> findOptionalById(UUID id) {
    if (id == null) {
      return Optional.empty();
    }
    return entityManager
        .createQuery(
            "select new br.org.catolicasc.pug.academic.infra.read.dtos.AreaOfExpertiseView("
                + "s.id, s.name, s.createdAt, s.updatedAt) "
                + "from SchoolEntity s where s.id = :id",
            AreaOfExpertiseView.class)
        .setParameter("id", id)
        .getResultStream()
        .findFirst();
  }

  /** {@inheritDoc} */
  @Override
  public List<AreaOfExpertiseView> listAllByIds(List<UUID> ids) {
    if (CollectionUtils.isEmpty(ids)) {
      return List.of();
    }
    return entityManager
        .createQuery(
            "select new br.org.catolicasc.pug.academic.infra.read.dtos.AreaOfExpertiseView("
                + "s.id, s.name, s.createdAt, s.updatedAt) "
                + "from SchoolEntity s where s.id in :ids order by s.name asc",
            AreaOfExpertiseView.class)
        .setParameter("ids", ids)
        .getResultList();
  }

  /** {@inheritDoc} */
  @Override
  public List<AreaOfExpertiseView> listAllViews() {
    return entityManager
        .createQuery(
            "select new br.org.catolicasc.pug.academic.infra.read.dtos.AreaOfExpertiseView("
                + "s.id, s.name, s.createdAt, s.updatedAt) "
                + "from SchoolEntity s order by s.name asc",
            AreaOfExpertiseView.class)
        .getResultList();
  }

  /** {@inheritDoc} */
  @Override
  public PageResult<AreaOfExpertiseView> search(
      PageQuery pageQuery, AreaOfExpertiseComplexSearchCriteria criteria) {
    String name = criteria == null ? null : criteria.name();
    String whereClause =
        StringUtils.isNotEmpty(name)
            ? " where " + JpaSearchUtils.containsClause("s.name", "namePattern")
            : "";

    var countQuery =
        entityManager.createQuery(
            "select count(s.id) from SchoolEntity s" + whereClause, Long.class);
    bindSearchParameters(countQuery, name);
    long totalElements = countQuery.getSingleResult();
    PageExecution pageExecution = PageExecution.from(pageQuery, totalElements);

    var dataQuery =
        entityManager.createQuery(
            "select new br.org.catolicasc.pug.academic.infra.read.dtos.AreaOfExpertiseView("
                + "s.id, s.name, s.createdAt, s.updatedAt) "
                + "from SchoolEntity s"
                + whereClause
                + " order by s.name asc",
            AreaOfExpertiseView.class);
    bindSearchParameters(dataQuery, name);

    return new PageResult<>(
        pageExecution.apply(dataQuery).getResultList(),
        pageExecution.page(),
        pageExecution.size(),
        pageExecution.totalElements(),
        pageExecution.totalPages());
  }

  private <T> void bindSearchParameters(TypedQuery<T> query, String name) {
    if (StringUtils.isNotEmpty(name)) {
      JpaSearchUtils.bindContains(query, "namePattern", name);
    }
  }
}
