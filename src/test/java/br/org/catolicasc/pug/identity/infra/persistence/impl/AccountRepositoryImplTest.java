package br.org.catolicasc.pug.identity.infra.persistence.impl;

import static org.assertj.core.api.Assertions.assertThat;

import br.org.catolicasc.pug.helpers.TestDataFactory;
import br.org.catolicasc.pug.identity.domain.User;
import br.org.catolicasc.pug.shared.domain.enums.AccountType;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@QuarkusTest
class AccountRepositoryImplTest {

  @Inject AccountRepositoryImpl repository;
  @Inject TestDataFactory factory;
  @Inject EntityManager em;

  @Test
  @Transactional
  @DisplayName("Should detect orphaned user IDs correctly")
  void testOrphanDetection() {
    User user1 = factory.createUser();
    factory.createAccount(user1, AccountType.STUDENT);
    em.flush();

    UUID orphanId = UUID.randomUUID();
    var orphans = repository.findAllOrphanUserIdsByUserIds(List.of(user1.getId(), orphanId));

    assertThat(orphans).containsExactly(orphanId);
  }
}
