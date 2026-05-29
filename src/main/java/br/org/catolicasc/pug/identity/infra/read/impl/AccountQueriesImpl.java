package br.org.catolicasc.pug.identity.infra.read.impl;

import br.org.catolicasc.pug.identity.infra.read.AccountQueries;
import br.org.catolicasc.pug.identity.infra.read.dtos.AccountView;
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
 * Implementation of the {@link AccountQueries} interface using JPA.
 *
 * <p>This application-scoped bean handles read-only queries for accounts. Because an account
 * inherently belongs to a user, the JPQL queries utilize constructor expressions that implicitly
 * join the {@link AccountEntity} and {@link UserEntity} tables to assemble a fully populated {@link
 * AccountView} in a single database round-trip.
 */
@ApplicationScoped
@Transactional(Transactional.TxType.SUPPORTS)
public class AccountQueriesImpl implements AccountQueries {

  @Inject EntityManager entityManager;

  private static final String SELECT_BASE =
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

  private static final String ORDER_BY_NAME_ASC = " order by u.name asc";

  /** {@inheritDoc} */
  @Override
  public Optional<AccountView> findOptionalByEmail(String email) {
    if (StringUtils.isEmpty(email)) {
      return Optional.empty();
    }
    var q = entityManager.createQuery(SELECT_BASE + " and a.email = :email", AccountView.class);
    q.setParameter("email", email);
    return q.getResultStream().findFirst();
  }

  /** {@inheritDoc} */
  @Override
  public Optional<AccountView> findOptionalById(UUID id) {
    if (id == null) {
      return Optional.empty();
    }
    var q = entityManager.createQuery(SELECT_BASE + " and a.id = :id", AccountView.class);
    q.setParameter("id", id);
    return q.getResultStream().findFirst();
  }

  /** {@inheritDoc} */
  @Override
  public List<AccountView> listAllAccounts() {
    return entityManager
        .createQuery(SELECT_BASE + ORDER_BY_NAME_ASC, AccountView.class)
        .getResultList();
  }

  /** {@inheritDoc} */
  @Override
  public List<AccountView> listByCpf(String cpf) {
    if (StringUtils.isEmpty(cpf)) {
      return List.of();
    }
    var q = entityManager.createQuery(SELECT_BASE + " and u.cpf = :cpf", AccountView.class);
    q.setParameter("cpf", cpf);
    return q.getResultList();
  }

  /** {@inheritDoc} */
  @Override
  public List<AccountView> searchByName(String key) {
    if (StringUtils.isEmpty(key)) {
      return List.of();
    }
    var q =
        entityManager.createQuery(
            SELECT_BASE
                + " and "
                + JpaSearchUtils.folded("u.name")
                + " like :pattern"
                + ORDER_BY_NAME_ASC,
            AccountView.class);
    q.setParameter("pattern", JpaSearchUtils.containsPattern(key));
    return q.getResultList();
  }
}
