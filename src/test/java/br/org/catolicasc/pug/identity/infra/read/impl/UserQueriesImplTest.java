package br.org.catolicasc.pug.identity.infra.read.impl;

import static org.assertj.core.api.Assertions.assertThat;

import br.org.catolicasc.pug.helpers.TestDataFactory;
import br.org.catolicasc.pug.identity.domain.User;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@QuarkusTest
class UserQueriesImplTest {

  @Inject UserQueriesImpl queries;
  @Inject TestDataFactory factory;

  private User user;

  @BeforeEach
  void setup() {
    user = factory.createUser();
  }

  @Test
  @Transactional
  @DisplayName("Should retrieve user by CPF")
  void shouldFindUser() {
    var found = queries.findOptionalByCpf(user.getCpf().getValue());

    assertThat(found).isPresent();
    assertThat(found.get().name()).isEqualTo(user.getName());
  }

  @Test
  @Transactional
  @DisplayName("Should list all users")
  void shouldListAll() {
    var users = queries.listAllUsers();
    assertThat(users).anyMatch(u -> u.id().equals(user.getId()));
  }
}
