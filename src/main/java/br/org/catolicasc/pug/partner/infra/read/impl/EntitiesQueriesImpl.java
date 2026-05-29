package br.org.catolicasc.pug.partner.infra.read.impl;

import br.org.catolicasc.pug.partner.infra.read.EntitiesQueries;
import br.org.catolicasc.pug.partner.infra.read.dtos.EntityComplexSearchView;
import br.org.catolicasc.pug.partner.infra.read.dtos.EntityView;
import br.org.catolicasc.pug.partner.service.dtos.EntityComplexSearchCriteria;
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
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Implementation of the {@link EntitiesQueries} interface using JPA.
 *
 * <p>This application-scoped bean handles the execution of read-only queries for partner
 * organizations and resolves linked city data directly in the query layer.
 */
@ApplicationScoped
@Transactional(Transactional.TxType.SUPPORTS)
public class EntitiesQueriesImpl implements EntitiesQueries {

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

  private static final String COMPLEX_SEARCH_SELECT =
      """
      select new br.org.catolicasc.pug.partner.infra.read.dtos.EntityComplexSearchView(
        e.id,
        e.cnpj,
        e.name,
        e.address,
        c.id,
        c.name,
        c.ibgeCode,
        e.createdAt,
        e.updatedAt
      )
      from EntityEntity e
        join CityEntity c on c.id = e.cityId
      """;

  private static final String ORDER_BY_NAME_ASC = " order by e.name asc";

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
  public List<EntityView> listAllByIds(List<UUID> ids) {
    if (CollectionUtils.isEmpty(ids)) {
      return List.of();
    }

    return em.createQuery(SELECT_BASE + " where e.id in :ids" + ORDER_BY_NAME_ASC, EntityView.class)
        .setParameter("ids", ids)
        .getResultList();
  }

  /** {@inheritDoc} */
  @Override
  public List<EntityView> listAllEntities() {
    return em.createQuery(SELECT_BASE + ORDER_BY_NAME_ASC, EntityView.class).getResultList();
  }

  /** {@inheritDoc} */
  @Override
  public PageResult<EntityComplexSearchView> search(
      PageQuery pageQuery, EntityComplexSearchCriteria criteria) {
    String address = criteria == null ? null : criteria.address();
    List<UUID> cityIds = criteria == null ? null : criteria.cityIds();
    String cnpj = criteria == null ? null : criteria.cnpj();
    OffsetDateTime dateFrom = criteria == null ? null : criteria.dateFrom();
    OffsetDateTime dateTo = criteria == null ? null : criteria.dateTo();
    String name = criteria == null ? null : criteria.name();

    List<String> clauses = new ArrayList<>();
    if (StringUtils.isNotEmpty(address)) {
      clauses.add(JpaSearchUtils.containsClause("e.address", "addressPattern"));
    }
    if (CollectionUtils.isNotEmpty(cityIds)) {
      clauses.add("e.cityId in :cityIds");
    }
    if (StringUtils.isNotEmpty(cnpj)) {
      clauses.add(JpaSearchUtils.containsClause("e.cnpj", "cnpjPattern"));
    }
    if (dateFrom != null) {
      clauses.add("(e.createdAt >= :dateFrom or e.updatedAt >= :dateFrom)");
    }
    if (dateTo != null) {
      clauses.add("(e.createdAt <= :dateTo or e.updatedAt <= :dateTo)");
    }
    if (StringUtils.isNotEmpty(name)) {
      clauses.add(JpaSearchUtils.containsClause("e.name", "namePattern"));
    }

    String whereClause = clauses.isEmpty() ? "" : " where " + String.join(" and ", clauses);

    TypedQuery<Long> countQuery =
        em.createQuery("select count(e.id) from EntityEntity e" + whereClause, Long.class);
    bindSearchParameters(countQuery, address, cityIds, cnpj, dateFrom, dateTo, name);
    long totalElements = countQuery.getSingleResult();
    PageExecution pageExecution = PageExecution.from(pageQuery, totalElements);

    TypedQuery<EntityComplexSearchView> dataQuery =
        em.createQuery(
            COMPLEX_SEARCH_SELECT + whereClause + ORDER_BY_NAME_ASC, EntityComplexSearchView.class);
    bindSearchParameters(dataQuery, address, cityIds, cnpj, dateFrom, dateTo, name);

    return new PageResult<>(
        pageExecution.apply(dataQuery).getResultList(),
        pageExecution.page(),
        pageExecution.size(),
        pageExecution.totalElements(),
        pageExecution.totalPages());
  }

  private <T> void bindSearchParameters(
      TypedQuery<T> query,
      String address,
      List<UUID> cityIds,
      String cnpj,
      OffsetDateTime dateFrom,
      OffsetDateTime dateTo,
      String name) {
    if (StringUtils.isNotEmpty(address)) {
      JpaSearchUtils.bindContains(query, "addressPattern", address);
    }
    if (CollectionUtils.isNotEmpty(cityIds)) {
      query.setParameter("cityIds", cityIds);
    }
    if (StringUtils.isNotEmpty(cnpj)) {
      JpaSearchUtils.bindContains(query, "cnpjPattern", cnpj);
    }
    if (dateFrom != null) {
      query.setParameter("dateFrom", dateFrom);
    }
    if (dateTo != null) {
      query.setParameter("dateTo", dateTo);
    }
    if (StringUtils.isNotEmpty(name)) {
      JpaSearchUtils.bindContains(query, "namePattern", name);
    }
  }
}
