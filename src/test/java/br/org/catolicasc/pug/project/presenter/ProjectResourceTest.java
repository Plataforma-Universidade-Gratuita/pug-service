package br.org.catolicasc.pug.project.presenter;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Mockito.when;

import br.org.catolicasc.pug.helpers.TestDataFactory;
import br.org.catolicasc.pug.identity.domain.Account;
import br.org.catolicasc.pug.identity.service.AuthService;
import br.org.catolicasc.pug.partner.domain.Entity;
import br.org.catolicasc.pug.project.domain.Project;
import br.org.catolicasc.pug.project.presenter.dtos.ProjectCreateRequest;
import br.org.catolicasc.pug.project.presenter.dtos.ProjectUpdateRequest;
import br.org.catolicasc.pug.shared.domain.enums.AccountType;
import com.github.f4b6a3.uuid.UuidCreator;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.UserTransaction;
import java.math.BigDecimal;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@QuarkusTest
@DisplayName("ProjectResource Integration Tests")
class ProjectResourceTest {

  @Inject TestDataFactory factory;
  @Inject UserTransaction utx;
  @Inject EntityManager em;
  @InjectMock AuthService authService;

  @Test
  @TestSecurity(
      user = "admin",
      roles = {"ADMIN"})
  @DisplayName("GET /projects/{id} - Success")
  void getByIdSuccess() throws Exception {
    utx.begin();
    Entity entity = factory.createEntity(factory.getAnyCity());
    Account creator = factory.createAccount(factory.createUser(), AccountType.PARTNER);
    Project project = factory.createProject(entity, creator);
    em.flush();
    utx.commit();

    given()
        .pathParam("id", project.getId())
        .when()
        .get("/projects/{id}")
        .then()
        .statusCode(200)
        .body("success", is(true))
        .body("data.id", is(project.getId().toString()))
        .body("data.name", is(project.getName()));
  }

  @Test
  @TestSecurity(
      user = "admin",
      roles = {"ADMIN"})
  @DisplayName("GET /projects/{id} - Not Found")
  void getByIdNotFound() {
    given()
        .pathParam("id", UuidCreator.getTimeOrderedEpoch())
        .when()
        .get("/projects/{id}")
        .then()
        .statusCode(404)
        .body("success", is(false));
  }

  @Test
  @TestSecurity(
      user = "admin",
      roles = {"ADMIN"})
  @DisplayName("GET /projects - List All")
  void listAll() throws Exception {
    utx.begin();
    Entity entity = factory.createEntity(factory.getAnyCity());
    Account creator = factory.createAccount(factory.createUser(), AccountType.PARTNER);
    factory.createProject(entity, creator);
    utx.commit();

    given()
        .when()
        .get("/projects")
        .then()
        .statusCode(200)
        .body("data", hasSize(greaterThanOrEqualTo(1)));
  }

  @Test
  @TestSecurity(
      user = "admin",
      roles = {"ADMIN"})
  @DisplayName("GET /projects?entityId= - Filter by Entity")
  void listByEntityId() throws Exception {
    utx.begin();
    Entity entity = factory.createEntity(factory.getAnyCity());
    Account creator = factory.createAccount(factory.createUser(), AccountType.PARTNER);
    factory.createProject(entity, creator);
    utx.commit();

    given()
        .queryParam("entityId", entity.getId().toString())
        .when()
        .get("/projects")
        .then()
        .statusCode(200)
        .body("data", hasSize(greaterThanOrEqualTo(1)));
  }

