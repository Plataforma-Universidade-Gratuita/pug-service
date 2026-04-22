package br.org.catolicasc.pug.identity.infra.persistence.impl;

import static org.assertj.core.api.Assertions.assertThat;

import br.org.catolicasc.pug.helpers.TestDataFactory;
import br.org.catolicasc.pug.helpers.builders.domain.AccountBuilder;
import br.org.catolicasc.pug.identity.domain.Account;
import br.org.catolicasc.pug.identity.domain.User;
import br.org.catolicasc.pug.shared.domain.enums.AccountType;
import io.quarkus.test.TestTransaction;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

@QuarkusTest
@TestTransaction
@DisplayName("AccountRepositoryImpl Coverage")
class AccountRepositoryImplTest {

  @Inject AccountRepositoryImpl repository;
  @Inject TestDataFactory factory;
  @Inject EntityManager em;

  @Test
  @DisplayName("Should return zero when user ID is null")
  void countNull() {
    assertThat(repository.countAllAccountsByUserId(null)).isZero();
  }

  @Test
  @DisplayName("Should count accounts correctly")
  void countSuccess() {
    User user = factory.createUser();
    factory.createAccount(user, AccountType.STUDENT);
    factory.createAccount(user, AccountType.PARTNER);
    assertThat(repository.countAllAccountsByUserId(user.getId())).isEqualTo(2);
  }

  @Test
  @DisplayName("Should return false when deleting with null ID")
  void deleteNullId() {
    assertThat(repository.deleteById(null)).isFalse();
  }

  @Test
  @DisplayName("Should delete account successfully")
  void deleteSuccess() {
    User user = factory.createUser();
    Account acc = factory.createAccount(user, AccountType.STUDENT);
    assertThat(repository.deleteById(acc.getId())).isTrue();
    em.clear();
    assertThat(repository.findOptionalById(acc.getId())).isEmpty();
  }

  @Test
  @DisplayName("Should return zero when bulk deleting empty list")
  void deleteAllEmpty() {
    assertThat(repository.deleteAllByIds(List.of())).isZero();
  }

  @Test
  @DisplayName("Should bulk delete multiple accounts successfully")
  void deleteAllSuccess() {
    User user = factory.createUser();
    Account a1 = factory.createAccount(user, AccountType.STUDENT);
    Account a2 = factory.createAccount(user, AccountType.PARTNER);

    long deleted = repository.deleteAllByIds(List.of(a1.getId(), a2.getId()));

    assertThat(deleted).isEqualTo(2);
    em.clear();
    assertThat(repository.findOptionalById(a1.getId())).isEmpty();
    assertThat(repository.findOptionalById(a2.getId())).isEmpty();
  }

  @ParameterizedTest
  @NullAndEmptySource
  @DisplayName("Should return false for invalid email checks")
  void existsInvalid(List<String> emails) {
    assertThat(repository.existsAnyByEmails(emails)).isFalse();
  }

  @Test
  @DisplayName("Should detect existence in batch")
  void existsAnyByEmailsSuccess() {
    User user = factory.createUser();
    Account acc1 = factory.createAccount(user, AccountType.STUDENT);
    Account acc2 = factory.createAccount(user, AccountType.PARTNER);

    assertThat(
            repository.existsAnyByEmails(
                List.of(acc1.getEmail().getValue(), acc2.getEmail().getValue())))
        .isTrue();
    assertThat(repository.existsAnyByEmails(List.of("nonexistent@pug.com"))).isFalse();
  }

  @Test
  @DisplayName("Should find accounts and detect orphans")
  void findFlow() {
    User user = factory.createUser();
    Account acc = factory.createAccount(user, AccountType.STUDENT);
    em.flush();

    assertThat(repository.existsByEmail(acc.getEmail().getValue())).isTrue();
    assertThat(repository.findUserIdsByIds(List.of(acc.getId()))).containsExactly(user.getId());

    UUID orphanId = UUID.randomUUID();
    assertThat(repository.findAllOrphanUserIdsByUserIds(List.of(user.getId(), orphanId)))
        .containsExactly(orphanId);
  }

  @ParameterizedTest
  @NullAndEmptySource
  @DisplayName("Should return empty when searching by invalid email")
  void findOptionalByEmailInvalid(String email) {
    assertThat(repository.findOptionalByEmail(email)).isEmpty();
  }

  @Test
  @DisplayName("Should persist and update successfully")
  void persistUpdateFlow() {
    User user = factory.createUser();
    Account acc =
        AccountBuilder.anAccount().forUser(user.getId()).withEmail("updated@pug.com").build();

    Account saved = repository.persist(acc);
    assertThat(saved.getId()).isNotNull();

    repository.update(saved);
    em.clear();

    assertThat(repository.findOptionalById(saved.getId()).get().getEmail().getValue())
        .isEqualTo("updated@pug.com");
  }

  @Test
  @DisplayName("Should persist all accounts in batch")
  void persistAllSuccess() {
    User user = factory.createUser();
    Account a1 = AccountBuilder.anAccount().forUser(user.getId()).build();
    Account a2 = AccountBuilder.anAccount().forUser(user.getId()).build();

    List<Account> saved = repository.persistAll(List.of(a1, a2));

    assertThat(saved).hasSize(2);
    em.clear();
    assertThat(repository.findOptionalById(saved.get(0).getId())).isPresent();
    assertThat(repository.findOptionalById(saved.get(1).getId())).isPresent();
  }
}
