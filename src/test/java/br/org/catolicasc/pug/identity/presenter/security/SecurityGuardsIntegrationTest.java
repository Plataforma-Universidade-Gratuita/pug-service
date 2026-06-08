package br.org.catolicasc.pug.identity.presenter.security;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;

import br.org.catolicasc.pug.helpers.BaseResourceTest;
import br.org.catolicasc.pug.identity.infra.persistence.AccountEntity;
import br.org.catolicasc.pug.identity.infra.persistence.RefreshTokenEntity;
import br.org.catolicasc.pug.identity.infra.persistence.UserEntity;
import br.org.catolicasc.pug.identity.service.AuthService;
import br.org.catolicasc.pug.shared.domain.enums.AccountType;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import io.quarkus.test.security.jwt.Claim;
import io.quarkus.test.security.jwt.ClaimType;
import io.quarkus.test.security.jwt.JwtSecurity;
import java.time.OffsetDateTime;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@QuarkusTest
@DisplayName("Security Guard Integration Tests")
class SecurityGuardsIntegrationTest extends BaseResourceTest {

  private static final UUID ACCOUNT_ID = UUID.fromString("00000000-0000-7000-8000-000000000111");
  private static final UUID USER_ID = UUID.fromString("00000000-0000-7000-8000-000000000112");
  private static final UUID SESSION_ID = UUID.fromString("00000000-0000-7000-8000-000000000113");
  private static final String ACCOUNT_ID_CLAIM = "00000000-0000-7000-8000-000000000111";
  private static final String SESSION_ID_CLAIM = "00000000-0000-7000-8000-000000000113";

  @InjectMock AuthService authService;

  @Test
  @TestSecurity(
      user = "security.guard.admin@pug.com",
      roles = {"ADMIN"})
  @JwtSecurity(
      claims = {
        @Claim(key = "accountId", value = ACCOUNT_ID_CLAIM),
        @Claim(key = "refreshTokenId", value = SESSION_ID_CLAIM),
        @Claim(key = "passwordWired", value = "false", type = ClaimType.BOOLEAN)
      })
  @DisplayName("password setup guard should block protected endpoints when password is not wired")
  void passwordGuardBlocksProtectedEndpoints() throws Exception {
    seedFixture(true);

    given()
        .when()
        .get("/v1/geo/cities")
        .then()
        .statusCode(422)
        .body("error.code", is("ACCOUNT_PASSWORD_SETUP_REQUIRED"));
  }

  @Test
  @TestSecurity(
      user = "security.guard.admin@pug.com",
      roles = {"ADMIN"})
  @JwtSecurity(
      claims = {
        @Claim(key = "accountId", value = ACCOUNT_ID_CLAIM),
        @Claim(key = "refreshTokenId", value = SESSION_ID_CLAIM),
        @Claim(key = "passwordWired", value = "false", type = ClaimType.BOOLEAN)
      })
  @DisplayName("password setup guard should still allow /me endpoints")
  void passwordGuardAllowsMeEndpoints() throws Exception {
    seedFixture(true);
    when(authService.getCurrentAccountId()).thenReturn(ACCOUNT_ID);

    given()
        .when()
        .get("/v1/identity/accounts/me")
        .then()
        .statusCode(200)
        .body("data.id", is(ACCOUNT_ID.toString()));
  }

  @Test
  @TestSecurity(
      user = "security.guard.admin@pug.com",
      roles = {"ADMIN"})
  @JwtSecurity(
      claims = {
        @Claim(key = "accountId", value = ACCOUNT_ID_CLAIM),
        @Claim(key = "refreshTokenId", value = SESSION_ID_CLAIM),
        @Claim(key = "passwordWired", value = "true", type = ClaimType.BOOLEAN)
      })
  @DisplayName("active session guard should reject revoked sessions")
  void activeSessionGuardRejectsRevokedSessions() throws Exception {
    seedFixture(false);

    given().when().get("/v1/geo/cities").then().statusCode(401);
  }

  @Test
  @TestSecurity(
      user = "security.guard.admin@pug.com",
      roles = {"ADMIN"})
  @JwtSecurity(
      claims = {
        @Claim(key = "accountId", value = ACCOUNT_ID_CLAIM),
        @Claim(key = "refreshTokenId", value = SESSION_ID_CLAIM),
        @Claim(key = "passwordWired", value = "true", type = ClaimType.BOOLEAN)
      })
  @DisplayName("active session guard should allow live sessions")
  void activeSessionGuardAllowsLiveSessions() throws Exception {
    seedFixture(true);

    given()
        .when()
        .get("/v1/geo/cities")
        .then()
        .statusCode(200)
        .body("data", hasSize(greaterThan(0)));
  }

  private void seedFixture(boolean withSession) throws Exception {
    doInTransaction(
        () -> {
          clearFixture();

          OffsetDateTime now = OffsetDateTime.now();

          UserEntity user = new UserEntity();
          user.setId(USER_ID);
          user.setCpf("12345678909");
          user.setName("Security Guard Admin");
          user.setCreatedAt(now);
          user.setUpdatedAt(now);
          em.persist(user);

          AccountEntity account = new AccountEntity();
          account.setId(ACCOUNT_ID);
          account.setUserId(USER_ID);
          account.setEmail("security.guard.admin@pug.com");
          account.setAccountType(AccountType.ADMIN);
          account.setPasswordHash("wired-password-hash");
          account.setActive(true);
          account.setCreatedAt(now);
          account.setUpdatedAt(now);
          em.persist(account);

          if (withSession) {
            RefreshTokenEntity refreshToken = new RefreshTokenEntity();
            refreshToken.setId(SESSION_ID);
            refreshToken.setAccount(account);
            refreshToken.setTokenHash("security-guard-session-hash");
            refreshToken.setExpiresAt(now.plusHours(1));
            refreshToken.setCreatedAt(now);
            refreshToken.setUpdatedAt(now);
            em.persist(refreshToken);
          }
        });
  }

  private void clearFixture() {
    em.createQuery("delete from RefreshTokenEntity where id = :sessionId")
        .setParameter("sessionId", SESSION_ID)
        .executeUpdate();
    em.createQuery("delete from AccountEntity where id = :accountId")
        .setParameter("accountId", ACCOUNT_ID)
        .executeUpdate();
    em.createQuery("delete from UserEntity where id = :userId")
        .setParameter("userId", USER_ID)
        .executeUpdate();
  }
}
