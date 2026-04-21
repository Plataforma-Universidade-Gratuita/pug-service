package br.org.catolicasc.pug.identity.presenter;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;

import br.org.catolicasc.pug.helpers.TestDataFactory;
import br.org.catolicasc.pug.identity.domain.Account;
import br.org.catolicasc.pug.identity.domain.User;
import br.org.catolicasc.pug.identity.presenter.dtos.AdminCreateRequest;
import br.org.catolicasc.pug.identity.presenter.dtos.AdminUpdateRequest;
import br.org.catolicasc.pug.identity.service.AuthService;
import br.org.catolicasc.pug.shared.domain.enums.AccountType;
import br.org.catolicasc.pug.shared.domain.enums.Campi;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.UserTransaction;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@QuarkusTest
@DisplayName("AdminResource Integration Tests")
class AdminResourceTest {

  @Inject TestDataFactory factory;
  @Inject UserTransaction utx;
  @Inject EntityManager em;

  @InjectMock AuthService authService;

  @Test
  @TestSecurity(
      user = "admin",
      roles = {"ADMIN"})
  @DisplayName("POST /identity/admins - Success")
  void createSuccess() {
    AdminCreateRequest req =
        new AdminCreateRequest(
            "11144477735", "Admin Name", "admin2@pug.com", "password123", Campi.JARAGUA_DO_SUL);

    given()
        .contentType(ContentType.JSON)
        .body(req)
        .when()
        .post("/identity/admins")
        .then()
        .statusCode(201)
        .body("data.accountResponse.email", is("admin2@pug.com"))
        .body("data.campus.campus", is("JARAGUA_DO_SUL"));
  }

  @Test
  @TestSecurity(
      user = "admin",
      roles = {"ADMIN"})
  @DisplayName("PUT /identity/admins/{id} - Success")
  void updateSuccess() throws Exception {
    utx.begin();
    User user = factory.createUser();
    Account acc = factory.createAccount(user, AccountType.ADMIN);
    factory.createAdmin(acc);
    em.flush();
    utx.commit();

    AdminUpdateRequest req = new AdminUpdateRequest(null, null, null, Campi.JOINVILLE);

    given()
        .contentType(ContentType.JSON)
        .pathParam("id", acc.getId())
        .body(req)
        .when()
        .put("/identity/admins/{id}")
        .then()
        .statusCode(200)
        .body("data.campus.campus", is("JOINVILLE"));
  }

  @Test
  @TestSecurity(
      user = "admin",
      roles = {"ADMIN"})
  @DisplayName("PATCH /identity/admins/{id}/deactivate - Success")
  void deactivateSuccess() throws Exception {
    utx.begin();
    User user = factory.createUser();
    Account acc = factory.createAccount(user, AccountType.ADMIN);
    factory.createAdmin(acc);
    em.flush();
    utx.commit();

    given()
        .pathParam("id", acc.getId())
        .when()
        .patch("/identity/admins/{id}/deactivate")
        .then()
        .statusCode(200);
  }

  @Test
  @TestSecurity(
      user = "admin",
      roles = {"ADMIN"})
  @DisplayName("DELETE /identity/admins/{id} - Success")
  void deleteSuccess() throws Exception {
    utx.begin();
    User user = factory.createUser();
    Account acc = factory.createAccount(user, AccountType.ADMIN);
    factory.createAdmin(acc);
    em.flush();
    utx.commit();

    given()
        .pathParam("id", acc.getId())
        .when()
        .delete("/identity/admins/{id}")
        .then()
        .statusCode(200);
  }

  @Test
  @TestSecurity(
      user = "admin",
      roles = {"ADMIN"})
  @DisplayName("GET /identity/admins/me - Success")
  void getMeSuccess() throws Exception {
    utx.begin();
    User user = factory.createUser();
    Account acc = factory.createAccount(user, AccountType.ADMIN);
    factory.createAdmin(acc);
    utx.commit();

    when(authService.getCurrentAccountId()).thenReturn(acc.getId());

    given()
        .when()
        .get("/identity/admins/me")
        .then()
        .statusCode(200)
        .body("data.accountResponse.id", is(acc.getId().toString()));
  }
}
