package br.org.catolicasc.pug.partner.infra.persistence.impl;

import static org.assertj.core.api.Assertions.assertThat;

import br.org.catolicasc.pug.helpers.TestDataFactory;
import br.org.catolicasc.pug.identity.domain.Account;
import br.org.catolicasc.pug.identity.domain.User;
import br.org.catolicasc.pug.partner.domain.Entity;
import br.org.catolicasc.pug.partner.domain.Staff;
import br.org.catolicasc.pug.shared.domain.enums.AccountType;
import com.github.f4b6a3.uuid.UuidCreator;
import io.quarkus.test.TestTransaction;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@QuarkusTest
@TestTransaction
@DisplayName("StaffRepositoryImpl Coverage")
class StaffRepositoryImplTest {

  @Inject StaffRepositoryImpl repository;
  @Inject TestDataFactory factory;
  @Inject EntityManager em;

  private Account setupAccount() {
    User user = factory.createUser();
    return factory.createAccount(user, AccountType.PARTNER);
  }

  @Test
  @DisplayName("Should persist and find staff")
  void shouldPersistAndFind() {
    Account account = setupAccount();
    Entity entity = factory.createEntity(factory.getAnyCity());
    factory.createStaff(account, entity);
    em.flush();

    Optional<Staff> found = repository.findOptionalByAccountId(account.getId());
    assertThat(found).isPresent();
    assertThat(found.get().getEntityId()).isEqualTo(entity.getId());
  }

  @Test
  @DisplayName("Should check existence by account and entity")
  void shouldCheckExistence() {
    Account account = setupAccount();
    Entity entity = factory.createEntity(factory.getAnyCity());
    factory.createStaff(account, entity);
    em.flush();

    assertThat(repository.existsByAccountIdAndEntityId(account.getId(), entity.getId())).isTrue();
    assertThat(
            repository.existsByAccountIdAndEntityId(
                account.getId(), UuidCreator.getTimeOrderedEpoch()))
        .isFalse();
  }

  @Test
  @DisplayName("Should list all staff by entity")
  void listByEntity() {
    Account account = setupAccount();
    Entity entity = factory.createEntity(factory.getAnyCity());
    factory.createStaff(account, entity);
    em.flush();

    assertThat(repository.listAllByEntityId(entity.getId())).hasSize(1);
  }

  @Test
  @DisplayName(
      "Should return false when no other staff uses the informed email in the target entity")
  void shouldCheckExistingEmailInEntity() {
    Entity entity = factory.createEntity(factory.getAnyCity());
    Account account = setupAccount();
    factory.createStaff(account, entity);
    em.flush();

    assertThat(
            repository.existsAnotherByEntityIdAndEmail(
                entity.getId(), account.getEmail().getValue(), account.getId()))
        .isFalse();
  }

  @Test
  @DisplayName("Should update staff entity assignment")
  void shouldUpdate() {
    Account account = setupAccount();
    Entity originalEntity = factory.createEntity(factory.getAnyCity());
    Entity targetEntity = factory.createEntity(factory.getAnyCity());
    factory.createStaff(account, originalEntity);
    em.flush();

    Staff updated = Staff.factory(account.getId(), targetEntity.getId());
    repository.update(updated);
    em.flush();
    em.clear();

    Optional<Staff> found = repository.findOptionalByAccountId(account.getId());
    assertThat(found).isPresent();
    assertThat(found.get().getEntityId()).isEqualTo(targetEntity.getId());
  }

  @Test
  @DisplayName("Should delete staff by account ID or entity ID")
  void deleteOperations() {
    Account account = setupAccount();
    Entity entity = factory.createEntity(factory.getAnyCity());
    factory.createStaff(account, entity);
    em.flush();

    assertThat(repository.deleteByAccountId(account.getId())).isTrue();

    Account secondAccount = setupAccount();
    factory.createStaff(secondAccount, entity);
    em.flush();

    assertThat(repository.deleteByEntityId(entity.getId())).isEqualTo(1);
  }
}
