package br.org.catolicasc.pug.partner.infra.read.impl;

import br.org.catolicasc.pug.partner.infra.persistence.EntityEntity;
import br.org.catolicasc.pug.partner.infra.read.EntityQueries;
import br.org.catolicasc.pug.partner.infra.read.dtos.EntityView;
import br.org.catolicasc.pug.shared.infra.search.HibernateSearchUtils;
import br.org.catolicasc.pug.shared.utils.StringUtils;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Implementation of the {@link EntityQueries} interface using JPA and Hibernate Search.
 *
 * <p>This application-scoped bean handles the execution of read-only queries for partner
 * organizations. It uses JPQL constructor expressions to implicitly join the partner data with its
 * underlying geographic location (City) in a single database round-trip.
 */
@ApplicationScoped
@Transactional(Transactional.TxType.SUPPORTS)
public class EntityQueriesImpl implements EntityQueries {

  @Inject EntityManager em;

  private static final String SELECT_BASE =
      """
                  select new br.org.catolicasc.pug.partner.infra.read.dtos.EntityView(
                    e.id,
                    e.cnpj,
                    e.name,
                    e.address,
                    e.cityId,
                    e.createdAt,
                    e.updatedAt
                  )
                  from EntityEntity e
                  """;

  private static final String ORDER_BY_NAME_ASC = " order by e.name asc";

  /** {@inheritDoc} */
  @Override
  public Optional<EntityView> findOptionalByCnpj(String cnpj) {
    if (StringUtils.isEmpty(cnpj)) {
      return Optional.empty();
    }

    var q =
        em.createQuery(SELECT_BASE + " where e.cnpj = :cnpj", EntityView.class)
            .setParameter("cnpj", cnpj);
    return q.getResultStream().findFirst();
  }

  /** {@inheritDoc} */
  @Override
  public Optional<EntityView> findOptionalById(UUID id) {
    if (id == null) {
      return Optional.empty();
    }

    var q =
        em.createQuery(SELECT_BASE + " where e.id = :id", EntityView.class).setParameter("id", id);
    return q.getResultStream().findFirst();
  }

  /** {@inheritDoc} */
  @Override
  public List<EntityView> listAllByCityId(UUID cityId) {
    if (cityId == null) {
      return List.of();
    }

    var q =
        em.createQuery(
                SELECT_BASE + " where e.cityId = :cityId" + ORDER_BY_NAME_ASC, EntityView.class)
            .setParameter("cityId", cityId);
    return q.getResultList();
  }

  /** {@inheritDoc} */
  @Override
  public List<UUID> listAllCityIds() {
    return em.createQuery("select distinct e.cityId from EntityEntity e", UUID.class)
        .getResultList();
  }

  /** {@inheritDoc} */
  @Override
  public List<EntityView> listAllEntities() {
    return em.createQuery(SELECT_BASE + ORDER_BY_NAME_ASC, EntityView.class).getResultList();
  }

  /**
   * {@inheritDoc}
   *
   * <p>To achieve a full-text search against the entity's name, this implementation first resolves
   * the matching entities via the search index and then projects them directly into flattened
   * {@link EntityView} DTOs. City details are not nested here; only the {@code cityId} is exposed
   * so that callers can resolve additional geographic information on demand.
   */
  @Override
  public List<EntityView> searchByName(String key) {
    List<EntityEntity> hits = HibernateSearchUtils.searchByName(em, EntityEntity.class, key);
    if (hits.isEmpty()) {
      return List.of();
    }

    List<EntityView> out = new ArrayList<>(hits.size());
    for (EntityEntity e : hits) {
      out.add(
          new EntityView(
              e.getId(),
              e.getCnpj(),
              e.getName(),
              e.getAddress(),
              e.getCityId(),
              e.getCreatedAt(),
              e.getUpdatedAt()));
    }
    return out;
  }
}
