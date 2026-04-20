package br.org.catolicasc.pug.identity.infra.persistence.impl;

import static org.assertj.core.api.Assertions.assertThat;

import br.org.catolicasc.pug.builders.AccountBuilder;
import br.org.catolicasc.pug.builders.UserBuilder;
import br.org.catolicasc.pug.identity.domain.Account;
import br.org.catolicasc.pug.identity.domain.User;
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
  @Inject UserRepositoryImpl userRepository;
  @Inject EntityManager em;

  @Test
  @Transactional
  @DisplayName("Should detect orphaned user IDs correctly")
  void testOrphanDetection() {
    User user1 = UserBuilder.aUser().withCpf("11144477735").build();
    userRepository.persist(user1);

    Account acc = AccountBuilder.anAccount().forUser(user1.getId()).build();
    repository.persist(acc);
    em.flush();

    UUID orphanId = UUID.randomUUID();
    var orphans = repository.findAllOrphanUserIdsByUserIds(List.of(user1.getId(), orphanId));

    assertThat(orphans).containsExactly(orphanId);
  }
}
