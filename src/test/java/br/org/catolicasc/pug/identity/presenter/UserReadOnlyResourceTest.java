package br.org.catolicasc.pug.identity.presenter;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;

import br.org.catolicasc.pug.helpers.TestDataFactory;
import br.org.catolicasc.pug.identity.domain.User;
import br.org.catolicasc.pug.identity.service.AuthService;
import com.github.f4b6a3.uuid.UuidCreator;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.UserTransaction;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@QuarkusTest
@DisplayName("UserReadOnlyResource Integration Tests")
class UserReadOnlyResourceTest {

  @Inject TestDataFactory factory;
  @Inject UserTransaction utx;
  @Inject EntityManager em;

  @InjectMock AuthService authService;

  @Test
  @TestSecurity(
      user = "admin",
      roles = {"ADMIN"})
  @DisplayName("GET /identity/users/{id} - Success")
  void getByIdSuccess() throws Exception {
    utx.begin();
    User user = factory.createUser();
    em.flush();
    utx.commit();

    given()
        .pathParam("id", user.getId())
        .when()
        .get("/identity/users/{id}")
        .then()
        .statusCode(200)
        .body("success", is(true))
        .body("data.id", is(user.getId().toString()));
  }

  @Test
  @TestSecurity(
      user = "admin",
      roles = {"ADMIN"})
  @DisplayName("GET /identity/users/{id} - Not Found")
  void getByIdNotFound() {
    given()
        .pathParam("id", UuidCreator.getTimeOrderedEpoch())
        .when()
        .get("/identity/users/{id}")
        .then()
        .statusCode(404)
        .body("success", is(false))
        .body("error.code", is("USER_NOT_FOUND"));
  }

  @Test
  @TestSecurity(
      user = "admin",
      roles = {"ADMIN"})
  @DisplayName("GET /identity/users/by-cpf/{cpf} - Success")
  void getByCpfSuccess() throws Exception {
    utx.begin();
    User user = factory.createUser();
    em.flush();
    utx.commit();

    given()
        .pathParam("cpf", user.getCpf().getValue())
        .when()
        .get("/identity/users/by-cpf/{cpf}")
        .then()
        .statusCode(200)
        .body("data.cpf", is(user.getCpf().getValue()));
  }

  @Test
  @TestSecurity(
      user = "student",
      roles = {"STUDENT"})
  @DisplayName("GET /identity/users/me - Authenticated Success")
  void getMeSuccess() throws Exception {
    utx.begin();
    User user = factory.createUser();
    utx.commit();

    when(authService.getCurrentUserId()).thenReturn(user.getId());

    given()
        .when()
        .get("/identity/users/me")
        .then()
        .statusCode(200)
        .body("data.id", is(user.getId().toString()));
  }

  @Test
  @TestSecurity(
      user = "admin",
      roles = {"ADMIN"})
  @DisplayName("GET /identity/users - List All")
  void listUsers() throws Exception {
    utx.begin();
    factory.createUser();
    utx.commit();

    given()
        .when()
        .get("/identity/users")
        .then()
        .statusCode(200)
        .body("data", hasSize(greaterThanOrEqualTo(1)));
  }

  @Test
  @DisplayName("Should return 401 when accessing without security")
  void unauthorizedAccess() {
    given().when().get("/identity/users").then().statusCode(401);
  }
}
