package br.org.catolicasc.pug.identity.infra.persistence.impl;

import static org.assertj.core.api.Assertions.assertThat;

import br.org.catolicasc.pug.helpers.TestDataFactory;
import br.org.catolicasc.pug.identity.domain.Account;
import br.org.catolicasc.pug.identity.domain.User;
import br.org.catolicasc.pug.identity.infra.persistence.AccountEntity;
import br.org.catolicasc.pug.identity.infra.persistence.RefreshTokenEntity;
import br.org.catolicasc.pug.shared.domain.enums.AccountType;
import com.github.f4b6a3.uuid.UuidCreator;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.UserTransaction;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@QuarkusTest
@DisplayName("RefreshTokenRepositoryImpl Integration Tests")
class RefreshTokenRepositoryImplTest {

  @Inject RefreshTokenRepositoryImpl repo;
  @Inject TestDataFactory factory;
  @Inject UserTransaction utx;
  @Inject EntityManager em;

  private void createToken(UUID accountId, String hash, OffsetDateTime expiresAt) throws Exception {
    utx.begin();
    AccountEntity accountRef = em.getReference(AccountEntity.class, accountId);
    OffsetDateTime now = OffsetDateTime.now();
    RefreshTokenEntity entity =
        RefreshTokenEntity.builder()
            .id(UuidCreator.getTimeOrderedEpoch())
            .account(accountRef)
            .tokenHash(hash)
            .expiresAt(expiresAt)
            .createdAt(now)
            .updatedAt(now)
            .build();
    repo.persist(entity);
    em.flush();
    utx.commit();
  }

  private Account setupAccount() throws Exception {
    Account[] acc = new Account[1];
    utx.begin();
    User user = factory.createUser();
    acc[0] = factory.createAccount(user, AccountType.STUDENT);
    em.flush();
    utx.commit();
    return acc[0];
  }

  @Test
  @DisplayName("findByTokenHash should return entity when exists")
  void findByTokenHashFound() throws Exception {
    Account account = setupAccount();
    String hash = "hash-find-" + UUID.randomUUID();
    createToken(account.getId(), hash, OffsetDateTime.now().plusDays(7));

    Optional<RefreshTokenEntity> result = repo.findByTokenHash(hash);
    assertThat(result).isPresent();
    assertThat(result.get().getTokenHash()).isEqualTo(hash);
  }

  @Test
  @DisplayName("findByTokenHash should return empty when not found")
  void findByTokenHashNotFound() {
    Optional<RefreshTokenEntity> result = repo.findByTokenHash("nonexistent-hash");
    assertThat(result).isEmpty();
  }

  @Test
  @DisplayName("deleteByTokenHash should remove the token")
  void deleteByTokenHash() throws Exception {
    Account account = setupAccount();
    String hash = "hash-del-" + UUID.randomUUID();
    createToken(account.getId(), hash, OffsetDateTime.now().plusDays(7));

    utx.begin();
    long deleted = repo.deleteByTokenHash(hash);
    utx.commit();

    assertThat(deleted).isEqualTo(1);
    assertThat(repo.findByTokenHash(hash)).isEmpty();
  }

  @Test
  @DisplayName("deleteByAccountId should remove all tokens for account")
  void deleteByAccountId() throws Exception {
    Account account = setupAccount();
    createToken(
        account.getId(), "hash-all-1-" + UUID.randomUUID(), OffsetDateTime.now().plusDays(7));
    createToken(
        account.getId(), "hash-all-2-" + UUID.randomUUID(), OffsetDateTime.now().plusDays(7));

    utx.begin();
    long deleted = repo.deleteByAccountId(account.getId());
    utx.commit();

    assertThat(deleted).isGreaterThanOrEqualTo(2);
  }

  @Test
  @DisplayName("deleteExpired should remove only expired tokens")
  void deleteExpired() throws Exception {
    Account account = setupAccount();
    String expiredHash = "hash-exp-" + UUID.randomUUID();
    String validHash = "hash-val-" + UUID.randomUUID();
    createToken(account.getId(), expiredHash, OffsetDateTime.now().minusHours(1));
    createToken(account.getId(), validHash, OffsetDateTime.now().plusDays(7));

    utx.begin();
    long deleted = repo.deleteExpired();
    utx.commit();

    assertThat(deleted).isGreaterThanOrEqualTo(1);
    assertThat(repo.findByTokenHash(validHash)).isPresent();
    assertThat(repo.findByTokenHash(expiredHash)).isEmpty();
  }
}