  @Test
  @TestSecurity(
      user = "staff",
      roles = {"STAFF"})
  @DisplayName("POST /projects - Success")
  void createSuccess() throws Exception {
    Account staffAccount;
    Entity entity;

    utx.begin();
    entity = factory.createEntity(factory.getAnyCity());
    staffAccount = factory.createAccount(factory.createUser(), AccountType.PARTNER);
    utx.commit();

    when(authService.getCurrentAccountId()).thenReturn(staffAccount.getId());

    var req =
        new ProjectCreateRequest(
            "REST Test Project " + UuidCreator.getTimeOrderedEpoch(),
            entity.getId(),
            "Test description",
            15,
            new BigDecimal("30.00"));

    given()
        .contentType(ContentType.JSON)
        .body(req)
        .when()
        .post("/projects")
        .then()
        .statusCode(201)
        .body("data.name", notNullValue())
        .body("data.entityId", is(entity.getId().toString()));
  }

  @Test
  @TestSecurity(
      user = "staff",
      roles = {"STAFF"})
  @DisplayName("PUT /projects/{id} - Success")
  void updateSuccess() throws Exception {
    utx.begin();
    Entity entity = factory.createEntity(factory.getAnyCity());
    Account creator = factory.createAccount(factory.createUser(), AccountType.PARTNER);
    Project project = factory.createProject(entity, creator);
    em.flush();
    utx.commit();

    var req = new ProjectUpdateRequest(null, "Updated Description", null, null);

    given()
        .contentType(ContentType.JSON)
        .pathParam("id", project.getId())
        .body(req)
        .when()
        .put("/projects/{id}")
        .then()
        .statusCode(200)
        .body("data.description", is("Updated Description"));
  }

  @Test
  @TestSecurity(
      user = "admin",
      roles = {"ADMIN"})
  @DisplayName("PUT /projects/{id} - Not Found")
  void updateNotFound() {
    var req = new ProjectUpdateRequest("Name", null, null, null);

    given()
        .contentType(ContentType.JSON)
        .pathParam("id", UuidCreator.getTimeOrderedEpoch())
        .body(req)
        .when()
        .put("/projects/{id}")
        .then()
        .statusCode(404);
  }

  @Test
  @TestSecurity(
      user = "admin",
      roles = {"ADMIN"})
  @DisplayName("DELETE /projects/{id} - Success")
  void deleteSuccess() throws Exception {
    utx.begin();
    Entity entity = factory.createEntity(factory.getAnyCity());
    Account creator = factory.createAccount(factory.createUser(), AccountType.PARTNER);
    Project project = factory.createProject(entity, creator);
    em.flush();
    utx.commit();

    given().pathParam("id", project.getId()).when().delete("/projects/{id}").then().statusCode(200);
  }

  @Test
  @DisplayName("Should return 401 when accessing without authentication")
  void unauthorizedAccess() {
    given().when().get("/projects").then().statusCode(401);
  }

  @Test
  @TestSecurity(
      user = "student",
      roles = {"STUDENT"})
  @DisplayName("POST /projects - Forbidden for STUDENT")
  void createForbiddenForStudent() {
    var req =
        new ProjectCreateRequest(
            "Forbidden", UuidCreator.getTimeOrderedEpoch(), "desc", 10, new BigDecimal("10"));

    given().contentType(ContentType.JSON).body(req).when().post("/projects").then().statusCode(403);
  }

  @Test
  @TestSecurity(
      user = "student",
      roles = {"STUDENT"})
  @DisplayName("DELETE /projects/{id} - Forbidden for STUDENT")
  void deleteForbiddenForStudent() {
    given()
        .pathParam("id", UuidCreator.getTimeOrderedEpoch())
        .when()
        .delete("/projects/{id}")
        .then()
        .statusCode(403);
  }

  @Test
  @TestSecurity(
      user = "admin",
      roles = {"ADMIN"})
  @DisplayName("PATCH /projects/{id}/start - Success")
  void startSuccess() throws Exception {
    utx.begin();
    Entity entity = factory.createEntity(factory.getAnyCity());
    Account creator = factory.createAccount(factory.createUser(), AccountType.PARTNER);
    Project project = factory.createProject(entity, creator);
    em.flush();
    utx.commit();

    given()
        .pathParam("id", project.getId())
        .when()
        .patch("/projects/{id}/start")
        .then()
        .statusCode(200)
        .body("data.status", is("IN_PROGRESS"));
  }

