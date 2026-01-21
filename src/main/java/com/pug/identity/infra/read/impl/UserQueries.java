package com.pug.identity.infra.read.impl;

import com.pug.identity.infra.persistence.UserEntity;
import com.pug.identity.infra.read.IUserQueries;
import com.pug.identity.infra.read.dtos.UserView;
import com.pug.shared.infra.search.HibernateSearchUtils;
import com.pug.shared.utils.StringUtils;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Implementation of UserQueries using JPA and Hibernate Search.
 */
@ApplicationScoped
@Transactional(Transactional.TxType.SUPPORTS)
public class UserQueries implements IUserQueries {

  @Inject
  EntityManager em;

  /**
   * Converts a UserEntity to a UserView.
   *
   * @param e the UserEntity
   * @return the UserView
   */
  private static UserView toView(UserEntity e) {
    if (e == null) {
      return null;
    }
    return new UserView(e.getId(), e.getCpf(), e.getName(), e.getCreatedAt());
  }

  @Override
  public Optional<UserView> findOptionalById(UUID id) {
    if (id == null) {
      return Optional.empty();
    }
    var q =
            em.createQuery(
                    "select new com.pug.identity.infra.read.dtos.UserView("
                            + "p.id, p.cpf, p.name, p.createdAt) "
                            + "from UserEntity p where p.id = :id",
                    UserView.class);
    q.setParameter("id", id);
    return q.getResultStream().findFirst();
  }

  @Override
  public Optional<UserView> findOptionalByCpf(String cpf) {
    if (StringUtils.isEmpty(cpf)) {
      return Optional.empty();
    }
    var q =
            em.createQuery(
                    "select new com.pug.identity.infra.read.dtos.UserView("
                            + "p.id, p.cpf, p.name, p.createdAt) "
                            + "from UserEntity p where p.cpf = :cpf",
                    UserView.class);
    q.setParameter("cpf", cpf);
    return q.getResultStream().findFirst();
  }

  @Override
  public List<UserView> listAllUsers() {
    var q =
            em.createQuery(
                    "select new com.pug.identity.infra.read.dtos.UserView("
                            + "p.id, p.cpf, p.name, p.createdAt) "
                            + "from UserEntity p order by p.name asc",
                    UserView.class);
    return q.getResultList();
  }

  @Override
  public List<UserView> searchByName(String key) {
    List<UserEntity> hits = HibernateSearchUtils.searchByName(em, UserEntity.class, key);

    if (hits.isEmpty()) {
      return List.of();
    }

    List<UserView> out = new ArrayList<>(hits.size());
    for (UserEntity p : hits) {
      out.add(toView(p));
    }
    return out;
  }
}