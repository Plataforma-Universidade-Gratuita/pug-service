package br.org.catolicasc.pug.identity.infra.persistence.impl;

import static org.assertj.core.api.Assertions.assertThat;

import br.org.catolicasc.pug.identity.domain.User;
import br.org.catolicasc.pug.identity.domain.vos.Cpf;
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
    boolean exists = repository.existsByCpf("00000000000");
    assertThat(exists).isTrue();

    boolean notExists = repository.existsByCpf("11111111111");
    assertThat(notExists).isFalse();
  }

  @Test
  @Transactional
  @DisplayName("Should persist and find user")
  void shouldPersistAndFind() {
    User user = User.factory(Cpf.factory("11144477735"), "Test User");
    repository.persist(user);

    assertThat(repository.existsByCpf("11144477735")).isTrue();
  }
}
