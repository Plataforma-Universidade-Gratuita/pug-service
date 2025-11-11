package com.pug.identity.infra.queries;

import com.pug.identity.infra.read.AdminQueries;
import com.pug.identity.infra.read.dtos.AdminView;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/** Implementation of AdminQueries using JPA EntityManager. */
@ApplicationScoped
public class AdminQueriesImpl implements AdminQueries {

  @Inject EntityManager em;

  private static final String SELECT_BASE =
      """
                  select new com.pug.identity.infra.read.dtos.AdminView(
                    new com.pug.identity.infra.read.dtos.AccountView(
                      acc.id,
                      new com.pug.identity.infra.read.dtos.UserView(u.id, u.cpf, u.name, u.createdAt),
                      acc.email,
                      acc.accountType,
                      acc.createdAt
                    ),
                    a.grantedAt
                  )
                  from AdminEntity a
                    join AccountEntity acc on acc.id = a.accountId
                    join UserEntity u on u.id = acc.userId
                  """;

  private static final String ORDER_BY_PERSON_NAME_ASC = " order by u.name asc";

  @Override
  public Optional<AdminView> findOptionalById(UUID accountId) {
    if (accountId == null) {
      return Optional.empty();
    }
    var q = em.createQuery(SELECT_BASE + " where a.accountId = :id", AdminView.class);
    q.setParameter("id", accountId);
    return q.getResultStream().findFirst();
  }

  @Override
  public List<AdminView> listAllAdmins() {
    return em.createQuery(SELECT_BASE + ORDER_BY_PERSON_NAME_ASC, AdminView.class).getResultList();
  }
}