  @Test
  @TestSecurity(
      user = "admin",
      roles = {"ADMIN"})
  @DisplayName("PATCH /projects/{id}/cancel - Success")
  void cancelSuccess() throws Exception {
    utx.begin();
    Entity entity = factory.createEntity(factory.getAnyCity());
    Account creator = factory.createAccount(factory.createUser(), AccountType.PARTNER);
    Project project = factory.createProject(entity, creator);
    em.flush();
    utx.commit();

    given()
        .pathParam("id", project.getId())
        .when()
        .patch("/projects/{id}/cancel")
        .then()
        .statusCode(200)
        .body("data.status", is("CANCELED"));
  }

  @Test
  @TestSecurity(
      user = "admin",
      roles = {"ADMIN"})
  @DisplayName("PATCH /projects/{id}/complete - Success")
  void completeSuccess() throws Exception {
    utx.begin();
    Entity entity = factory.createEntity(factory.getAnyCity());
    Account creator = factory.createAccount(factory.createUser(), AccountType.PARTNER);
    Project project = factory.createProject(entity, creator);
    project = project.start(); // Precisa estar em IN_PROGRESS para completar
    em.merge(br.org.catolicasc.pug.project.infra.ProjectMapper.toEntity(project));
    em.flush();
    utx.commit();

    given()
        .pathParam("id", project.getId())
        .when()
        .patch("/projects/{id}/complete")
        .then()
        .statusCode(200)
        .body("data.status", is("COMPLETED"));
  }

  @Test
  @TestSecurity(
      user = "staff",
      roles = {"STAFF"})
  @DisplayName("GET /projects/created-by/{accountId} - Success")
  void listByCreatedBy() throws Exception {
    utx.begin();
    Entity entity = factory.createEntity(factory.getAnyCity());
    Account creator = factory.createAccount(factory.createUser(), AccountType.PARTNER);
    factory.createProject(entity, creator);
    em.flush();
    utx.commit();

    given()
        .pathParam("accountId", creator.getId())
        .when()
        .get("/projects/created-by/{accountId}")
        .then()
        .statusCode(200)
        .body("data", hasSize(greaterThanOrEqualTo(1)));
  }

  @Test
  @TestSecurity(
      user = "admin",
      roles = {"ADMIN"})
  @DisplayName("PATCH /projects/{id}/hold - Success")
  void holdSuccess() throws Exception {
    utx.begin();
    Entity entity = factory.createEntity(factory.getAnyCity());
    Account creator = factory.createAccount(factory.createUser(), AccountType.PARTNER);
    Project project = factory.createProject(entity, creator).start();
    em.merge(br.org.catolicasc.pug.project.infra.ProjectMapper.toEntity(project));
    em.flush();
    utx.commit();

    given()
        .pathParam("id", project.getId())
        .when()
        .patch("/projects/{id}/hold")
        .then()
        .statusCode(200)
        .body("data.status", is("ON_HOLD"));
  }

  @Test
  @TestSecurity(
      user = "admin",
      roles = {"ADMIN"})
  @DisplayName("PATCH /projects/{id}/retake - Success")
  void retakeSuccess() throws Exception {
    utx.begin();
    Entity entity = factory.createEntity(factory.getAnyCity());
    Account creator = factory.createAccount(factory.createUser(), AccountType.PARTNER);
    Project project = factory.createProject(entity, creator).start().putOnHold();
    em.merge(br.org.catolicasc.pug.project.infra.ProjectMapper.toEntity(project));
    em.flush();
    utx.commit();

    given()
        .pathParam("id", project.getId())
        .when()
        .patch("/projects/{id}/retake")
        .then()
        .statusCode(200)
        .body("data.status", is("IN_PROGRESS"));
  }
}
