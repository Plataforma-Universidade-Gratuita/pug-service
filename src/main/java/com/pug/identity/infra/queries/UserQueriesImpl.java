package com.pug.identity.infra.queries;

import com.pug.identity.infra.persistence.UserEntity;
import com.pug.identity.infra.read.UserQueries;
import com.pug.identity.infra.read.dtos.UserView;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.hibernate.search.mapper.orm.Search;
import org.hibernate.search.mapper.orm.session.SearchSession;

@ApplicationScoped
@Transactional(Transactional.TxType.SUPPORTS)
public class UserQueriesImpl implements UserQueries {

  @Inject EntityManager entityManager;

  private static UserView toView(UserEntity u) {
    if (u == null) {
      return null;
    }
    return new UserView(
        u.getId(), u.getCpf(), u.getName(), u.getEmail(), u.getAccountType(), u.getCreatedAt());
  }

  @Override
  public Optional<UserView> findOptionalById(UUID id) {
    if (id == null) {
      return Optional.empty();
    }
    var q =
        entityManager.createQuery(
            "select new com.pug.identity.infra.read.dtos.UserView("
                + "u.id, u.cpf, u.name, u.email, u.accountType, u.createdAt) "
                + "from UserEntity u where u.id = :id",
            UserView.class);
    q.setParameter("id", id);
    return q.getResultStream().findFirst();
  }

  @Override
  public Optional<UserView> findOptionalByEmail(String email) {
    if (email == null || email.isBlank()) {
      return Optional.empty();
    }
    var q =
        entityManager.createQuery(
            "select new com.pug.identity.infra.read.dtos.UserView("
                + "u.id, u.cpf, u.name, u.email, u.accountType, u.createdAt) "
                + "from UserEntity u where u.email = :email",
            UserView.class);
    q.setParameter("email", email);
    return q.getResultStream().findFirst();
  }

  @Override
  public List<UserView> listAllUsers() {
    var q =
        entityManager.createQuery(
            "select new com.pug.identity.infra.read.dtos.UserView("
                + "u.id, u.cpf, u.name, u.email, u.accountType, u.createdAt) "
                + "from UserEntity u order by u.name asc",
            UserView.class);
    return q.getResultList();
  }

  @Override
  public List<UserView> listByCpf(String cpf) {
    if (cpf == null || cpf.isBlank()) {
      return List.of();
    }
    var q =
        entityManager.createQuery(
            "select new com.pug.identity.infra.read.dtos.UserView("
                + "u.id, u.cpf, u.name, u.email, u.accountType, u.createdAt) "
                + "from UserEntity u where u.cpf = :cpf",
            UserView.class);
    q.setParameter("cpf", cpf);
    return q.getResultList();
  }

  @Override
  public List<UserView> searchByName(String key) {
    if (key == null || key.isBlank()) {
      return List.of();
    }

    String[] tokens = key.split("\\s+");
    SearchSession s = Search.session(entityManager);

    List<UserEntity> hits =
        s.search(UserEntity.class)
            .where(
                f ->
                    f.bool(
                        b -> {
                          b.should(f.wildcard().field("name_exact").matching(key + "*").boost(8f));
                          b.should(
                              f.wildcard().field("name_exact").matching("*" + key + "*").boost(6f));
                          for (String t : tokens) {
                            if (t.length() >= 3) {
                              b.should(
                                  f.wildcard()
                                      .field("name_exact")
                                      .matching("*" + t + "*")
                                      .boost(3f));
                            }
                          }
                          b.should(f.match().field("name").matching(key).fuzzy(1).boost(4f));
                          b.should(f.match().field("name_auto").matching(key).boost(2f));
                        }))
            .sort(f -> f.score().then().field("name_sort"))
            .fetchAllHits();

    if (hits.isEmpty()) {
      return List.of();
    }

    List<UserView> out = new ArrayList<>(hits.size());
    for (UserEntity u : hits) {
      out.add(toView(u));
    }
    return out;
  }
}
