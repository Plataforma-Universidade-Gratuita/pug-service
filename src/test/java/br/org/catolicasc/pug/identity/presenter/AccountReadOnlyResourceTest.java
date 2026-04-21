package br.org.catolicasc.pug.identity.presenter;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.when;

import br.org.catolicasc.pug.helpers.TestDataFactory;
import br.org.catolicasc.pug.identity.domain.Account;
import br.org.catolicasc.pug.identity.domain.User;
import br.org.catolicasc.pug.identity.service.AuthService;
import br.org.catolicasc.pug.shared.domain.enums.AccountType;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.UserTransaction;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@QuarkusTest
@DisplayName("AccountReadOnlyResource Integration Tests")
class AccountReadOnlyResourceTest {

  @Inject TestDataFactory factory;
  @Inject UserTransaction utx;
  @Inject EntityManager em;

  @InjectMock AuthService authService;

  @Test
  @TestSecurity(
      user = "admin",
      roles = {"ADMIN"})
  @DisplayName("GET /identity/accounts/{id} - Success")
  void getByIdSuccess() throws Exception {
    utx.begin();
    User user = factory.createUser();
    Account acc = factory.createAccount(user, AccountType.STUDENT);
    em.flush();
    utx.commit();

    given()
        .pathParam("id", acc.getId())
        .when()
        .get("/identity/accounts/{id}")
        .then()
        .statusCode(200)
        .body("success", is(true))
        .body("data.id", is(acc.getId().toString()));
  }

  @Test
  @TestSecurity(
      user = "admin",
      roles = {"ADMIN"})
  @DisplayName("GET /identity/accounts/by-email/{email} - Success")
  void getByEmailSuccess() throws Exception {
    utx.begin();
    User user = factory.createUser();
    Account acc = factory.createAccount(user, AccountType.STUDENT);
    em.flush();
    utx.commit();

    given()
        .pathParam("email", acc.getEmail().getValue())
        .when()
        .get("/identity/accounts/by-email/{email}")
        .then()
        .statusCode(200)
        .body("data.email", is(acc.getEmail().getValue()));
  }

  @Test
  @TestSecurity(
      user = "student",
      roles = {"STUDENT"})
  @DisplayName("GET /identity/accounts/me - Authenticated Success")
  void getMeSuccess() throws Exception {
    utx.begin();
    User user = factory.createUser();
    Account acc = factory.createAccount(user, AccountType.STUDENT);
    utx.commit();

    when(authService.getCurrentAccountId()).thenReturn(acc.getId());

    given()
        .when()
        .get("/identity/accounts/me")
        .then()
        .statusCode(200)
        .body("data.id", is(acc.getId().toString()));
  }

  @Test
  @TestSecurity(
      user = "admin",
      roles = {"ADMIN"})
  @DisplayName("GET /identity/accounts - List All")
  void listAccounts() throws Exception {
    utx.begin();
    User user = factory.createUser();
    factory.createAccount(user, AccountType.STUDENT);
    utx.commit();

    given()
        .when()
        .get("/identity/accounts")
        .then()
        .statusCode(200)
        .body("data", hasSize(greaterThanOrEqualTo(1)));
  }

  @Test
  @DisplayName("Should return 401 when accessing without security")
  void unauthorizedAccess() {
    given().when().get("/identity/accounts").then().statusCode(401);
  }
}
