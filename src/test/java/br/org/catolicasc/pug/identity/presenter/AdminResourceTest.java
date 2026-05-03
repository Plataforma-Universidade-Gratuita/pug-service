package br.org.catolicasc.pug.identity.presenter;

import static br.org.catolicasc.pug.helpers.builders.requests.AdminCreateRequestBuilder.anAdminCreateRequest;
import static br.org.catolicasc.pug.helpers.builders.requests.AdminUpdateRequestBuilder.anAdminUpdateRequest;
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
import br.org.catolicasc.pug.shared.domain.enums.Campi;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@QuarkusTest
@DisplayName("AdminResource Integration Tests")
class AdminResourceTest extends BaseResourceTest {

  @InjectMock AuthService authService;

  @Test
  @TestSecurity(
      user = "admin",
      roles = {"ADMIN"})
  @DisplayName("POST /v1/identity/admins - Success")
  void createSuccess() {
    var req = anAdminCreateRequest().withCampus(Campi.JARAGUA_DO_SUL).build();

    given()
        .contentType(ContentType.JSON)
        .body(req)
        .when()
        .post("/v1/identity/admins")
        .then()
        .statusCode(201)
        .body("data.campus.campus", is("JARAGUA_DO_SUL"));
  }

  @Test
  @TestSecurity(
      user = "admin",
      roles = {"ADMIN"})
  @DisplayName("PUT /v1/identity/admins/{id} - Success")
  void updateSuccess() throws Exception {
    Account[] acc = new Account[1];
    doInTransaction(
        () -> {
          User user = factory.createUser();
          acc[0] = factory.createAccount(user, AccountType.ADMIN);
          factory.createAdmin(acc[0]);
        });

    var req = anAdminUpdateRequest().withName(null).withCampus(Campi.JOINVILLE).build();

    given()
        .contentType(ContentType.JSON)
        .pathParam("id", acc[0].getId())
        .body(req)
        .when()
        .put("/v1/identity/admins/{id}")
        .then()
        .statusCode(200)
        .body("data.campus.campus", is("JOINVILLE"));
  }

  @Test
  @TestSecurity(
      user = "admin",
      roles = {"ADMIN"})
  @DisplayName("PATCH /v1/identity/admins/{id} - Success")
  void deactivateSuccess() throws Exception {
    Account[] acc = new Account[1];
    doInTransaction(
        () -> {
          User user = factory.createUser();
          acc[0] = factory.createAccount(user, AccountType.ADMIN);
          factory.createAdmin(acc[0]);
        });

    given()
        .contentType(ContentType.JSON)
        .pathParam("id", acc[0].getId())
        .body(anAdminUpdateRequest().withName(null).withCampus(null).withActive(false).build())
        .when()
        .patch("/v1/identity/admins/{id}")
        .then()
        .statusCode(200);
  }

  @Test
  @TestSecurity(
      user = "admin",
      roles = {"ADMIN"})
  @DisplayName("DELETE /v1/identity/admins/{id} - Success")
  void deleteSuccess() throws Exception {
    Account[] acc = new Account[1];
    doInTransaction(
        () -> {
          User user = factory.createUser();
          acc[0] = factory.createAccount(user, AccountType.ADMIN);
          factory.createAdmin(acc[0]);
        });

    given()
        .pathParam("id", acc[0].getId())
        .when()
        .delete("/v1/identity/admins/{id}")
        .then()
        .statusCode(200);
  }

  @Test
  @TestSecurity(
      user = "admin",
      roles = {"ADMIN"})
  @DisplayName("GET /v1/identity/admins/me - Success")
  void getMeSuccess() throws Exception {
    Account[] acc = new Account[1];
    doInTransaction(
        () -> {
          User user = factory.createUser();
          acc[0] = factory.createAccount(user, AccountType.ADMIN);
          factory.createAdmin(acc[0]);
        });

    when(authService.getCurrentAccountId()).thenReturn(acc[0].getId());

    given()
        .when()
        .get("/v1/identity/admins/me")
        .then()
        .statusCode(200)
        .body("data.accountResponse.id", is(acc[0].getId().toString()));
  }

  @Test
  @TestSecurity(
      user = "admin",
      roles = {"ADMIN"})
  @DisplayName("GET /v1/identity/admins/{id} - Success")
  void getByIdSuccess() throws Exception {
    Account[] acc = new Account[1];
    doInTransaction(
        () -> {
          User user = factory.createUser();
          acc[0] = factory.createAccount(user, AccountType.ADMIN);
          factory.createAdmin(acc[0]);
        });

    given()
        .pathParam("id", acc[0].getId())
        .when()
        .get("/v1/identity/admins/{id}")
        .then()
        .statusCode(200)
        .body("data.accountResponse.id", is(acc[0].getId().toString()));
  }

  @Test
  @TestSecurity(
      user = "admin",
      roles = {"ADMIN"})
  @DisplayName("GET /v1/identity/admins?email= - Success")
  void getByEmailSuccess() throws Exception {
    Account[] acc = new Account[1];
    doInTransaction(
        () -> {
          User user = factory.createUser();
          acc[0] = factory.createAccount(user, AccountType.ADMIN);
          factory.createAdmin(acc[0]);
        });

    given()
        .queryParam("email", acc[0].getEmail().getValue())
        .when()
        .get("/v1/identity/admins")
        .then()
        .statusCode(200)
        .body("data.accountResponse.email", is(acc[0].getEmail().getValue()));
  }

  @Test
  @TestSecurity(
      user = "admin",
      roles = {"ADMIN"})
  @DisplayName("GET /v1/identity/admins - List All")
  void listAdmins() throws Exception {
    doInTransaction(
        () -> {
          User user = factory.createUser();
          Account acc = factory.createAccount(user, AccountType.ADMIN);
          factory.createAdmin(acc);
        });

    given()
        .when()
        .get("/v1/identity/admins")
        .then()
        .statusCode(200)
        .body("data", hasSize(greaterThanOrEqualTo(1)));
  }

  @Test
  @TestSecurity(
      user = "admin",
      roles = {"ADMIN"})
  @DisplayName("GET /v1/identity/admins?cpf= - Success")
  void listByCpfSuccess() throws Exception {
    User[] user = new User[1];
    doInTransaction(
        () -> {
          user[0] = factory.createUser();
          Account acc = factory.createAccount(user[0], AccountType.ADMIN);
          factory.createAdmin(acc);
        });

    given()
        .queryParam("cpf", user[0].getCpf().getValue())
        .when()
        .get("/v1/identity/admins")
        .then()
        .statusCode(200)
        .body("data[0].accountResponse.userId", is(user[0].getId().toString()));
  }
}
