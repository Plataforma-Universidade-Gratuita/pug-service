package br.org.catolicasc.pug.identity.infra.read.impl;

import br.org.catolicasc.pug.identity.infra.read.AdminQueries;
import br.org.catolicasc.pug.identity.infra.read.dtos.AdminView;
import br.org.catolicasc.pug.shared.infra.persistence.JpaSearchUtils;
import br.org.catolicasc.pug.shared.utils.StringUtils;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Implementation of the {@link AdminQueries} interface using JPA.
 *
 * <p>This application-scoped bean executes read-only operations for administrative profiles. Given
 * the deeply nested structure of an administrator (Admin -> Account -> User), these queries rely on
 * explicitly declared JPQL {@code JOIN} paths inside constructor expressions to build out the full,
 * nested {@link AdminView} DTO without triggering N+1 select performance issues.
 */
@ApplicationScoped
@Transactional(Transactional.TxType.SUPPORTS)
public class AdminQueriesImpl implements AdminQueries {

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

  private static final String ORDER_BY_PERSON_NAME_ASC = " order by u.name asc";

  /** {@inheritDoc} */
  @Override
  public Optional<AdminView> findOptionalByEmail(String email) {
    if (StringUtils.isEmpty(email)) {
      return Optional.empty();
    }
    var q = em.createQuery(SELECT_BASE + " where acc.email = :email", AdminView.class);
    q.setParameter("email", email);
    return q.getResultStream().findFirst();
  }

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
  public List<AdminView> listByCpf(String cpf) {
    if (StringUtils.isEmpty(cpf)) {
      return List.of();
    }
    var q =
        em.createQuery(
            SELECT_BASE + " where u.cpf = :cpf" + ORDER_BY_PERSON_NAME_ASC, AdminView.class);
    q.setParameter("cpf", cpf);
    return q.getResultList();
  }

  /**
   * {@inheritDoc}
   *
   * <p>This implementation preserves the relevance order returned by the name-based search in
   * {@link UserEntity}. For each user found, the function loads the associated pairs ({@link
   * AdminEntity}, {@link AccountEntity}) and projects them into {@link AdminView} using the mapper
   * {@link AdminMapper#toView(AdminEntity, AccountEntity)}.
   */
  @Override
  public List<AdminView> searchByName(String key) {
    if (StringUtils.isEmpty(key)) {
      return List.of();
    }
    return em.createQuery(
            SELECT_BASE
                + " where "
                + JpaSearchUtils.folded("u.name")
                + " like :pattern"
                + ORDER_BY_PERSON_NAME_ASC,
            AdminView.class)
        .setParameter("pattern", JpaSearchUtils.containsPattern(key))
        .getResultList();
  }
}
