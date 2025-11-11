package com.pug.identity.infra.queries;

import com.pug.identity.infra.persistence.UserEntity;
import com.pug.identity.infra.persistence.AccountEntity;
import com.pug.identity.infra.read.AccountQueries;
import com.pug.identity.infra.read.dtos.UserView;
import com.pug.identity.infra.read.dtos.AccountView;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.hibernate.search.mapper.orm.Search;
import org.hibernate.search.mapper.orm.session.SearchSession;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
@Transactional(Transactional.TxType.SUPPORTS)
public class AccountQueriesImpl implements AccountQueries {

  @Inject
  EntityManager entityManager;

  private static final String SELECT_BASE =
    """
      select new com.pug.identity.infra.read.dtos.AccountView(
        u.id,
        new com.pug.identity.infra.read.dtos.UserView(p.id, p.cpf, p.name, p.createdAt),
        u.email,
        u.accountType,
        u.createdAt
      )
      from AccountEntity u, UserEntity p
      where p.id = u.personId
      """;

  private static final String ORDER_BY_NAME_ASC = " order by p.name asc";

  private static AccountView toView(AccountEntity u, UserEntity p) {
    if (u == null || p == null) {
      return null;
    }
    return new AccountView(
            u.getId(),
            new UserView(p.getId(), p.getCpf(), p.getName(), p.getCreatedAt()),
            u.getEmail(),
            u.getAccountType(),
            u.getCreatedAt());
  }

  @Override
  public Optional<AccountView> findOptionalById(UUID id) {
    if (id == null) {
      return Optional.empty();
    }
    var q =
            entityManager.createQuery(SELECT_BASE + " and u.id = :id", AccountView.class);
    q.setParameter("id", id);
    return q.getResultStream().findFirst();
  }

  @Override
  public Optional<AccountView> findOptionalByEmail(String email) {
    if (email == null || email.isBlank()) {
      return Optional.empty();
    }
    var q =
            entityManager.createQuery(SELECT_BASE + " and u.email = :email", AccountView.class);
    q.setParameter("email", email);
    return q.getResultStream().findFirst();
  }

  @Override
  public List<AccountView> listAllUsers() {
    return entityManager
            .createQuery(SELECT_BASE + ORDER_BY_NAME_ASC, AccountView.class)
            .getResultList();
  }

  @Override
  public List<AccountView> listByCpf(String cpf) {
    if (cpf == null || cpf.isBlank()) {
      return List.of();
    }
    var q =
            entityManager.createQuery(SELECT_BASE + " and p.cpf = :cpf", AccountView.class);
    q.setParameter("cpf", cpf);
    return q.getResultList();
  }

  @Override
  public List<AccountView> searchByName(String key) {
    if (key == null || key.isBlank()) {
      return List.of();
    }

    String[] tokens = key.split("\\s+");
    SearchSession s = Search.session(entityManager);

    List<UserEntity> personHits =
      s.search(UserEntity.class)
        .where(f ->
          f.bool(b -> {
            b.should(f.wildcard().field("name_exact").matching(key + "*").boost(8f));
            b.should(f.wildcard().field("name_exact").matching("*" + key + "*").boost(6f));
            for (String t : tokens) {
              if (t.length() >= 3) {
                b.should(f.wildcard().field("name_exact").matching("*" + t + "*").boost(3f));
              }
            }
            b.should(f.match().field("name").matching(key).fuzzy(1).boost(4f));
            b.should(f.match().field("name_auto").matching(key).boost(2f));
          }))
        .sort(f -> f.score().then().field("name_sort"))
        .fetchAllHits();

    if (personHits.isEmpty()) {
      return List.of();
    }

    List<UUID> personIds = personHits.stream().map(UserEntity::getId).toList();

    List<AccountEntity> users =
      entityManager
        .createQuery("from AccountEntity u where u.personId in :ids", AccountEntity.class)
        .setParameter("ids", personIds)
        .getResultList();

    Map<UUID, List<AccountEntity>> byPerson = new HashMap<>();
    for (AccountEntity u : users) {
      byPerson.computeIfAbsent(u.getPersonId(), k -> new ArrayList<>()).add(u);
    }

    List<AccountView> out = new ArrayList<>();
    for (UserEntity p : personHits) {
      List<AccountEntity> us = byPerson.get(p.getId());
      if (us == null) {
        continue;
      }
      for (AccountEntity u : us) {
        out.add(toView(u, p));
      }
    }
    return out;
  }
}
