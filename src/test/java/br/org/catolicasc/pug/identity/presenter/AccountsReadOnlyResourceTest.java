package br.org.catolicasc.pug.identity.presenter;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
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
@DisplayName("AccountsReadOnlyResource Integration Tests")
class AccountsReadOnlyResourceTest extends BaseResourceTest {

  @InjectMock AuthService authService;

  @Test
  @TestSecurity(
      user = "admin",
      roles = {"ADMIN"})
  @DisplayName("GET /v1/identity/accounts/{id} - Success")
  void getByIdSuccess() throws Exception {
    Account[] account = new Account[1];
    doInTransaction(
        () -> {
          User user = factory.createUser();
          account[0] = factory.createAccount(user, AccountType.FORMER_STUDENT);
        });

    given()
        .pathParam("id", account[0].getId())
        .when()
        .get("/v1/identity/accounts/{id}")
        .then()
        .statusCode(200)
        .body("success", is(true))
        .body("data.id", is(account[0].getId().toString()));
  }

  @Test
  @TestSecurity(
      user = "formerStudent",
      roles = {"STUDENT"})
  @DisplayName("GET /v1/identity/accounts/me - Authenticated Success")
  void getMeSuccess() throws Exception {
    Account[] account = new Account[1];
    doInTransaction(
        () -> {
          User user = factory.createUser();
          account[0] = factory.createAccount(user, AccountType.FORMER_STUDENT);
        });

    when(authService.getCurrentAccountId()).thenReturn(account[0].getId());

    given()
        .when()
        .get("/v1/identity/accounts/me")
        .then()
        .statusCode(200)
        .body("data.id", is(account[0].getId().toString()));
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
          factory.createAccount(user, AccountType.FORMER_STUDENT);
        });

    given()
        .when()
        .get("/v1/identity/accounts")
        .then()
        .statusCode(200)
        .body("data", hasSize(greaterThanOrEqualTo(1)));
  }

  @Test
  @TestSecurity(
      user = "admin",
      roles = {"ADMIN"})
  @DisplayName("GET /v1/identity/accounts?ids= - Filter By Ids")
  void listAccountsByIds() throws Exception {
    Account[] accounts = new Account[2];
    doInTransaction(
        () -> {
          accounts[0] = factory.createAccount(factory.createUser(), AccountType.FORMER_STUDENT);
          accounts[1] = factory.createAccount(factory.createUser(), AccountType.PARTNER);
        });

    given()
        .queryParam("ids", accounts[0].getId())
        .when()
        .get("/v1/identity/accounts")
        .then()
        .statusCode(200)
        .body("data", hasSize(1))
        .body("data[0].id", is(accounts[0].getId().toString()));
  }

  @Test
  @TestSecurity(
      user = "admin",
      roles = {"ADMIN"})
  @DisplayName("POST /v1/identity/accounts/search - Paginated Search")
  void searchAccounts() throws Exception {
    Account[] account = new Account[1];
    User[] user = new User[1];
    doInTransaction(
        () -> {
          user[0] = factory.createUser();
          account[0] = factory.createAccount(user[0], AccountType.FORMER_STUDENT);
        });

    given()
        .queryParam("page", 0)
        .queryParam("size", 10)
        .contentType("application/json")
        .body(
            """
            {
              "name": "%s",
              "cpf": "%s",
              "email": "%s",
              "accountTypes": ["%s"]
            }
            """
                .formatted(
                    user[0].getName().split(" ")[0],
                    user[0].getCpf().getValue().substring(0, 3),
                    account[0].getEmail().getValue().substring(0, 4),
                    account[0].getAccountType().name()))
        .when()
        .post("/v1/identity/accounts/search")
        .then()
        .statusCode(200)
        .body("data.content", hasSize(1))
        .body("data.content[0].id", is(account[0].getId().toString()))
        .body("data.content[0].user.id", is(user[0].getId().toString()))
        .body("data.content[0].user.name", is(user[0].getName()))
        .body("data.page", is(0))
        .body("data.size", is(10));
  }

  @Test
  @DisplayName("Should return 401 when accessing without security")
  void unauthorizedAccess() {
    assertUnauthenticated("/v1/identity/accounts");
  }
}

