package br.org.catolicasc.pug.identity.presenter;

import static br.org.catolicasc.pug.helpers.builders.requests.LoginRequestBuilder.aLoginRequest;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;

import br.org.catolicasc.pug.identity.presenter.dtos.auth.TokenResponse;
import br.org.catolicasc.pug.identity.service.AuthService;
import br.org.catolicasc.pug.shared.domain.enums.AccountType;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
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
    TokenResponse token = new TokenResponse("mocked-token", null, AccountType.STUDENT, 3600);

    when(authService.login(req)).thenReturn(token);

    given()
        .contentType(ContentType.JSON)
        .body(req)
        .when()
        .post("/auth/login")
        .then()
        .statusCode(200)
        .body("success", is(true))
        .body("data.token", is("mocked-token"));
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
}
