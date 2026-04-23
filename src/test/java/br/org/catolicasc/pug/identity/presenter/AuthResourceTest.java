package br.org.catolicasc.pug.identity.presenter;

import static br.org.catolicasc.pug.helpers.builders.requests.LoginRequestBuilder.aLoginRequest;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import br.org.catolicasc.pug.identity.presenter.dtos.auth.LogoutRequest;
import br.org.catolicasc.pug.identity.presenter.dtos.auth.RefreshRequest;
import br.org.catolicasc.pug.identity.presenter.dtos.auth.TokenResponse;
import br.org.catolicasc.pug.identity.service.AuthService;
import br.org.catolicasc.pug.shared.domain.enums.AccountType;
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
  @DisplayName("POST /auth/login - Success")
  void loginSuccess() {
    var req = aLoginRequest().withEmail("test@pug.com").withPassword("password").build();
    TokenResponse token =
        new TokenResponse("mocked-token", "mocked-refresh", null, AccountType.STUDENT, 900, 604800);

    when(authService.login(req)).thenReturn(token);

    given()
        .contentType(ContentType.JSON)
        .body(req)
        .when()
        .post("/auth/login")
        .then()
        .statusCode(200)
        .body("success", is(true))
        .body("data.token", is("mocked-token"))
        .body("data.refreshToken", is("mocked-refresh"));
  }

  @Test
  @DisplayName("POST /auth/login - Unauthorized")
  void loginUnauthorized() {
    var req = aLoginRequest().withEmail("bad@pug.com").withPassword("wrong").build();
    when(authService.login(req)).thenThrow(new NotAuthorizedException("Unauthorized"));

    given()
        .contentType(ContentType.JSON)
        .body(req)
        .when()
        .post("/auth/login")
        .then()
        .statusCode(401);
  }

  @Test
  @DisplayName("POST /auth/refresh - Success")
  void refreshSuccess() {
    var req = new RefreshRequest("valid-refresh-token");
    TokenResponse token =
        new TokenResponse(
            "new-access", "valid-refresh-token", null, AccountType.STUDENT, 900, 604800);

    when(authService.refresh(req)).thenReturn(token);

    given()
        .contentType(ContentType.JSON)
        .body(req)
        .when()
        .post("/auth/refresh")
        .then()
        .statusCode(200)
        .body("success", is(true))
        .body("data.token", is("new-access"))
        .body("data.refreshToken", is("valid-refresh-token"));
  }

  @Test
  @DisplayName("POST /auth/refresh - Unauthorized with invalid token")
  void refreshUnauthorized() {
    var req = new RefreshRequest("invalid-token");
    when(authService.refresh(req)).thenThrow(new NotAuthorizedException("Unauthorized"));

    given()
        .contentType(ContentType.JSON)
        .body(req)
        .when()
        .post("/auth/refresh")
        .then()
        .statusCode(401);
  }

  @Test
  @DisplayName("POST /auth/logout - Success (204 No Content)")
  void logoutSuccess() {
    var req = new LogoutRequest("some-refresh-token");
    doNothing().when(authService).logout(req);

    given()
        .contentType(ContentType.JSON)
        .body(req)
        .when()
        .post("/auth/logout")
        .then()
        .statusCode(204);
  }

  @Test
  @DisplayName("POST /auth/logout - Validation error with blank token")
  void logoutBadRequest() {
    given()
        .contentType(ContentType.JSON)
        .body("{\"refreshToken\": \"\"}")
        .when()
        .post("/auth/logout")
        .then()
        .statusCode(422);
  }

  @Test
  @TestSecurity(
      user = "admin",
      roles = {"ADMIN"})
  @DisplayName("POST /auth/logout-all - Success (204 No Content)")
  void logoutAllSuccess() {
    doNothing().when(authService).logoutAll();

    given().when().post("/auth/logout-all").then().statusCode(204);
  }

  @Test
  @DisplayName("POST /auth/logout-all - Unauthorized without token")
  void logoutAllUnauthorized() {
    given().when().post("/auth/logout-all").then().statusCode(401);
  }
}
