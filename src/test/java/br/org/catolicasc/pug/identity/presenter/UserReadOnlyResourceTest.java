package br.org.catolicasc.pug.identity.presenter;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;

import br.org.catolicasc.pug.helpers.BaseResourceTest;
import br.org.catolicasc.pug.identity.domain.User;
import br.org.catolicasc.pug.identity.service.AuthService;
import com.github.f4b6a3.uuid.UuidCreator;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@QuarkusTest
@DisplayName("UserReadOnlyResource Integration Tests")
class UserReadOnlyResourceTest extends BaseResourceTest {

  @InjectMock AuthService authService;

  @Test
  @TestSecurity(
      user = "admin",
      roles = {"ADMIN"})
  @DisplayName("GET /v1/identity/users/{id} - Success")
  void getByIdSuccess() throws Exception {
    User[] user = new User[1];
    doInTransaction(() -> user[0] = factory.createUser());

    given()
        .pathParam("id", user[0].getId())
        .when()
        .get("/v1/identity/users/{id}")
        .then()
        .statusCode(200)
        .body("success", is(true))
        .body("data.id", is(user[0].getId().toString()));
  }

  @Test
  @TestSecurity(
      user = "admin",
      roles = {"ADMIN"})
  @DisplayName("GET /v1/identity/users/{id} - Not Found")
  void getByIdNotFound() {
    given()
        .pathParam("id", UuidCreator.getTimeOrderedEpoch())
        .when()
        .get("/v1/identity/users/{id}")
        .then()
        .statusCode(404)
        .body("success", is(false))
        .body("error.code", is("USER_NOT_FOUND"));
  }

  @Test
  @TestSecurity(
      user = "admin",
      roles = {"ADMIN"})
  @DisplayName("GET /v1/identity/users?cpf= - Success")
  void getByCpfSuccess() throws Exception {
    User[] user = new User[1];
    doInTransaction(() -> user[0] = factory.createUser());

    given()
        .queryParam("cpf", user[0].getCpf().getValue())
        .when()
        .get("/v1/identity/users")
        .then()
        .statusCode(200)
        .body("data.cpf", is(user[0].getCpf().getValue()));
  }

  @Test
  @TestSecurity(
      user = "student",
      roles = {"STUDENT"})
  @DisplayName("GET /v1/identity/users/me - Authenticated Success")
  void getMeSuccess() throws Exception {
    User[] user = new User[1];
    doInTransaction(() -> user[0] = factory.createUser());

    when(authService.getCurrentUserId()).thenReturn(user[0].getId());

    given()
        .when()
        .get("/v1/identity/users/me")
        .then()
        .statusCode(200)
        .body("data.id", is(user[0].getId().toString()));
  }

  @Test
  @TestSecurity(
      user = "admin",
      roles = {"ADMIN"})
  @DisplayName("GET /v1/identity/users - List All")
  void listUsers() throws Exception {
    doInTransaction(() -> factory.createUser());

    given()
        .when()
        .get("/v1/identity/users")
        .then()
        .statusCode(200)
        .body("data", hasSize(greaterThanOrEqualTo(1)));
  }

  @Test
  @DisplayName("Should return 401 when accessing without security")
  void unauthorizedAccess() {
    assertUnauthenticated("/v1/identity/users");
  }
}
