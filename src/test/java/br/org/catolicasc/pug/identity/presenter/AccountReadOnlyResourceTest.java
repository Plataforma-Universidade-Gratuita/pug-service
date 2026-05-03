package br.org.catolicasc.pug.identity.presenter;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.when;

import br.org.catolicasc.pug.helpers.BaseResourceTest;
import br.org.catolicasc.pug.identity.domain.Account;
import br.org.catolicasc.pug.identity.domain.User;
import br.org.catolicasc.pug.identity.service.AuthService;
import br.org.catolicasc.pug.shared.domain.enums.AccountType;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@QuarkusTest
@DisplayName("AccountReadOnlyResource Integration Tests")
class AccountReadOnlyResourceTest extends BaseResourceTest {

  @InjectMock AuthService authService;

  @Test
  @TestSecurity(
      user = "admin",
      roles = {"ADMIN"})
  @DisplayName("GET /v1/identity/accounts/{id} - Success")
  void getByIdSuccess() throws Exception {
    Account[] acc = new Account[1];
    doInTransaction(
        () -> {
          User user = factory.createUser();
          acc[0] = factory.createAccount(user, AccountType.STUDENT);
        });

    given()
        .pathParam("id", acc[0].getId())
        .when()
        .get("/v1/identity/accounts/{id}")
        .then()
        .statusCode(200)
        .body("success", is(true))
        .body("data.id", is(acc[0].getId().toString()));
  }

  @Test
  @TestSecurity(
      user = "admin",
      roles = {"ADMIN"})
  @DisplayName("GET /v1/identity/accounts?email= - Success")
  void getByEmailSuccess() throws Exception {
    Account[] acc = new Account[1];
    doInTransaction(
        () -> {
          User user = factory.createUser();
          acc[0] = factory.createAccount(user, AccountType.STUDENT);
        });

    given()
        .queryParam("email", acc[0].getEmail().getValue())
        .when()
        .get("/v1/identity/accounts")
        .then()
        .statusCode(200)
        .body("data.email", is(acc[0].getEmail().getValue()));
  }

  @Test
  @TestSecurity(
      user = "student",
      roles = {"STUDENT"})
  @DisplayName("GET /v1/identity/accounts/me - Authenticated Success")
  void getMeSuccess() throws Exception {
    Account[] acc = new Account[1];
    doInTransaction(
        () -> {
          User user = factory.createUser();
          acc[0] = factory.createAccount(user, AccountType.STUDENT);
        });

    when(authService.getCurrentAccountId()).thenReturn(acc[0].getId());

    given()
        .when()
        .get("/v1/identity/accounts/me")
        .then()
        .statusCode(200)
        .body("data.id", is(acc[0].getId().toString()));
  }

  @Test
  @TestSecurity(
      user = "admin",
      roles = {"ADMIN"})
  @DisplayName("GET /v1/identity/accounts - List All")
  void listAccounts() throws Exception {
    doInTransaction(
        () -> {
          User user = factory.createUser();
          factory.createAccount(user, AccountType.STUDENT);
        });

    given()
        .when()
        .get("/v1/identity/accounts")
        .then()
        .statusCode(200)
        .body("data", hasSize(greaterThanOrEqualTo(1)));
  }

  @Test
  @DisplayName("Should return 401 when accessing without security")
  void unauthorizedAccess() {
    assertUnauthenticated("/v1/identity/accounts");
  }
}
