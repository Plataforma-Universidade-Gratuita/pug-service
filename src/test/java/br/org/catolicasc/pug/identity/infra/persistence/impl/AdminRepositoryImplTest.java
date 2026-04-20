package br.org.catolicasc.pug.identity.infra.persistence.impl;

import static org.assertj.core.api.Assertions.assertThat;

import br.org.catolicasc.pug.identity.domain.Admin;
import br.org.catolicasc.pug.shared.domain.enums.Campi;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@QuarkusTest
class AdminRepositoryImplTest {

  @Inject AdminRepositoryImpl repository;
  @Inject EntityManager em;

  @Test
  @Transactional
  @DisplayName("Should persist and delete admin privilege")
  void testRevokePrivilege() {
    UUID accountId = UUID.randomUUID();
    Admin admin = Admin.factory(accountId, Campi.JOINVILLE);

    repository.persist(admin);
    em.flush();

    boolean deleted = repository.deleteByAccountId(accountId);
    assertThat(deleted).isTrue();

    assertThat(repository.findOptionalByAccountId(accountId)).isEmpty();
  }
}
