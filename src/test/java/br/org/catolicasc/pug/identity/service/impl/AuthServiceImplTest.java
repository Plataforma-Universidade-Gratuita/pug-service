package br.org.catolicasc.pug.identity.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import br.org.catolicasc.pug.identity.domain.Account;
import br.org.catolicasc.pug.identity.domain.enums.IdentityErrorCodes;
import br.org.catolicasc.pug.identity.domain.vos.Email;
import br.org.catolicasc.pug.identity.infra.persistence.AccountEntity;
import br.org.catolicasc.pug.identity.infra.persistence.RefreshTokenEntity;
import br.org.catolicasc.pug.identity.infra.persistence.impl.RefreshTokenRepositoryImpl;
import br.org.catolicasc.pug.identity.presenter.dtos.auth.CredentialsRequest;
import br.org.catolicasc.pug.identity.presenter.dtos.auth.LoginRequest;
import br.org.catolicasc.pug.identity.presenter.dtos.auth.LogoutRequest;
import br.org.catolicasc.pug.identity.presenter.dtos.auth.RefreshRequest;
import br.org.catolicasc.pug.identity.presenter.dtos.auth.TokenResponse;
import br.org.catolicasc.pug.identity.service.AccountsService;
import br.org.catolicasc.pug.identity.service.PasswordService;
import br.org.catolicasc.pug.identity.service.dtos.accounts.AccountUpdateCommand;
import br.org.catolicasc.pug.shared.domain.enums.AccountType;
import br.org.catolicasc.pug.shared.exceptions.BusinessRuleException;
import br.org.catolicasc.pug.shared.exceptions.ResourceNotFoundException;
import com.github.f4b6a3.uuid.UuidCreator;
import io.quarkus.security.identity.SecurityIdentity;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.ws.rs.NotAuthorizedException;
import java.security.Principal;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@QuarkusTest
@DisplayName("AuthServiceImpl Coverage")
class AuthServiceImplTest {

  @Inject AuthServiceImpl authService;

  @InjectMock AccountsService accountService;
  @InjectMock PasswordService passwordService;
  @InjectMock SecurityIdentity securityIdentity;
  @InjectMock RefreshTokenRepositoryImpl refreshTokenRepository;

  @Test
  @DisplayName("Should login successfully and return token")
  void loginSuccess() {
    String email = "test@pug.com";
    String raw = "Password1!";
    String hash = "hashed";

    Account acc =
        Account.factory(
            UuidCreator.getTimeOrderedEpoch(),
            Email.factory(email),
            AccountType.FORMER_STUDENT,
            hash);
    acc = acc.toBuilder().active(true).build();

    when(accountService.getByEmail(email)).thenReturn(acc);
    when(passwordService.isConfigured(hash)).thenReturn(true);
    when(passwordService.verify(hash, raw)).thenReturn(true);

    TokenResponse response = authService.login(new LoginRequest(email, raw));

    assertThat(response.token()).isNotBlank();
    assertThat(response.refreshToken()).isNotBlank();
    assertThat(response.accountId()).isEqualTo(acc.getId());
  }

  @Test
  @DisplayName("Should login successfully when account has no wired password yet")
  void loginSuccessWithoutConfiguredPassword() {
    String email = "test@pug.com";

    Account acc =
        Account.factory(
                UuidCreator.getTimeOrderedEpoch(),
                Email.factory(email),
                AccountType.FORMER_STUDENT,
                null)
            .toBuilder()
            .active(true)
            .build();

    when(accountService.getByEmail(email)).thenReturn(acc);
    when(passwordService.isConfigured(null)).thenReturn(false);

    TokenResponse response = authService.login(new LoginRequest(email, "AnyPassword1!"));

    assertThat(response.token()).isNotBlank();
    assertThat(response.refreshToken()).isNotBlank();
    verify(passwordService, never()).verify(null, "AnyPassword1!");
  }

  @Test
  @DisplayName("Should throw NotAuthorized when account not found")
  void loginAccountNotFound() {
    when(accountService.getByEmail("unknown@pug.com"))
        .thenThrow(
            new ResourceNotFoundException(
                br.org.catolicasc.pug.identity.domain.enums.IdentityErrorCodes.ACCOUNT_NOT_FOUND));

    assertThrows(
        NotAuthorizedException.class,
        () -> authService.login(new LoginRequest("unknown@pug.com", "pass")));
  }

