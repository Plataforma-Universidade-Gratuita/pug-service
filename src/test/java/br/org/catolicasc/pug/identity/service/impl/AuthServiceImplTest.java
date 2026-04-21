package br.org.catolicasc.pug.identity.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import br.org.catolicasc.pug.identity.domain.Account;
import br.org.catolicasc.pug.identity.domain.vos.Email;
import br.org.catolicasc.pug.identity.presenter.dtos.auth.LoginRequest;
import br.org.catolicasc.pug.identity.presenter.dtos.auth.TokenResponse;
import br.org.catolicasc.pug.identity.service.AccountService;
import br.org.catolicasc.pug.identity.service.PasswordService;
import br.org.catolicasc.pug.shared.domain.enums.AccountType;
import br.org.catolicasc.pug.shared.exceptions.ResourceNotFoundException;
import io.quarkus.security.identity.SecurityIdentity;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.ws.rs.NotAuthorizedException;
import java.util.Set;
import java.util.UUID;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@QuarkusTest
@DisplayName("AuthServiceImpl Coverage")
class AuthServiceImplTest {

  @Inject AuthServiceImpl authService;

  @InjectMock AccountService accountService;
  @InjectMock PasswordService passwordService;
  @InjectMock SecurityIdentity securityIdentity;

  @Test
  @DisplayName("Should login successfully and return token")
  void loginSuccess() {
    String email = "test@pug.com";
    String raw = "password";
    String hash = "hashed";

    Account acc =
        Account.factory(UUID.randomUUID(), Email.factory(email), AccountType.STUDENT, hash);
    acc = acc.toBuilder().active(true).build();

    when(accountService.getByEmail(email)).thenReturn(acc);
    when(passwordService.verify(hash, raw)).thenReturn(true);

    TokenResponse response = authService.login(new LoginRequest(email, raw));

    assertThat(response.token()).isNotBlank();
    assertThat(response.accountId()).isEqualTo(acc.getId());
  }

  @Test
  @DisplayName("Should throw NotAuthorized when account not found")
  void loginAccountNotFound() {
    when(accountService.getByEmail("unknown@pug.com"))
        .thenThrow(
            new ResourceNotFoundException(
                br.org.catolicasc.pug.identity.domain.enums.IdentityErrorCodes.ACCOUNT_NOT_FOUND));

    Assertions.assertThrows(
        NotAuthorizedException.class,
        () -> authService.login(new LoginRequest("unknown@pug.com", "pass")));
  }

  @Test
  @DisplayName("Should throw NotAuthorized when password mismatch")
  void loginWrongPassword() {
    String email = "test@pug.com";
    String hash = "hashed";
    Account acc =
        Account.factory(UUID.randomUUID(), Email.factory(email), AccountType.STUDENT, hash);

    when(accountService.getByEmail(email)).thenReturn(acc);
    when(passwordService.verify(hash, "wrong")).thenReturn(false);

    Assertions.assertThrows(
        NotAuthorizedException.class, () -> authService.login(new LoginRequest(email, "wrong")));
  }

  @Test
  @DisplayName("Should throw NotAuthorized when account is inactive")
  void loginInactiveAccount() {
    String email = "test@pug.com";
    Account acc =
        Account.factory(UUID.randomUUID(), Email.factory(email), AccountType.STUDENT, "hash")
            .toBuilder()
            .active(false)
            .build();

    when(accountService.getByEmail(email)).thenReturn(acc);

    Assertions.assertThrows(
        NotAuthorizedException.class, () -> authService.login(new LoginRequest(email, "pass")));
  }

  @Test
  @DisplayName("Should return current account ID from claims")
  void getCurrentAccountId() {
    JsonWebToken jwtMock = mock(JsonWebToken.class);
    when(securityIdentity.isAnonymous()).thenReturn(false);
    when(securityIdentity.getPrincipal()).thenReturn(jwtMock);

    UUID accId = UUID.randomUUID();
    when(jwtMock.getClaim("accountId")).thenReturn(accId.toString());

    assertThat(authService.getCurrentAccountId()).isEqualTo(accId);
  }

  @Test
  @DisplayName("Should return current user ID from claims")
  void getCurrentUserId() {
    JsonWebToken jwtMock = mock(JsonWebToken.class);
    when(securityIdentity.isAnonymous()).thenReturn(false);
    when(securityIdentity.getPrincipal()).thenReturn(jwtMock);

    UUID userId = UUID.randomUUID();
    when(jwtMock.getClaim("userId")).thenReturn(userId.toString());

    assertThat(authService.getCurrentUserId()).isEqualTo(userId);
  }

  @Test
  @DisplayName("Should return current account type from groups claim")
  void getCurrentAccountType() {
    JsonWebToken jwtMock = mock(JsonWebToken.class);
    when(securityIdentity.isAnonymous()).thenReturn(false);
    when(securityIdentity.getPrincipal()).thenReturn(jwtMock);
    when(jwtMock.getGroups()).thenReturn(Set.of("STUDENT"));

    assertThat(authService.getCurrentAccountType()).isEqualTo(AccountType.STUDENT);
  }

  @Test
  @DisplayName("Should enforce account type requirements")
  void requireAccountType() {
    JsonWebToken jwtMock = mock(JsonWebToken.class);
    when(securityIdentity.isAnonymous()).thenReturn(false);
    when(securityIdentity.getPrincipal()).thenReturn(jwtMock);
    when(jwtMock.getGroups()).thenReturn(Set.of("STUDENT"));

    authService.requireCurrentAccountOfType(AccountType.STUDENT);
    Assertions.assertThrows(
        NotAuthorizedException.class,
        () -> authService.requireCurrentAccountOfType(AccountType.ADMIN));
  }

  @Test
  @DisplayName("Should enforce forbidden account type")
  void requireAccountTypeNot() {
    JsonWebToken jwtMock = mock(JsonWebToken.class);
    when(securityIdentity.isAnonymous()).thenReturn(false);
    when(securityIdentity.getPrincipal()).thenReturn(jwtMock);
    when(jwtMock.getGroups()).thenReturn(Set.of("STUDENT"));

    authService.requireCurrentAccountNotOfType(AccountType.ADMIN);
    Assertions.assertThrows(
        NotAuthorizedException.class,
        () -> authService.requireCurrentAccountNotOfType(AccountType.STUDENT));
  }

  @Test
  @DisplayName("Should throw NotAuthorized if claim missing")
  void getRequiredClaimMissing() {
    JsonWebToken jwtMock = mock(JsonWebToken.class);
    when(securityIdentity.isAnonymous()).thenReturn(false);
    when(securityIdentity.getPrincipal()).thenReturn(jwtMock);
    when(jwtMock.getClaim("accountId")).thenReturn(null);

    Assertions.assertThrows(NotAuthorizedException.class, () -> authService.getCurrentAccountId());
  }
}
