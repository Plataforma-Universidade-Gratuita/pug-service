package br.org.catolicasc.pug.partner.infra.read.impl;

import br.org.catolicasc.pug.partner.infra.read.StaffQueries;
import br.org.catolicasc.pug.partner.infra.read.dtos.StaffComplexSearchView;
import br.org.catolicasc.pug.partner.infra.read.dtos.StaffView;
import br.org.catolicasc.pug.partner.service.dtos.staff.StaffComplexSearchCriteria;
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

/** Implementation of the {@link StaffQueries} interface using JPA. */
@ApplicationScoped
@Transactional(Transactional.TxType.SUPPORTS)
public class StaffQueriesImpl implements StaffQueries {

  @Inject EntityManager em;

  private static final String SELECT_BASE =
      """
      select new br.org.catolicasc.pug.partner.infra.read.dtos.StaffView(
        new br.org.catolicasc.pug.identity.infra.read.dtos.AccountView(
          acc.id,
          acc.userId,
          acc.email,
          acc.accountType,
          acc.createdAt,
          acc.updatedAt,
          acc.active
        ),
        e.id,
        c.id
      )
      from StaffEntity s
        join AccountEntity acc on acc.id = s.accountId
        join UserEntity u on u.id = acc.userId
        join EntityEntity e on e.id = s.entityId
        join CityEntity c on c.id = e.cityId
      """;

  private static final String COMPLEX_SEARCH_SELECT =
      """
      select new br.org.catolicasc.pug.partner.infra.read.dtos.StaffComplexSearchView(
        new br.org.catolicasc.pug.identity.infra.read.dtos.AccountComplexSearchView(
          acc.id,
          u.id,
          u.name,
          acc.email,
          acc.accountType,
          acc.createdAt,
          acc.updatedAt,
          acc.active
        ),
        new br.org.catolicasc.pug.partner.infra.read.dtos.EntityComplexSearchView(
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
      )
      from StaffEntity s
        join AccountEntity acc on acc.id = s.accountId
        join UserEntity u on u.id = acc.userId
        join EntityEntity e on e.id = s.entityId
        join CityEntity c on c.id = e.cityId
      """;

  private static final String ORDER_BY_PERSON_NAME_ASC = " order by u.name asc";

  /** {@inheritDoc} */
  @Override
  public Optional<StaffView> findOptionalById(UUID accountId) {
    if (accountId == null) {
      return Optional.empty();
    }
    var query = em.createQuery(SELECT_BASE + " where s.accountId = :id", StaffView.class);
    query.setParameter("id", accountId);
    return query.getResultStream().findFirst();
  }

  /** {@inheritDoc} */
  @Override
  public List<StaffView> listAllByIds(List<UUID> ids) {
    if (CollectionUtils.isEmpty(ids)) {
      return List.of();
    }
    return em.createQuery(
            SELECT_BASE + " where s.accountId in :ids" + ORDER_BY_PERSON_NAME_ASC, StaffView.class)
        .setParameter("ids", ids)
        .getResultList();
  }

  /** {@inheritDoc} */
  @Override
  public List<StaffView> listAllStaff() {
    return em.createQuery(SELECT_BASE + ORDER_BY_PERSON_NAME_ASC, StaffView.class).getResultList();
  }

  /** {@inheritDoc} */
  @Override
  public PageResult<StaffComplexSearchView> search(
      PageQuery pageQuery, StaffComplexSearchCriteria criteria) {
    boolean activeOnly = criteria == null || criteria.activeOnly();
    String cpf = criteria == null ? null : criteria.cpf();
    OffsetDateTime dateFrom = criteria == null ? null : criteria.dateFrom();
    OffsetDateTime dateTo = criteria == null ? null : criteria.dateTo();
    String email = criteria == null ? null : criteria.email();
    List<UUID> entityIds = criteria == null ? null : criteria.entityIds();
    String name = criteria == null ? null : criteria.name();

    List<String> clauses = new ArrayList<>();
    if (activeOnly) {
      clauses.add("acc.active = true");
    }
    if (StringUtils.isNotEmpty(cpf)) {
      clauses.add(JpaSearchUtils.containsClause("u.cpf", "cpfPattern"));
    }
    if (dateFrom != null) {
      clauses.add(
          "(acc.createdAt >= :dateFrom or acc.updatedAt >= :dateFrom or u.createdAt >= :dateFrom"
              + " or u.updatedAt >= :dateFrom or e.createdAt >= :dateFrom or e.updatedAt >= :dateFrom)");
    }
    if (dateTo != null) {
      clauses.add(
          "(acc.createdAt <= :dateTo or acc.updatedAt <= :dateTo or u.createdAt <= :dateTo"
              + " or u.updatedAt <= :dateTo or e.createdAt <= :dateTo or e.updatedAt <= :dateTo)");
    }
    if (StringUtils.isNotEmpty(email)) {
      clauses.add(JpaSearchUtils.containsClause("acc.email", "emailPattern"));
    }
    if (CollectionUtils.isNotEmpty(entityIds)) {
      clauses.add("e.id in :entityIds");
    }
    if (StringUtils.isNotEmpty(name)) {
      clauses.add(JpaSearchUtils.containsClause("u.name", "namePattern"));
    }

    String whereClause = clauses.isEmpty() ? "" : " where " + String.join(" and ", clauses);

    TypedQuery<Long> countQuery =
        em.createQuery(
            "select count(s.accountId) from StaffEntity s"
                + " join AccountEntity acc on acc.id = s.accountId"
                + " join UserEntity u on u.id = acc.userId"
                + " join EntityEntity e on e.id = s.entityId"
                + whereClause,
            Long.class);
    bindSearchParameters(countQuery, cpf, dateFrom, dateTo, email, entityIds, name);
    long totalElements = countQuery.getSingleResult();
    PageExecution pageExecution = PageExecution.from(pageQuery, totalElements);

    TypedQuery<StaffComplexSearchView> dataQuery =
        em.createQuery(
            COMPLEX_SEARCH_SELECT + whereClause + ORDER_BY_PERSON_NAME_ASC,
            StaffComplexSearchView.class);
    bindSearchParameters(dataQuery, cpf, dateFrom, dateTo, email, entityIds, name);

    return new PageResult<>(
        pageExecution.apply(dataQuery).getResultList(),
        pageExecution.page(),
        pageExecution.size(),
        pageExecution.totalElements(),
        pageExecution.totalPages());
  }

  private <T> void bindSearchParameters(
      TypedQuery<T> query,
      String cpf,
      OffsetDateTime dateFrom,
      OffsetDateTime dateTo,
      String email,
      List<UUID> entityIds,
      String name) {
    if (StringUtils.isNotEmpty(cpf)) {
      JpaSearchUtils.bindContains(query, "cpfPattern", cpf);
    }
    if (dateFrom != null) {
      query.setParameter("dateFrom", dateFrom);
    }
    if (dateTo != null) {
      query.setParameter("dateTo", dateTo);
    }
    if (StringUtils.isNotEmpty(email)) {
      JpaSearchUtils.bindContains(query, "emailPattern", email);
    }
    if (CollectionUtils.isNotEmpty(entityIds)) {
      query.setParameter("entityIds", entityIds);
    }
    if (StringUtils.isNotEmpty(name)) {
      JpaSearchUtils.bindContains(query, "namePattern", name);
    }
  }
}