  @Test
  @DisplayName("Should throw NotAuthorized when password mismatch")
  void loginWrongPassword() {
    String email = "test@pug.com";
    String hash = "hashed";
    Account acc =
        Account.factory(
            UuidCreator.getTimeOrderedEpoch(),
            Email.factory(email),
            AccountType.FORMER_STUDENT,
            hash);

    when(accountService.getByEmail(email)).thenReturn(acc);
    when(passwordService.isConfigured(hash)).thenReturn(true);
    when(passwordService.verify(hash, "wrong")).thenReturn(false);

    assertThrows(
        NotAuthorizedException.class, () -> authService.login(new LoginRequest(email, "wrong")));
  }

  @Test
  @DisplayName("Should throw NotAuthorized when account is inactive")
  void loginInactiveAccount() {
    String email = "test@pug.com";
    Account acc =
        Account.factory(
                UuidCreator.getTimeOrderedEpoch(),
                Email.factory(email),
                AccountType.FORMER_STUDENT,
                "hash")
            .toBuilder()
            .active(false)
            .build();

    when(accountService.getByEmail(email)).thenReturn(acc);

    assertThrows(
        NotAuthorizedException.class, () -> authService.login(new LoginRequest(email, "pass")));
  }

  @Test
  @DisplayName("Should refresh token successfully")
  void refreshSuccess() {
    String rawToken = "valid-refresh-token";
    String hash = AuthServiceImpl.sha256(rawToken);
    UUID accId = UuidCreator.getTimeOrderedEpoch();

    AccountEntity accountEntity = new AccountEntity();
    accountEntity.setId(accId);
    accountEntity.setActive(true);

    UUID sessionId = UuidCreator.getTimeOrderedEpoch();
    RefreshTokenEntity entity = mock(RefreshTokenEntity.class);
    when(entity.getId()).thenReturn(sessionId);
    when(entity.getExpiresAt()).thenReturn(OffsetDateTime.now().plusHours(1));
    when(entity.getAccount()).thenReturn(accountEntity);

    when(refreshTokenRepository.findByTokenHash(hash)).thenReturn(Optional.of(entity));

    Account account =
        Account.factory(accId, Email.factory("r@pug.com"), AccountType.FORMER_STUDENT, "hash");
    when(accountService.getById(accId)).thenReturn(account);

    TokenResponse response = authService.refresh(new RefreshRequest(rawToken));

    assertThat(response.token()).isNotBlank();
    assertThat(response.refreshToken()).isEqualTo(rawToken);
  }

  @Test
  @DisplayName("Should throw NotAuthorized when refresh token not found")
  void refreshTokenNotFound() {
    String rawToken = "unknown-token";
    String hash = AuthServiceImpl.sha256(rawToken);
    when(refreshTokenRepository.findByTokenHash(hash)).thenReturn(Optional.empty());

    assertThrows(
        NotAuthorizedException.class, () -> authService.refresh(new RefreshRequest(rawToken)));
  }

  @Test
  @DisplayName("Should throw NotAuthorized when refresh token is expired")
  void refreshTokenExpired() {
    String rawToken = "expired-token";
    String hash = AuthServiceImpl.sha256(rawToken);

    RefreshTokenEntity entity = mock(RefreshTokenEntity.class);
    when(entity.getExpiresAt()).thenReturn(OffsetDateTime.now().minusHours(1));

    when(refreshTokenRepository.findByTokenHash(hash)).thenReturn(Optional.of(entity));

    assertThrows(
        NotAuthorizedException.class, () -> authService.refresh(new RefreshRequest(rawToken)));
  }

  @Test
  @DisplayName("Should throw NotAuthorized when refreshing with inactive account")
  void refreshInactiveAccount() {
    String rawToken = "inactive-token";
    String hash = AuthServiceImpl.sha256(rawToken);

    AccountEntity accountEntity = new AccountEntity();
    accountEntity.setId(UuidCreator.getTimeOrderedEpoch());
    accountEntity.setActive(false);

    RefreshTokenEntity entity = mock(RefreshTokenEntity.class);
    when(entity.getExpiresAt()).thenReturn(OffsetDateTime.now().plusHours(1));
    when(entity.getAccount()).thenReturn(accountEntity);

    when(refreshTokenRepository.findByTokenHash(hash)).thenReturn(Optional.of(entity));

    assertThrows(
        NotAuthorizedException.class, () -> authService.refresh(new RefreshRequest(rawToken)));
  }

  @Test
  @DisplayName("Should logout successfully when token exists")
  void logoutSuccess() {
    String rawToken = "some-token";
    String hash = AuthServiceImpl.sha256(rawToken);
    when(refreshTokenRepository.deleteByTokenHash(hash)).thenReturn(1L);

    assertDoesNotThrow(() -> authService.logout(new LogoutRequest(rawToken)));
    verify(refreshTokenRepository).deleteByTokenHash(hash);
  }

