package br.org.catolicasc.pug.identity.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import br.org.catolicasc.pug.helpers.BaseResourceTest;
import br.org.catolicasc.pug.identity.domain.Account;
import br.org.catolicasc.pug.identity.domain.User;
import br.org.catolicasc.pug.identity.infra.persistence.AccountEntity;
import br.org.catolicasc.pug.identity.infra.persistence.RefreshTokenEntity;
import br.org.catolicasc.pug.identity.infra.persistence.impl.RefreshTokenRepositoryImpl;
import br.org.catolicasc.pug.identity.presenter.dtos.auth.LogoutRequest;
import br.org.catolicasc.pug.shared.domain.enums.AccountType;
import com.github.f4b6a3.uuid.UuidCreator;
import io.quarkus.security.identity.SecurityIdentity;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import java.time.OffsetDateTime;
import java.util.UUID;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@QuarkusTest
@DisplayName("AuthServiceImpl Persistence Integration Tests")
class AuthServiceImplPersistenceTest extends BaseResourceTest {

  @Inject AuthServiceImpl authService;

  @Inject RefreshTokenRepositoryImpl refreshTokenRepository;

  @InjectMock SecurityIdentity securityIdentity;

  @Test
  @DisplayName("logout should remove the matching refresh token row")
  void logoutRemovesRefreshTokenRow() throws Exception {
    Account account = createAccount(AccountType.ADMIN);
    String rawToken = "logout-token";

    persistRefreshToken(account.getId(), rawToken);

    authService.logout(new LogoutRequest(rawToken));

    assertThat(refreshTokenRepository.findByTokenHash(AuthServiceImpl.sha256(rawToken))).isEmpty();
  }

  @Test
  @DisplayName("logoutAll should remove only the current account refresh tokens")
  void logoutAllRemovesOnlyCurrentAccountTokens() throws Exception {
    Account currentAccount = createAccount(AccountType.ADMIN);
    Account otherAccount = createAccount(AccountType.PARTNER);

    persistRefreshToken(currentAccount.getId(), "current-token-1");
    persistRefreshToken(currentAccount.getId(), "current-token-2");
    persistRefreshToken(otherAccount.getId(), "other-token");

    JsonWebToken jwt = mock(JsonWebToken.class);
    when(securityIdentity.isAnonymous()).thenReturn(false);
    when(securityIdentity.getPrincipal()).thenReturn(jwt);
    when(jwt.getClaim("accountId")).thenReturn(currentAccount.getId().toString());

    authService.logoutAll();

    assertThat(refreshTokenRepository.existsActiveByAccountId(currentAccount.getId())).isFalse();
    assertThat(refreshTokenRepository.findByTokenHash(AuthServiceImpl.sha256("other-token")))
        .isPresent();
  }

  private Account createAccount(AccountType type) throws Exception {
    Account[] account = new Account[1];
    doInTransaction(
        () -> {
          User user = factory.createUser();
          account[0] = factory.createAccount(user, type);
        });
    return account[0];
  }

  private void persistRefreshToken(UUID accountId, String rawToken) throws Exception {
    doInTransaction(
        () -> {
          AccountEntity accountRef = em.getReference(AccountEntity.class, accountId);
          OffsetDateTime now = OffsetDateTime.now();

          RefreshTokenEntity entity =
              RefreshTokenEntity.builder()
                  .id(UuidCreator.getTimeOrderedEpoch())
                  .account(accountRef)
                  .tokenHash(AuthServiceImpl.sha256(rawToken))
                  .expiresAt(now.plusHours(1))
                  .createdAt(now)
                  .updatedAt(now)
                  .build();

          em.persist(entity);
        });
  }
}
