package br.org.catolicasc.pug.identity.infra.persistence.impl;

import static org.assertj.core.api.Assertions.assertThat;

import br.org.catolicasc.pug.helpers.TestDataFactory;
import br.org.catolicasc.pug.helpers.builders.AdminBuilder;
import br.org.catolicasc.pug.identity.domain.Account;
import br.org.catolicasc.pug.identity.domain.Admin;
import br.org.catolicasc.pug.identity.domain.User;
import br.org.catolicasc.pug.shared.domain.enums.AccountType;
import br.org.catolicasc.pug.shared.domain.enums.Campi;
import io.quarkus.test.TestTransaction;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@QuarkusTest
@TestTransaction
@DisplayName("AdminRepositoryImpl Coverage")
class AdminRepositoryImplTest {

  @Inject AdminRepositoryImpl repository;
  @Inject TestDataFactory factory;
  @Inject EntityManager em;

  private Account setup() {
    User user = factory.createUser();
    return factory.createAccount(user, AccountType.ADMIN);
  }

  @Test
  @DisplayName("Should handle delete scenarios")
  void deleteTests() {
    Account acc = setup();
    assertThat(repository.deleteByAccountId(null)).isFalse();
    assertThat(repository.deleteByAccountId(UUID.randomUUID())).isFalse();

    factory.createAdmin(acc);
    em.flush();
    assertThat(repository.deleteByAccountId(acc.getId())).isTrue();
  }

  @Test
  @DisplayName("Should find admin by account ID")
  void findTests() {
    Account acc = setup();
    assertThat(repository.findOptionalByAccountId(UUID.randomUUID())).isEmpty();

    Admin admin = factory.createAdmin(acc);
    em.flush();
    Optional<Admin> found = repository.findOptionalByAccountId(acc.getId());
    assertThat(found).isPresent();
    assertThat(found.get().getAccountId()).isEqualTo(admin.getAccountId());
  }

  @Test
  @DisplayName("Should persist and update admin")
  void persistUpdateTests() {
    Account acc = setup();
    Admin admin = AdminBuilder.anAdmin().forAccount(acc.getId()).atCampus(Campi.JOINVILLE).build();

    Admin saved = repository.persist(admin);
    assertThat(saved.getCampus()).isEqualTo(Campi.JOINVILLE);

    Admin updated =
        AdminBuilder.anAdmin().forAccount(acc.getId()).atCampus(Campi.JARAGUA_DO_SUL).build();
    repository.update(updated);

    assertThat(repository.findOptionalByAccountId(acc.getId()).get().getCampus())
        .isEqualTo(Campi.JARAGUA_DO_SUL);
  }
}
