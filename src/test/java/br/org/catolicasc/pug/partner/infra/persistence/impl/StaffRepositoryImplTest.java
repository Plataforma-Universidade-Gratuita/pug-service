package br.org.catolicasc.pug.partner.infra.persistence.impl;

import static org.assertj.core.api.Assertions.assertThat;

import br.org.catolicasc.pug.helpers.TestDataFactory;
import br.org.catolicasc.pug.identity.domain.Account;
import br.org.catolicasc.pug.identity.domain.User;
import br.org.catolicasc.pug.partner.domain.Entity;
import br.org.catolicasc.pug.partner.domain.Staff;
import br.org.catolicasc.pug.shared.domain.enums.AccountType;
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
@DisplayName("StaffRepositoryImpl Coverage")
class StaffRepositoryImplTest {

  @Inject StaffRepositoryImpl repository;
  @Inject TestDataFactory factory;
  @Inject EntityManager em;

  private Account setup() {
    User user = factory.createUser();
    return factory.createAccount(user, AccountType.PARTNER);
  }

  @Test
  @DisplayName("Should persist and find Staff")
  void shouldPersistAndFind() {
    Account acc = setup();
    Entity ent = factory.createEntity(factory.getAnyCity());
    factory.createStaff(acc, ent);
    em.flush();

    Optional<Staff> found = repository.findOptionalByAccountId(acc.getId());
    assertThat(found).isPresent();
    assertThat(found.get().getEntityId()).isEqualTo(ent.getId());
  }

  @Test
  @DisplayName("Should check existence by Account and Entity")
  void shouldCheckExistence() {
    Account acc = setup();
    Entity ent = factory.createEntity(factory.getAnyCity());
    factory.createStaff(acc, ent);
    em.flush();

    assertThat(repository.existsByAccountIdAndEntityId(acc.getId(), ent.getId())).isTrue();
    assertThat(repository.existsByAccountIdAndEntityId(acc.getId(), UUID.randomUUID())).isFalse();
  }

  @Test
  @DisplayName("Should list all staff by Entity")
  void listByEntity() {
    Account acc = setup();
    Entity ent = factory.createEntity(factory.getAnyCity());
    factory.createStaff(acc, ent);
    em.flush();

    assertThat(repository.listAllByEntityId(ent.getId())).hasSize(1);
  }

  @Test
  @DisplayName("Should delete staff by ID or Entity ID")
  void deleteOperations() {
    Account acc = setup();
    Entity ent = factory.createEntity(factory.getAnyCity());
    factory.createStaff(acc, ent);
    em.flush();

    assertThat(repository.deleteByAccountId(acc.getId())).isTrue();

    Account acc2 = setup();
    factory.createStaff(acc2, ent);
    em.flush();

    assertThat(repository.deleteByEntityId(ent.getId())).isEqualTo(1);
  }
}
