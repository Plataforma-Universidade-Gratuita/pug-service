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

  @Override
  public Optional<AdminView> findOptionalById(UUID userId) {
    var q =
        em.createQuery(
            """
                            select new com.pug.identity.infra.read.dtos.AdminView(
                              new com.pug.identity.infra.read.dtos.UserView(
                              u.id, u.cpf, u.name, u.email, u.accountType, u.createdAt
                              ), a.grantedAt
                            )
                            from AdminEntity a
                              join UserEntity u on u.id = a.userId
                            where a.userId = :id
                            """,
            AdminView.class);
    q.setParameter("id", userId);
    return q.getResultList().stream().findFirst();
  }

  @Override
  public List<AdminView> listAllAdmins() {
    return em.createQuery(
            """
                            select new com.pug.identity.infra.read.dtos.AdminView(
                              new com.pug.identity.infra.read.dtos.UserView(
                              u.id, u.cpf, u.name, u.email, u.accountType, u.createdAt
                              ), a.grantedAt
                            )
                            from AdminEntity a
                              join UserEntity u on u.id = a.userId
                            order by u.name
                            """,
            AdminView.class)
        .getResultList();
  }
}
