package br.org.catolicasc.pug.geo.infra.read.impl;

import br.org.catolicasc.pug.geo.infra.CityMapper;
import br.org.catolicasc.pug.geo.infra.persistence.CityEntity;
import br.org.catolicasc.pug.geo.infra.read.CitiesQueries;
import br.org.catolicasc.pug.geo.infra.read.dtos.CityView;
import br.org.catolicasc.pug.geo.service.dtos.CityComplexSearchCriteria;
import br.org.catolicasc.pug.shared.infra.persistence.JpaSearchUtils;
import br.org.catolicasc.pug.shared.service.dtos.PageExecution;
import br.org.catolicasc.pug.shared.service.dtos.PageQuery;
import br.org.catolicasc.pug.shared.service.dtos.PageResult;
import br.org.catolicasc.pug.shared.utils.CollectionUtils;
import br.org.catolicasc.pug.shared.utils.StringUtils;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/** Implementation of CitiesQueries using JPA. */
@ApplicationScoped
@Transactional(Transactional.TxType.SUPPORTS)
public class CitiesQueriesImpl implements CitiesQueries {

  @Inject EntityManager entityManager;

  /** {@inheritDoc} */
  @Override
  public Optional<CityView> findOptionalById(UUID id) {
    if (id == null) {
      return Optional.empty();
    }
    var q = entityManager.createQuery("from CityEntity c where c.id = :id", CityEntity.class);
    q.setParameter("id", id);
    return q.getResultStream().findFirst().map(CityMapper::toView);
  }

  /** {@inheritDoc} */
  @Override
  public List<CityView> listAllByIds(List<UUID> ids) {
    if (CollectionUtils.isEmpty(ids)) {
      return List.of();
    }
    var q =
        entityManager.createQuery(
            "from CityEntity c where c.id in :ids order by c.name asc", CityEntity.class);
    q.setParameter("ids", ids);
    return q.getResultList().stream().map(CityMapper::toView).toList();
  }

  /** {@inheritDoc} */
  @Override
  public List<CityView> listAllCities() {
    var q = entityManager.createQuery("from CityEntity c order by c.name asc", CityEntity.class);
    return q.getResultList().stream().map(CityMapper::toView).toList();
  }

  /** {@inheritDoc} */
  @Override
  public PageResult<CityView> search(PageQuery pageQuery, CityComplexSearchCriteria criteria) {
    String name = criteria == null ? null : criteria.name();
    boolean hasNameFilter = StringUtils.isNotEmpty(name);
    String whereClause =
        hasNameFilter ? " where " + JpaSearchUtils.containsClause("city.name", "pattern") : "";

    var countQuery =
        entityManager.createQuery(
            "select count(city.id) from CityEntity city" + whereClause, Long.class);
    if (hasNameFilter) {
      JpaSearchUtils.bindContains(countQuery, "pattern", name);
    }

    long totalElements = countQuery.getSingleResult();
    PageExecution pageExecution = PageExecution.from(pageQuery, totalElements);

    var dataQuery =
        entityManager.createQuery(
            "select new br.org.catolicasc.pug.geo.infra.read.dtos.CityView("
                + "city.id, city.name, city.ibgeCode) "
                + "from CityEntity city"
                + whereClause
                + " order by city.name asc",
            CityView.class);
    if (hasNameFilter) {
      JpaSearchUtils.bindContains(dataQuery, "pattern", name);
    }

    List<CityView> content = pageExecution.apply(dataQuery).getResultList();

    return new PageResult<>(
        content,
        pageExecution.page(),
        pageExecution.size(),
        pageExecution.totalElements(),
        pageExecution.totalPages());
  }
}
