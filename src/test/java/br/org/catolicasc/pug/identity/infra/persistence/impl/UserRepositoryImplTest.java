package br.org.catolicasc.pug.identity.infra.persistence.impl;

import static org.assertj.core.api.Assertions.assertThat;

import br.org.catolicasc.pug.helpers.TestDataFactory;
import br.org.catolicasc.pug.identity.domain.User;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@QuarkusTest
class UserRepositoryImplTest {

  @Inject UserRepositoryImpl repository;
  @Inject TestDataFactory factory;

  @Test
  @Transactional
  @DisplayName("Should verify existence by CPF")
  void testExistsByCpf() {
    assertThat(repository.existsByCpf("11111111111")).isFalse();

    User user = factory.createUser();
    assertThat(repository.existsByCpf(user.getCpf().getValue())).isTrue();
  }

  @Test
  @Transactional
  @DisplayName("Should persist and find user")
  void shouldPersistAndFind() {
    User user = factory.createUser();
    assertThat(repository.existsByCpf(user.getCpf().getValue())).isTrue();
  }
}
