/*
 * Copyright (c) 2026 Mateus Fernandes and Plataforma Universidade Gratuita.
 * All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized use,
 * copying, modification, distribution, or deployment is prohibited.
 */

package br.org.catolicasc.pug.identity.infra.read.impl;

import br.org.catolicasc.pug.identity.infra.read.AdminsQueries;
import br.org.catolicasc.pug.identity.infra.read.dtos.AdminComplexSearchView;
import br.org.catolicasc.pug.identity.infra.read.dtos.AdminView;
import br.org.catolicasc.pug.identity.service.dtos.admins.AdminComplexSearchCriteria;
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
 * Implementation of the {@link AdminsQueries} interface using JPA.
 *
 * <p>This application-scoped bean executes read-only operations for administrative profiles. Given
 * the nested structure of an administrator (Admin -> Account -> User), these queries rely on JPQL
 * constructor expressions that join the required tables and project only the fields needed by the
 * presenter contract.
 */
@ApplicationScoped
@Transactional(Transactional.TxType.SUPPORTS)
public class AdminsQueriesImpl implements AdminsQueries {

  @Inject EntityManager em;

  private static final String SELECT_BASE =
      """
                  select new br.org.catolicasc.pug.identity.infra.read.dtos.AdminView(
                    new br.org.catolicasc.pug.identity.infra.read.dtos.AccountView(
                      acc.id,
                      u.id,
                      acc.email,
                      acc.accountType,
                      acc.createdAt,
                      acc.updatedAt,
                      acc.active
                    ),
                    a.grantedAt,
                    a.campus
                  )
                  from AdminEntity a
                    join AccountEntity acc on acc.id = a.accountId
                    join UserEntity u on u.id = acc.userId
                  """;

  private static final String COMPLEX_SEARCH_SELECT =
      """
                  select new br.org.catolicasc.pug.identity.infra.read.dtos.AdminComplexSearchView(
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
                    a.campus,
                    a.grantedAt
                  )
                  from AdminEntity a
                    join AccountEntity acc on acc.id = a.accountId
                    join UserEntity u on u.id = acc.userId
                  """;

  private static final String ORDER_BY_PERSON_NAME_ASC = " order by u.name asc";

  /** {@inheritDoc} */
  @Override
  public Optional<AdminView> findOptionalById(UUID accountId) {
    if (accountId == null) {
      return Optional.empty();
    }
    var q = em.createQuery(SELECT_BASE + " where a.accountId = :id", AdminView.class);
    q.setParameter("id", accountId);
    return q.getResultStream().findFirst();
  }

  /** {@inheritDoc} */
  @Override
  public List<AdminView> listAllAdmins() {
    return em.createQuery(SELECT_BASE + ORDER_BY_PERSON_NAME_ASC, AdminView.class).getResultList();
  }

  /** {@inheritDoc} */
  @Override
  public List<AdminView> listAllByIds(List<UUID> ids) {
    if (CollectionUtils.isEmpty(ids)) {
      return List.of();
    }
    return em.createQuery(
            SELECT_BASE + " where a.accountId in :ids" + ORDER_BY_PERSON_NAME_ASC, AdminView.class)
        .setParameter("ids", ids)
        .getResultList();
  }

  /** {@inheritDoc} */
  @Override
  public PageResult<AdminComplexSearchView> search(
      PageQuery pageQuery, AdminComplexSearchCriteria criteria) {
    boolean activeOnly = criteria == null || criteria.activeOnly();
    String cpf = criteria == null ? null : criteria.cpf();
    OffsetDateTime dateFrom = criteria == null ? null : criteria.dateFrom();

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
              + " or u.updatedAt >= :dateFrom or a.grantedAt >= :dateFrom)");
    }
    OffsetDateTime dateTo = criteria == null ? null : criteria.dateTo();
    if (dateTo != null) {
      clauses.add(
          "(acc.createdAt <= :dateTo or acc.updatedAt <= :dateTo or u.createdAt <= :dateTo or"
              + " u.updatedAt <= :dateTo or a.grantedAt <= :dateTo)");
    }
    String email = criteria == null ? null : criteria.email();
    if (StringUtils.isNotEmpty(email)) {
      clauses.add(JpaSearchUtils.containsClause("acc.email", "emailPattern"));
    }
    String name = criteria == null ? null : criteria.name();
    if (StringUtils.isNotEmpty(name)) {
      clauses.add(JpaSearchUtils.containsClause("u.name", "namePattern"));
    }

    String whereClause = clauses.isEmpty() ? "" : " where " + String.join(" and ", clauses);

    TypedQuery<Long> countQuery =
        em.createQuery(
            "select count(a.accountId) from AdminEntity a"
                + " join AccountEntity acc on acc.id = a.accountId"
                + " join UserEntity u on u.id = acc.userId"
                + whereClause,
            Long.class);
    bindSearchParameters(countQuery, cpf, dateFrom, dateTo, email, name);
    long totalElements = countQuery.getSingleResult();
    PageExecution pageExecution = PageExecution.from(pageQuery, totalElements);

    TypedQuery<AdminComplexSearchView> dataQuery =
        em.createQuery(
            COMPLEX_SEARCH_SELECT + whereClause + ORDER_BY_PERSON_NAME_ASC,
            AdminComplexSearchView.class);
    bindSearchParameters(dataQuery, cpf, dateFrom, dateTo, email, name);

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
    if (StringUtils.isNotEmpty(name)) {
      JpaSearchUtils.bindContains(query, "namePattern", name);
    }
  }
}
