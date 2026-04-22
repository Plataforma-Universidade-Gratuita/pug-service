package br.org.catolicasc.pug.identity.infra.persistence.impl;

import static org.assertj.core.api.Assertions.assertThat;

import br.org.catolicasc.pug.helpers.TestDataFactory;
import br.org.catolicasc.pug.helpers.builders.domain.UserBuilder;
import br.org.catolicasc.pug.identity.domain.User;
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
@DisplayName("UserRepositoryImpl Coverage")
class UserRepositoryImplTest {

  @Inject UserRepositoryImpl repository;
  @Inject TestDataFactory factory;
  @Inject EntityManager em;

  @Test
  @DisplayName("Should return false when deleting with null ID")
  void deleteNullId() {
    assertThat(repository.deleteById(null)).isFalse();
  }

  @Test
  @DisplayName("Should return 0 when deleting bulk with empty list")
  void deleteBulkEmpty() {
    assertThat(repository.deleteAllByIds(List.of())).isZero();
  }

  @Test
  @DisplayName("Should delete existing user successfully")
  void deleteSuccess() {
    User user = factory.createUser();
    assertThat(repository.deleteById(user.getId())).isTrue();

    em.clear();

    assertThat(repository.findOptionalById(user.getId())).isEmpty();
  }

  @ParameterizedTest
  @NullAndEmptySource
  @DisplayName("Should return false for null or empty CPF checks")
  void existsByCpfInvalid(String cpf) {
    assertThat(repository.existsByCpf(cpf)).isFalse();
    assertThat(repository.existsAnyByCpfs(List.of())).isFalse();
  }

  @Test
  @DisplayName("Should verify existence by CPF")
  void testExistsByCpf() {
    User user = factory.createUser();
    assertThat(repository.existsByCpf(user.getCpf().getValue())).isTrue();
    assertThat(repository.existsAnyByCpfs(List.of(user.getCpf().getValue()))).isTrue();
  }

  @ParameterizedTest
  @NullAndEmptySource
  @DisplayName("Should return empty when searching by invalid inputs")
  void findInvalid(String val) {
    assertThat(repository.findOptionalByCpf(val)).isEmpty();
    assertThat(repository.listByCpfs(null)).isEmpty();
  }

  @Test
  @DisplayName("Should persist and find user")
  void shouldPersistAndFind() {
    User user = factory.createUser();
    var found = repository.findOptionalById(user.getId());
    assertThat(found).isPresent();
    assertThat(found.get().getId()).isEqualTo(user.getId());
  }

  @Test
  @DisplayName("Should persist multiple users and list by CPFs")
  void shouldPersistAndListBulk() {
    User u1 = factory.createUser();
    User u2 = factory.createUser();
    List<User> found =
        repository.listByCpfs(List.of(u1.getCpf().getValue(), u2.getCpf().getValue()));
    assertThat(found).hasSize(2);
  }

  @Test
  @DisplayName("Should handle update with null entity")
  void updateNull() {
    repository.update(null);
  }

  @Test
  @DisplayName("Should successfully persist all users in batch")
  void persistAllSuccess() {
    User u1 = factory.createUser();
    User u2 = factory.createUser();

    // We create new instances manually to test persistAll
    List<User> users =
        List.of(
            br.org.catolicasc.pug.helpers.builders.domain.UserBuilder.aUser().build(),
            br.org.catolicasc.pug.helpers.builders.domain.UserBuilder.aUser().build());

    List<User> saved = repository.persistAll(users);
    assertThat(saved).hasSize(2);

    em.clear();
    assertThat(repository.findOptionalById(saved.get(0).getId())).isPresent();
    assertThat(repository.findOptionalById(saved.get(1).getId())).isPresent();
  }

  @Test
  @DisplayName("Should handle empty list in persistAll")
  void persistAllEmpty() {
    assertThat(repository.persistAll(List.of())).isEmpty();
  }

  @Test
  @DisplayName("Should successfully delete multiple users by IDs")
  void deleteAllSuccess() {
    User u1 = factory.createUser();
    User u2 = factory.createUser();

    long deleted = repository.deleteAllByIds(List.of(u1.getId(), u2.getId()));

    assertThat(deleted).isEqualTo(2);
    em.clear();
    assertThat(repository.findOptionalById(u1.getId())).isEmpty();
    assertThat(repository.findOptionalById(u2.getId())).isEmpty();
  }

  @Test
  @DisplayName("Should update user name successfully")
  void updateSuccess() {
    User user = factory.createUser();

    User updatedUser = user.rename("New Name Updated");

    repository.update(updatedUser);
    em.flush();
    em.clear();

    User found =
        repository
            .findOptionalById(user.getId())
            .orElseThrow(() -> new AssertionError("User not found in DB"));

    assertThat(found.getName()).isEqualTo("New Name Updated");
    assertThat(found.getAuditInfo().getUpdatedAt()).isAfter(user.getAuditInfo().getUpdatedAt());
  }

  @Test
  @DisplayName("Should do nothing when updating user with non-existent ID")
  void updateNonExisting() {
    User user = UserBuilder.aUser().build();
    User userWithId =
        User.builder()
            .id(UUID.randomUUID())
            .cpf(user.getCpf())
            .name(user.getName())
            .auditInfo(user.getAuditInfo())
            .build();

    repository.update(userWithId);
  }
}
