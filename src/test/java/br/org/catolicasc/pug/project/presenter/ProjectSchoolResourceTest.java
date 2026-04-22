package br.org.catolicasc.pug.project.presenter;

import static br.org.catolicasc.pug.helpers.builders.requests.ProjectSchoolRequestBuilder.aProjectSchoolRequest;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasSize;

import br.org.catolicasc.pug.academic.domain.School;
import br.org.catolicasc.pug.helpers.BaseResourceTest;
import br.org.catolicasc.pug.identity.domain.Account;
import br.org.catolicasc.pug.partner.domain.Entity;
import br.org.catolicasc.pug.project.domain.Project;
import br.org.catolicasc.pug.shared.domain.enums.AccountType;
import com.github.f4b6a3.uuid.UuidCreator;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import io.restassured.http.ContentType;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@QuarkusTest
@DisplayName("ProjectSchoolResource Integration Tests")
class ProjectSchoolResourceTest extends BaseResourceTest {

  private record ProjectSchoolGraph(Project project, School school) {}

  private ProjectSchoolGraph createProjectSchoolGraph() throws Exception {
    Project[] project = new Project[1];
    School[] school = new School[1];
    doInTransaction(
        () -> {
          Entity entity = factory.createEntity(factory.getAnyCity());
          Account creator = factory.createAccount(factory.createUser(), AccountType.PARTNER);
          project[0] = factory.createProject(entity, creator);
          school[0] = factory.createSchool();
        });
    return new ProjectSchoolGraph(project[0], school[0]);
  }

  @Test
  @TestSecurity(
      user = "admin",
      roles = {"ADMIN"})
  @DisplayName("POST /projects/by-school - Create Associations")
  void createAssociations() throws Exception {
    ProjectSchoolGraph g = createProjectSchoolGraph();

    var req =
        aProjectSchoolRequest()
            .withProjectId(g.project().getId())
            .withSchoolIds(List.of(g.school().getId()))
            .build();

    given()
        .contentType(ContentType.JSON)
        .body(req)
        .when()
        .post("/projects/by-school")
        .then()
        .statusCode(201)
        .body("data", hasSize(greaterThanOrEqualTo(1)));
  }

  @Test
  @TestSecurity(
      user = "admin",
      roles = {"ADMIN"})
  @DisplayName("GET /projects/by-school/projects/{projectId}/schools - List Schools")
  void listSchoolsByProjectId() throws Exception {
    ProjectSchoolGraph g = createProjectSchoolGraph();
    createAssociation(g);

    given()
        .pathParam("projectId", g.project().getId())
        .when()
        .get("/projects/by-school/projects/{projectId}/schools")
        .then()
        .statusCode(200)
        .body("data", hasSize(greaterThanOrEqualTo(1)));
  }

  @Test
  @TestSecurity(
      user = "admin",
      roles = {"ADMIN"})
  @DisplayName("GET /projects/by-school/schools/{schoolId}/projects - List Projects")
  void listProjectsBySchoolId() throws Exception {
    ProjectSchoolGraph g = createProjectSchoolGraph();
    createAssociation(g);

    given()
        .pathParam("schoolId", g.school().getId())
        .when()
        .get("/projects/by-school/schools/{schoolId}/projects")
        .then()
        .statusCode(200)
        .body("data", hasSize(greaterThanOrEqualTo(1)));
  }

  @Test
  @TestSecurity(
      user = "admin",
      roles = {"ADMIN"})
  @DisplayName("DELETE /projects/by-school/projects/{projectId}/schools/{schoolId} - Success")
  void deleteAssociation() throws Exception {
    ProjectSchoolGraph g = createProjectSchoolGraph();
    createAssociation(g);

    given()
        .pathParam("projectId", g.project().getId())
        .pathParam("schoolId", g.school().getId())
        .when()
        .delete("/projects/by-school/projects/{projectId}/schools/{schoolId}")
        .then()
        .statusCode(200);
  }

  @Test
  @TestSecurity(
      user = "admin",
      roles = {"ADMIN"})
  @DisplayName("DELETE /projects/by-school/projects/{projectId} - Delete All by Project")
  void deleteAllByProject() throws Exception {
    ProjectSchoolGraph g = createProjectSchoolGraph();
    createAssociation(g);

    given()
        .pathParam("projectId", g.project().getId())
        .when()
        .delete("/projects/by-school/projects/{projectId}")
        .then()
        .statusCode(200);
  }

  @Test
  @TestSecurity(
      user = "admin",
      roles = {"ADMIN"})
  @DisplayName("DELETE /projects/by-school/schools/{schoolId} - Delete All by School")
  void deleteAllBySchool() throws Exception {
    ProjectSchoolGraph g = createProjectSchoolGraph();
    createAssociation(g);

    given()
        .pathParam("schoolId", g.school().getId())
        .when()
        .delete("/projects/by-school/schools/{schoolId}")
        .then()
        .statusCode(200);
  }

  @Test
  @DisplayName("Should return 401 when accessing without authentication")
  void unauthorizedAccess() {
    given()
        .pathParam("projectId", UuidCreator.getTimeOrderedEpoch())
        .when()
        .get("/projects/by-school/projects/{projectId}/schools")
        .then()
        .statusCode(401);
  }

  @Test
  @TestSecurity(
      user = "student",
      roles = {"STUDENT"})
  @DisplayName("POST /projects/by-school - Forbidden for STUDENT")
  void createForbiddenForStudent() {
    var req = aProjectSchoolRequest().build();

    given()
        .contentType(ContentType.JSON)
        .body(req)
        .when()
        .post("/projects/by-school")
        .then()
        .statusCode(403);
  }

  private void createAssociation(ProjectSchoolGraph g) {
    var req =
        aProjectSchoolRequest()
            .withProjectId(g.project().getId())
            .withSchoolIds(List.of(g.school().getId()))
            .build();
    given().contentType(ContentType.JSON).body(req).post("/projects/by-school");
  }
}
