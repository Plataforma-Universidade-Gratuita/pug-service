package br.org.catolicasc.pug.identity.infra.read.impl;

import static br.org.catolicasc.pug.identity.infra.UserMapper.toView;

import br.org.catolicasc.pug.identity.infra.persistence.UserEntity;
import br.org.catolicasc.pug.identity.infra.read.UserQueries;
import br.org.catolicasc.pug.identity.infra.read.dtos.UserView;
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
 * Implementation of the {@link UserQueries} interface using JPA and Hibernate Search.
 *
 * <p>This application-scoped bean handles the execution of read-only queries. It uses JPQL
 * constructor expressions to directly project database rows into lightweight {@link UserView} DTOs,
 * completely bypassing the overhead of instantiating managed JPA entities. Full-text searches are
 * delegated to the underlying Elasticsearch/OpenSearch indices via {@link HibernateSearchUtils}.
 */
@ApplicationScoped
@Transactional(Transactional.TxType.SUPPORTS)
public class UserQueriesImpl implements UserQueries {

  @Inject EntityManager em;

  /** {@inheritDoc} */
  @Override
  public Optional<UserView> findOptionalByCpf(String cpf) {
    if (StringUtils.isEmpty(cpf)) {
      return Optional.empty();
    }
    var q =
        em.createQuery(
            "select new com.pug.identity.infra.read.dtos.UserView("
                + "p.id, p.cpf, p.name, p.createdAt, p.updatedAt) "
                + "from UserEntity p where p.cpf = :cpf",
            UserView.class);
    q.setParameter("cpf", cpf);
    return q.getResultStream().findFirst();
  }

  /** {@inheritDoc} */
  @Override
  public Optional<UserView> findOptionalById(UUID id) {
    if (id == null) {
      return Optional.empty();
    }
    var q =
        em.createQuery(
            "select new com.pug.identity.infra.read.dtos.UserView("
                + "p.id, p.cpf, p.name, p.createdAt, p.updatedAt) "
                + "from UserEntity p where p.id = :id",
            UserView.class);
    q.setParameter("id", id);
    return q.getResultStream().findFirst();
  }

  /** {@inheritDoc} */
  @Override
  public List<UserView> listAllUsers() {
    var q =
        em.createQuery(
            "select new com.pug.identity.infra.read.dtos.UserView("
                + "p.id, p.cpf, p.name, p.createdAt, p.updatedAt) "
                + "from UserEntity p order by p.name asc",
            UserView.class);
    return q.getResultList();
  }

  /** {@inheritDoc} */
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
