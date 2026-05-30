package br.org.catolicasc.pug.identity.presenter;

import static br.org.catolicasc.pug.helpers.builders.requests.LoginRequestBuilder.aLoginRequest;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import br.org.catolicasc.pug.identity.presenter.dtos.auth.CredentialsRequest;
import br.org.catolicasc.pug.identity.presenter.dtos.auth.LogoutRequest;
import br.org.catolicasc.pug.identity.presenter.dtos.auth.RefreshRequest;
import br.org.catolicasc.pug.identity.presenter.dtos.auth.TokenResponse;
import br.org.catolicasc.pug.identity.service.AuthService;
import br.org.catolicasc.pug.shared.domain.enums.AccountType;
import com.github.f4b6a3.uuid.UuidCreator;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import io.restassured.http.ContentType;
import jakarta.ws.rs.NotAuthorizedException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@QuarkusTest
@DisplayName("AuthResource Integration Tests")
class AuthResourceTest {

  @InjectMock AuthService authService;

  @Test
  @DisplayName("POST /v1/auth/login - Success")
  void loginSuccess() {
    var req = aLoginRequest().withEmail("test@pug.com").withPassword("password").build();
    TokenResponse token =
        new TokenResponse(
            "mocked-token",
            "mocked-refresh",
            UuidCreator.getTimeOrderedEpoch(),
            AccountType.FORMER_STUDENT,
            true,
            900,
            604800);

    when(authService.login(req)).thenReturn(token);

    given()
        .contentType(ContentType.JSON)
        .body(req)
        .when()
        .post("/v1/auth/login")
        .then()
        .statusCode(200)
        .body("success", is(true))
        .body("data.token", is("mocked-token"))
        .body("data.refreshToken", is("mocked-refresh"));
  }

  @Test
  @DisplayName("POST /v1/auth/login - Unauthorized")
  void loginUnauthorized() {
    var req = aLoginRequest().withEmail("bad@pug.com").withPassword("wrongpass").build();
    when(authService.login(req)).thenThrow(new NotAuthorizedException("Unauthorized"));

    given()
        .contentType(ContentType.JSON)
        .body(req)
        .when()
        .post("/v1/auth/login")
        .then()
        .statusCode(401);
  }

  @Test
  @TestSecurity(
      user = "former.student@pug.com",
      roles = {"FORMER_STUDENT"})
  @DisplayName("POST /v1/auth/wire-credentials - Success")
  void wireCredentialsSuccess() {
    var req = new CredentialsRequest("former.student@pug.com", "StrongPass1!");
    doNothing().when(authService).wireCredentials(req);

    given()
        .contentType(ContentType.JSON)
        .body(req)
        .when()
        .post("/v1/auth/wire-credentials")
        .then()
        .statusCode(204);
  }

  @Test
  @DisplayName("POST /v1/auth/wire-credentials - Unauthorized without token")
  void wireCredentialsUnauthorized() {
    var req = new CredentialsRequest("former.student@pug.com", "StrongPass1!");

    given()
        .contentType(ContentType.JSON)
        .body(req)
        .when()
        .post("/v1/auth/wire-credentials")
        .then()
        .statusCode(401);
  }

  @Test
  @TestSecurity(
      user = "former.student@pug.com",
      roles = {"FORMER_STUDENT"})
  @DisplayName("POST /v1/auth/wire-credentials - Validation error with short password")
  void wireCredentialsValidationError() {
    given()
        .contentType(ContentType.JSON)
        .body("{\"email\":\"former.student@pug.com\",\"password\":\"short\"}")
        .when()
        .post("/v1/auth/wire-credentials")
        .then()
        .statusCode(422);
  }

  @Test
  @DisplayName("POST /v1/auth/refresh - Success")
  void refreshSuccess() {
    var req = new RefreshRequest("valid-refresh-token");
    TokenResponse token =
        new TokenResponse(
            "new-access",
            "valid-refresh-token",
            UuidCreator.getTimeOrderedEpoch(),
            AccountType.FORMER_STUDENT,
            true,
            900,
            604800);

    when(authService.refresh(req)).thenReturn(token);

    given()
        .contentType(ContentType.JSON)
        .body(req)
        .when()
        .post("/v1/auth/refresh")
        .then()
        .statusCode(200)
        .body("success", is(true))
        .body("data.token", is("new-access"))
        .body("data.refreshToken", is("valid-refresh-token"));
  }

  @Test
  @DisplayName("POST /v1/auth/refresh - Unauthorized with invalid token")
  void refreshUnauthorized() {
    var req = new RefreshRequest("invalid-token");
    when(authService.refresh(req)).thenThrow(new NotAuthorizedException("Unauthorized"));

    given()
        .contentType(ContentType.JSON)
        .body(req)
        .when()
        .post("/v1/auth/refresh")
        .then()
        .statusCode(401);
  }

  @Test
  @DisplayName("POST /v1/auth/logout - Success (204 No Content)")
  void logoutSuccess() {
    var req = new LogoutRequest("some-refresh-token");
    doNothing().when(authService).logout(req);

    given()
        .contentType(ContentType.JSON)
        .body(req)
        .when()
        .post("/v1/auth/logout")
        .then()
        .statusCode(204);
  }

  @Test
  @DisplayName("POST /v1/auth/logout - Validation error with blank token")
  void logoutBadRequest() {
    given()
        .contentType(ContentType.JSON)
        .body("{\"refreshToken\": \"\"}")
        .when()
        .post("/v1/auth/logout")
        .then()
        .statusCode(422);
  }

  @Test
  @TestSecurity(
      user = "admin",
      roles = {"ADMIN"})
  @DisplayName("POST /v1/auth/logout-all - Success (204 No Content)")
  void logoutAllSuccess() {
    doNothing().when(authService).logoutAll();

    given().when().post("/v1/auth/logout-all").then().statusCode(204);
  }

  @Test
  @DisplayName("POST /v1/auth/logout-all - Unauthorized without token")
  void logoutAllUnauthorized() {
    given().when().post("/v1/auth/logout-all").then().statusCode(401);
  }
}