  @Test
  @DisplayName("Should handle logout gracefully when token does not exist")
  void logoutUnknownToken() {
    String rawToken = "nonexistent-token";
    String hash = AuthServiceImpl.sha256(rawToken);
    when(refreshTokenRepository.deleteByTokenHash(hash)).thenReturn(0L);

    assertDoesNotThrow(() -> authService.logout(new LogoutRequest(rawToken)));
  }

  @Test
  @DisplayName("Should revoke all refresh tokens for current account")
  void logoutAllSuccess() {
    UUID accId = UuidCreator.getTimeOrderedEpoch();

    JsonWebToken jwtMock = mock(JsonWebToken.class);
    when(securityIdentity.isAnonymous()).thenReturn(false);
    when(securityIdentity.getPrincipal()).thenReturn(jwtMock);
    when(jwtMock.getClaim("accountId")).thenReturn(accId.toString());
    when(refreshTokenRepository.deleteByAccountId(accId)).thenReturn(3L);

    assertDoesNotThrow(() -> authService.logoutAll());
    verify(refreshTokenRepository).deleteByAccountId(accId);
  }

  @Test
  @DisplayName("Should wire credentials successfully")
  void wireCredentialsSuccess() {
    String email = "test@pug.com";
    String rawPassword = "StrongPass1!";
    String hashedPassword = "hashed-password";
    Account account =
        Account.factory(
            UuidCreator.getTimeOrderedEpoch(),
            Email.factory(email),
            AccountType.FORMER_STUDENT,
            null);

    when(accountService.getByEmail(email)).thenReturn(account);
    when(passwordService.hash(rawPassword)).thenReturn(hashedPassword);

    authService.wireCredentials(new CredentialsRequest(email, rawPassword));

    verify(passwordService).validateStrength(rawPassword);
    verify(passwordService).hash(rawPassword);
    verify(accountService)
        .update(account.getId(), new AccountUpdateCommand(null, hashedPassword, null, null));
  }

  @Test
  @DisplayName("Should reject weak passwords when wiring credentials")
  void wireCredentialsWeakPassword() {
    String email = "test@pug.com";
    String rawPassword = "weakpass";
    Account account =
        Account.factory(
            UuidCreator.getTimeOrderedEpoch(),
            Email.factory(email),
            AccountType.FORMER_STUDENT,
            null);

    when(accountService.getByEmail(email)).thenReturn(account);
    BusinessRuleException exception = new BusinessRuleException(IdentityErrorCodes.WEAK_PASSWORD);
    org.mockito.Mockito.doThrow(exception).when(passwordService).validateStrength(rawPassword);

    assertThrows(
        BusinessRuleException.class,
        () -> authService.wireCredentials(new CredentialsRequest(email, rawPassword)));
  }

  @Test
  @DisplayName("Should throw NotAuthorized when identity is anonymous")
  void getCurrentAccountTypeAnonymous() {
    when(securityIdentity.isAnonymous()).thenReturn(true);
    assertThrows(NotAuthorizedException.class, () -> authService.getCurrentAccountType());
  }

  @Test
  @DisplayName("Should throw NotAuthorized when principal is not JWT")
  void getCurrentAccountTypeNonJwtPrincipal() {
    when(securityIdentity.isAnonymous()).thenReturn(false);
    when(securityIdentity.getPrincipal()).thenReturn(mock(Principal.class));
    assertThrows(NotAuthorizedException.class, () -> authService.getCurrentAccountType());
  }

  @Test
  @DisplayName("Should throw NotAuthorized when groups claim is empty")
  void getCurrentAccountTypeEmptyGroups() {
    JsonWebToken jwtMock = mock(JsonWebToken.class);
    when(securityIdentity.isAnonymous()).thenReturn(false);
    when(securityIdentity.getPrincipal()).thenReturn(jwtMock);
    when(jwtMock.getGroups()).thenReturn(Collections.emptySet());

    assertThrows(NotAuthorizedException.class, () -> authService.getCurrentAccountType());
  }

  @Test
  @DisplayName("Should throw NotAuthorized when group value is invalid")
  void getCurrentAccountTypeInvalidGroup() {
    JsonWebToken jwtMock = mock(JsonWebToken.class);
    when(securityIdentity.isAnonymous()).thenReturn(false);
    when(securityIdentity.getPrincipal()).thenReturn(jwtMock);
    when(jwtMock.getGroups()).thenReturn(Set.of("INVALID_ROLE"));

    assertThrows(NotAuthorizedException.class, () -> authService.getCurrentAccountType());
  }

