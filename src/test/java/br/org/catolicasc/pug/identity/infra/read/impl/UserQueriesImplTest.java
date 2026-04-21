package br.org.catolicasc.pug.identity.infra.read.impl;

import static org.assertj.core.api.Assertions.assertThat;

import br.org.catolicasc.pug.helpers.BaseSearchTest;
import br.org.catolicasc.pug.helpers.TestDataFactory;
import br.org.catolicasc.pug.identity.domain.User;
import br.org.catolicasc.pug.identity.infra.persistence.UserEntity;
import br.org.catolicasc.pug.identity.infra.read.dtos.UserView;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

@QuarkusTest
@DisplayName("UserQueriesImpl Coverage")
class UserQueriesImplTest extends BaseSearchTest {

  @Inject UserQueriesImpl queries;
  @Inject TestDataFactory factory;

  @ParameterizedTest
  @NullAndEmptySource
  @Transactional
  @DisplayName("Should return empty when CPF is null or empty")
  void findOptionalByCpfInvalid(String cpf) {
    assertThat(queries.findOptionalByCpf(cpf)).isEmpty();
  }

  @Test
  @Transactional
  @DisplayName("Should find user by CPF successfully")
  void findOptionalByCpfSuccess() {
    User user = factory.createUser();
    Optional<UserView> found = queries.findOptionalByCpf(user.getCpf().getValue());
    assertThat(found).isPresent();
    assertThat(found.get().id()).isEqualTo(user.getId());
  }

  @Test
  @Transactional
  @DisplayName("Should find user by ID successfully")
  void findOptionalByIdSuccess() {
    User user = factory.createUser();
    Optional<UserView> found = queries.findOptionalById(user.getId());
    assertThat(found).isPresent();
    assertThat(found.get().id()).isEqualTo(user.getId());
  }

  @Test
  @Transactional
  @DisplayName("Should list all users")
  void list() {
    factory.createUser();
    assertThat(queries.listAllUsers()).isNotEmpty();
  }

  @Test
  @DisplayName("Should search users by name successfully")
  void searchByNameSuccess() throws Exception {
    User[] user = new User[1];
    runInTransaction(
        () -> {
          user[0] = factory.createUser();
        });

    syncIndex(UserEntity.class);

    String searchKey = user[0].getName().split(" ")[0];
    List<UserView> found = queries.searchByName(searchKey);

    assertThat(found).anyMatch(v -> v.id().equals(user[0].getId()));
  }
}
