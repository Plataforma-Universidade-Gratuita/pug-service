package br.org.catolicasc.pug.project.presenter;

import static br.org.catolicasc.pug.helpers.builders.requests.ProjectCreateRequestBuilder.aProjectCreateRequest;
import static br.org.catolicasc.pug.helpers.builders.requests.ProjectUpdateRequestBuilder.aProjectUpdateRequest;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Mockito.when;

import br.org.catolicasc.pug.helpers.BaseResourceTest;
import br.org.catolicasc.pug.identity.domain.Account;
import br.org.catolicasc.pug.identity.service.AuthService;
import br.org.catolicasc.pug.partner.domain.Entity;
import br.org.catolicasc.pug.project.domain.Project;
import br.org.catolicasc.pug.shared.domain.enums.AccountType;
import com.github.f4b6a3.uuid.UuidCreator;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@QuarkusTest
@DisplayName("ProjectResource Integration Tests")
class ProjectResourceTest extends BaseResourceTest {

  @InjectMock AuthService authService;

  @Test
  @TestSecurity(
      user = "admin",
      roles = {"ADMIN"})
  @DisplayName("GET /projects/{id} - Success")
  void getByIdSuccess() throws Exception {
    Project[] project = new Project[1];
    doInTransaction(
        () -> {
          Entity entity = factory.createEntity(factory.getAnyCity());
          Account creator = factory.createAccount(factory.createUser(), AccountType.PARTNER);
          project[0] = factory.createProject(entity, creator);
        });

    given()
        .pathParam("id", project[0].getId())
        .when()
        .get("/projects/{id}")
        .then()
        .statusCode(200)
        .body("success", is(true))
        .body("data.id", is(project[0].getId().toString()))
        .body("data.name", is(project[0].getName()));
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
    doInTransaction(
        () -> {
          Entity entity = factory.createEntity(factory.getAnyCity());
          Account creator = factory.createAccount(factory.createUser(), AccountType.PARTNER);
          factory.createProject(entity, creator);
        });

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
    Entity[] entity = new Entity[1];
    doInTransaction(
        () -> {
          entity[0] = factory.createEntity(factory.getAnyCity());
          Account creator = factory.createAccount(factory.createUser(), AccountType.PARTNER);
          factory.createProject(entity[0], creator);
        });

    given()
        .queryParam("entityId", entity[0].getId().toString())
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
    Account[] staffAccount = new Account[1];
    Entity[] entity = new Entity[1];
    doInTransaction(
        () -> {
          entity[0] = factory.createEntity(factory.getAnyCity());
          staffAccount[0] = factory.createAccount(factory.createUser(), AccountType.PARTNER);
        });

    when(authService.getCurrentAccountId()).thenReturn(staffAccount[0].getId());

    var req = aProjectCreateRequest().withEntityId(entity[0].getId()).build();

    given()
        .contentType(ContentType.JSON)
        .body(req)
        .when()
        .post("/projects")
        .then()
        .statusCode(201)
        .body("data.name", notNullValue())
        .body("data.entityId", is(entity[0].getId().toString()));
  }

  @Test
  @TestSecurity(
      user = "staff",
      roles = {"STAFF"})
  @DisplayName("PUT /projects/{id} - Success")
  void updateSuccess() throws Exception {
    Project[] project = new Project[1];
    doInTransaction(
        () -> {
          Entity entity = factory.createEntity(factory.getAnyCity());
          Account creator = factory.createAccount(factory.createUser(), AccountType.PARTNER);
          project[0] = factory.createProject(entity, creator);
        });

    var req = aProjectUpdateRequest().withDescription("Updated Description").build();

    given()
        .contentType(ContentType.JSON)
        .pathParam("id", project[0].getId())
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
    var req = aProjectUpdateRequest().withName("Name").build();

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
    Project[] project = new Project[1];
    doInTransaction(
        () -> {
          Entity entity = factory.createEntity(factory.getAnyCity());
          Account creator = factory.createAccount(factory.createUser(), AccountType.PARTNER);
          project[0] = factory.createProject(entity, creator);
        });

    given()
        .pathParam("id", project[0].getId())
        .when()
        .delete("/projects/{id}")
        .then()
        .statusCode(200);
  }

  @Test
  @DisplayName("Should return 401 when accessing without authentication")
  void unauthorizedAccess() {
    assertUnauthenticated("/projects");
  }

  @Test
  @TestSecurity(
      user = "student",
      roles = {"STUDENT"})
  @DisplayName("POST /projects - Forbidden for STUDENT")
  void createForbiddenForStudent() {
    var req = aProjectCreateRequest().build();

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
    Project[] project = new Project[1];
    doInTransaction(
        () -> {
          Entity entity = factory.createEntity(factory.getAnyCity());
          Account creator = factory.createAccount(factory.createUser(), AccountType.PARTNER);
          project[0] = factory.createProject(entity, creator);
        });

    given()
        .pathParam("id", project[0].getId())
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
    Project[] project = new Project[1];
    doInTransaction(
        () -> {
          Entity entity = factory.createEntity(factory.getAnyCity());
          Account creator = factory.createAccount(factory.createUser(), AccountType.PARTNER);
          project[0] = factory.createProject(entity, creator);
        });

    given()
        .pathParam("id", project[0].getId())
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
    Project[] project = new Project[1];
    doInTransaction(
        () -> {
          Entity entity = factory.createEntity(factory.getAnyCity());
          Account creator = factory.createAccount(factory.createUser(), AccountType.PARTNER);
          project[0] = factory.createProject(entity, creator);
          project[0] = project[0].start();
          em.merge(br.org.catolicasc.pug.project.infra.ProjectMapper.toEntity(project[0]));
        });

    given()
        .pathParam("id", project[0].getId())
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
    Account[] creator = new Account[1];
    doInTransaction(
        () -> {
          Entity entity = factory.createEntity(factory.getAnyCity());
          creator[0] = factory.createAccount(factory.createUser(), AccountType.PARTNER);
          factory.createProject(entity, creator[0]);
        });

    given()
        .pathParam("accountId", creator[0].getId())
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
    Project[] project = new Project[1];
    doInTransaction(
        () -> {
          Entity entity = factory.createEntity(factory.getAnyCity());
          Account creator = factory.createAccount(factory.createUser(), AccountType.PARTNER);
          project[0] = factory.createProject(entity, creator).start();
          em.merge(br.org.catolicasc.pug.project.infra.ProjectMapper.toEntity(project[0]));
        });

    given()
        .pathParam("id", project[0].getId())
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
    Project[] project = new Project[1];
    doInTransaction(
        () -> {
          Entity entity = factory.createEntity(factory.getAnyCity());
          Account creator = factory.createAccount(factory.createUser(), AccountType.PARTNER);
          project[0] = factory.createProject(entity, creator).start().putOnHold();
          em.merge(br.org.catolicasc.pug.project.infra.ProjectMapper.toEntity(project[0]));
        });

    given()
        .pathParam("id", project[0].getId())
        .when()
        .patch("/projects/{id}/retake")
        .then()
        .statusCode(200)
        .body("data.status", is("IN_PROGRESS"));
  }
}
