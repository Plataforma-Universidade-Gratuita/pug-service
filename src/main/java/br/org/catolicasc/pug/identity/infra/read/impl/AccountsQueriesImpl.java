package br.org.catolicasc.pug.identity.infra.read.impl;

import br.org.catolicasc.pug.identity.infra.read.AccountsQueries;
import br.org.catolicasc.pug.identity.infra.read.dtos.AccountComplexSearchView;
import br.org.catolicasc.pug.identity.infra.read.dtos.AccountView;
import br.org.catolicasc.pug.identity.service.dtos.accounts.AccountComplexSearchCriteria;
import br.org.catolicasc.pug.shared.domain.enums.AccountType;
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
 * Implementation of the {@link AccountsQueries} interface using JPA.
 *
 * <p>This application-scoped bean handles read-only queries for accounts. Because an account
 * inherently belongs to a user, the JPQL queries utilize constructor expressions that join account
 * and user data to assemble fully populated read-model projections in a single database round-trip.
 */
@ApplicationScoped
@Transactional(Transactional.TxType.SUPPORTS)
public class AccountsQueriesImpl implements AccountsQueries {

  @Inject EntityManager entityManager;

  private static final String ACCOUNT_VIEW_SELECT =
      """
      select new br.org.catolicasc.pug.identity.infra.read.dtos.AccountView(
        a.id,
        u.id,
        a.email,
        a.accountType,
        a.createdAt,
        a.updatedAt,
        a.active
      )
      from AccountEntity a, UserEntity u
      where u.id = a.userId
      """;

  private static final String ACCOUNT_COMPLEX_SEARCH_SELECT =
      """
      select new br.org.catolicasc.pug.identity.infra.read.dtos.AccountComplexSearchView(
        a.id,
        u.id,
        u.name,
        a.email,
        a.accountType,
        a.createdAt,
        a.updatedAt,
        a.active
      )
      from AccountEntity a, UserEntity u
      where u.id = a.userId
      """;

  private static final String ORDER_BY_NAME_ASC = " order by u.name asc";

  /** {@inheritDoc} */
  @Override
  public Optional<AccountView> findOptionalById(UUID id) {
    if (id == null) {
      return Optional.empty();
    }
    var query =
        entityManager.createQuery(ACCOUNT_VIEW_SELECT + " and a.id = :id", AccountView.class);
    query.setParameter("id", id);
    return query.getResultStream().findFirst();
  }

  /** {@inheritDoc} */
  @Override
  public List<AccountView> listAllByIds(List<UUID> ids) {
    if (CollectionUtils.isEmpty(ids)) {
      return List.of();
    }
    return entityManager
        .createQuery(
            ACCOUNT_VIEW_SELECT + " and a.id in :ids" + ORDER_BY_NAME_ASC, AccountView.class)
        .setParameter("ids", ids)
        .getResultList();
  }

  /** {@inheritDoc} */
  @Override
  public List<AccountView> listAllAccounts() {
    return entityManager
        .createQuery(ACCOUNT_VIEW_SELECT + ORDER_BY_NAME_ASC, AccountView.class)
        .getResultList();
  }

  /** {@inheritDoc} */
  @Override
  public PageResult<AccountComplexSearchView> search(
      PageQuery pageQuery, AccountComplexSearchCriteria criteria) {
    boolean activeOnly = criteria == null || criteria.activeOnly();
    String cpf = criteria == null ? null : criteria.cpf();
    OffsetDateTime dateFrom = criteria == null ? null : criteria.dateFrom();
    OffsetDateTime dateTo = criteria == null ? null : criteria.dateTo();
    String email = criteria == null ? null : criteria.email();
    List<AccountType> accountTypes = criteria == null ? null : criteria.accountTypes();
    String name = criteria == null ? null : criteria.name();

    List<String> clauses = new ArrayList<>();
    if (activeOnly) {
      clauses.add("a.active = true");
    }
    if (CollectionUtils.isNotEmpty(accountTypes)) {
      clauses.add("a.accountType in :accountTypes");
    }
    if (StringUtils.isNotEmpty(cpf)) {
      clauses.add(JpaSearchUtils.containsClause("u.cpf", "cpfPattern"));
    }
    if (dateFrom != null) {
      clauses.add(
          "(a.createdAt >= :dateFrom or a.updatedAt >= :dateFrom or u.createdAt >= :dateFrom or"
              + " u.updatedAt >= :dateFrom)");
    }
    if (dateTo != null) {
      clauses.add(
          "(a.createdAt <= :dateTo or a.updatedAt <= :dateTo or u.createdAt <= :dateTo or"
              + " u.updatedAt <= :dateTo)");
    }
    if (StringUtils.isNotEmpty(email)) {
      clauses.add(JpaSearchUtils.containsClause("a.email", "emailPattern"));
    }
    if (StringUtils.isNotEmpty(name)) {
      clauses.add(JpaSearchUtils.containsClause("u.name", "namePattern"));
    }

    String whereClause = clauses.isEmpty() ? "" : " and " + String.join(" and ", clauses);

    var countQuery =
        entityManager.createQuery(
            "select count(a.id) from AccountEntity a, UserEntity u where u.id = a.userId"
                + whereClause,
            Long.class);
    bindSearchParameters(countQuery, cpf, dateFrom, dateTo, email, accountTypes, name);
    long totalElements = countQuery.getSingleResult();
    PageExecution pageExecution = PageExecution.from(pageQuery, totalElements);

    var dataQuery =
        entityManager.createQuery(
            ACCOUNT_COMPLEX_SEARCH_SELECT + whereClause + ORDER_BY_NAME_ASC,
            AccountComplexSearchView.class);
    bindSearchParameters(dataQuery, cpf, dateFrom, dateTo, email, accountTypes, name);

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
      List<br.org.catolicasc.pug.shared.domain.enums.AccountType> accountTypes,
      String name) {
    if (CollectionUtils.isNotEmpty(accountTypes)) {
      query.setParameter("accountTypes", accountTypes);
    }
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
