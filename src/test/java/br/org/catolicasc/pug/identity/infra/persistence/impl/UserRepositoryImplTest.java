package br.org.catolicasc.pug.identity.infra.persistence.impl;

import static org.assertj.core.api.Assertions.assertThat;

import br.org.catolicasc.pug.builders.UserBuilder;
import br.org.catolicasc.pug.identity.domain.User;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@QuarkusTest
class UserRepositoryImplTest {

  @Inject UserRepositoryImpl repository;

  @Test
  @Transactional
  @DisplayName("Should verify existence by CPF")
  void testExistsByCpf() {
    assertThat(repository.existsByCpf("00000000000")).isTrue();
    assertThat(repository.existsByCpf("11111111111")).isFalse();
  }

  @Test
  @Transactional
  @DisplayName("Should persist and find user")
  void shouldPersistAndFind() {
    User user = UserBuilder.aUser().withCpf("98741369062").build();
    repository.persist(user);

    assertThat(repository.existsByCpf("98741369062")).isTrue();
  }
}
