package br.org.catolicasc.pug.identity.infra.persistence.impl;

import br.org.catolicasc.pug.identity.domain.Account;
import br.org.catolicasc.pug.identity.domain.vos.Email;
import br.org.catolicasc.pug.shared.domain.enums.AccountType;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@QuarkusTest
class AccountRepositoryImplTest {

    @Inject
    AccountRepositoryImpl repository;
    @Inject
    UserRepositoryImpl userRepository;
    @Inject
    EntityManager em;

    @Test
    @Transactional
    @DisplayName("Should detect orphaned user IDs correctly")
    void testOrphanDetection() {
        UUID userId1 = UUID.randomUUID();
        UUID userId2 = UUID.randomUUID();

        Account acc = Account.factory(userId1, Email.factory("a@a.com"), AccountType.STUDENT, "pass");
        repository.persist(acc);
        em.flush();

        var orphans = repository.findAllOrphanUserIdsByUserIds(List.of(userId1, userId2));

        assertThat(orphans).containsExactly(userId2);
    }
}