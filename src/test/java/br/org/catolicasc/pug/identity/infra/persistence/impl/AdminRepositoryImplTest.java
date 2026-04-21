package br.org.catolicasc.pug.identity.infra.persistence.impl;

import static org.assertj.core.api.Assertions.assertThat;

import br.org.catolicasc.pug.helpers.TestDataFactory;
import br.org.catolicasc.pug.identity.domain.Account;
import br.org.catolicasc.pug.identity.domain.User;
import br.org.catolicasc.pug.shared.domain.enums.AccountType;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@QuarkusTest
class AdminRepositoryImplTest {

  @Inject AdminRepositoryImpl repository;
  @Inject TestDataFactory factory;
  @Inject EntityManager em;

  private Account account;

  @BeforeEach
  @Transactional
  void setup() {
    User user = factory.createUser();
    account = factory.createAccount(user, AccountType.ADMIN);
    factory.createAdmin(account);
    em.flush();
  }

  @Test
  @Transactional
  @DisplayName("Should persist and delete admin privilege")
  void testRevokePrivilege() {
    boolean deleted = repository.deleteByAccountId(account.getId());
    assertThat(deleted).isTrue();

    assertThat(repository.findOptionalByAccountId(account.getId())).isEmpty();
  }
}