  @Test
  @DisplayName("Should throw NotAuthorized when identity is null/anonymous for claims")
  void getRequiredClaimAnonymous() {
    when(securityIdentity.isAnonymous()).thenReturn(true);
    assertThrows(NotAuthorizedException.class, () -> authService.getCurrentAccountId());
  }

  @Test
  @DisplayName("Should throw NotAuthorized when principal is not JWT for claims")
  void getRequiredClaimNonJwt() {
    when(securityIdentity.isAnonymous()).thenReturn(false);
    when(securityIdentity.getPrincipal()).thenReturn(mock(Principal.class));
    assertThrows(NotAuthorizedException.class, () -> authService.getCurrentAccountId());
  }

  @Test
  @DisplayName("Should return current account ID from claims")
  void getCurrentAccountId() {
    JsonWebToken jwtMock = mock(JsonWebToken.class);
    when(securityIdentity.isAnonymous()).thenReturn(false);
    when(securityIdentity.getPrincipal()).thenReturn(jwtMock);

    UUID accId = UuidCreator.getTimeOrderedEpoch();
    when(jwtMock.getClaim("accountId")).thenReturn(accId.toString());

    assertThat(authService.getCurrentAccountId()).isEqualTo(accId);
  }

  @Test
  @DisplayName("Should return current user ID from claims")
  void getCurrentUserId() {
    JsonWebToken jwtMock = mock(JsonWebToken.class);
    when(securityIdentity.isAnonymous()).thenReturn(false);
    when(securityIdentity.getPrincipal()).thenReturn(jwtMock);

    UUID userId = UuidCreator.getTimeOrderedEpoch();
    when(jwtMock.getClaim("userId")).thenReturn(userId.toString());

    assertThat(authService.getCurrentUserId()).isEqualTo(userId);
  }

  @Test
  @DisplayName("Should return current account type from groups claim")
  void getCurrentAccountType() {
    JsonWebToken jwtMock = mock(JsonWebToken.class);
    when(securityIdentity.isAnonymous()).thenReturn(false);
    when(securityIdentity.getPrincipal()).thenReturn(jwtMock);
    when(jwtMock.getGroups()).thenReturn(Set.of("FORMER_STUDENT"));

    assertThat(authService.getCurrentAccountType()).isEqualTo(AccountType.FORMER_STUDENT);
  }

  @Test
  @DisplayName("Should enforce account type requirements")
  void requireAccountType() {
    JsonWebToken jwtMock = mock(JsonWebToken.class);
    when(securityIdentity.isAnonymous()).thenReturn(false);
    when(securityIdentity.getPrincipal()).thenReturn(jwtMock);
    when(jwtMock.getGroups()).thenReturn(Set.of("FORMER_STUDENT"));

    authService.requireCurrentAccountOfType(AccountType.FORMER_STUDENT);
    assertThrows(
        NotAuthorizedException.class,
        () -> authService.requireCurrentAccountOfType(AccountType.ADMIN));
  }

  @Test
  @DisplayName("Should enforce forbidden account type")
  void requireAccountTypeNot() {
    JsonWebToken jwtMock = mock(JsonWebToken.class);
    when(securityIdentity.isAnonymous()).thenReturn(false);
    when(securityIdentity.getPrincipal()).thenReturn(jwtMock);
    when(jwtMock.getGroups()).thenReturn(Set.of("FORMER_STUDENT"));

    authService.requireCurrentAccountNotOfType(AccountType.ADMIN);
    assertThrows(
        NotAuthorizedException.class,
        () -> authService.requireCurrentAccountNotOfType(AccountType.FORMER_STUDENT));
  }

  @Test
  @DisplayName("Should throw NotAuthorized if claim missing")
  void getRequiredClaimMissing() {
    JsonWebToken jwtMock = mock(JsonWebToken.class);
    when(securityIdentity.isAnonymous()).thenReturn(false);
    when(securityIdentity.getPrincipal()).thenReturn(jwtMock);
    when(jwtMock.getClaim("accountId")).thenReturn(null);

    assertThrows(NotAuthorizedException.class, () -> authService.getCurrentAccountId());
  }

  @Test
  @DisplayName("sha256 should produce consistent hex output")
  void sha256Consistency() {
    String hash1 = AuthServiceImpl.sha256("test");
    String hash2 = AuthServiceImpl.sha256("test");
    assertThat(hash1).isEqualTo(hash2).hasSize(64);
  }
}
