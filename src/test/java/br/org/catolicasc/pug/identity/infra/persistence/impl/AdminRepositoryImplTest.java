package br.org.catolicasc.pug.identity.infra.persistence.impl;

import static org.assertj.core.api.Assertions.assertThat;

import br.org.catolicasc.pug.builders.AccountBuilder;
import br.org.catolicasc.pug.builders.AdminBuilder;
import br.org.catolicasc.pug.builders.UserBuilder;
import br.org.catolicasc.pug.identity.domain.Account;
import br.org.catolicasc.pug.identity.domain.Admin;
import br.org.catolicasc.pug.identity.domain.User;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@QuarkusTest
class AdminRepositoryImplTest {

  @Inject AdminRepositoryImpl repository;
  @Inject AccountRepositoryImpl accountRepository;
  @Inject UserRepositoryImpl userRepository;
  @Inject EntityManager em;

  @Test
  @Transactional
  @DisplayName("Should persist and delete admin privilege")
  void testRevokePrivilege() {
    User user = UserBuilder.aUser().withCpf("12345678900").build();
    userRepository.persist(user);

    Account acc =
        AccountBuilder.anAccount().forUser(user.getId()).withEmail("admin@test.com").build();
    accountRepository.persist(acc);

    Admin admin = AdminBuilder.anAdmin().forAccount(acc.getId()).build();
    repository.persist(admin);

    em.flush();

    boolean deleted = repository.deleteByAccountId(acc.getId());
    assertThat(deleted).isTrue();

    assertThat(repository.findOptionalByAccountId(acc.getId())).isEmpty();
  }
}
