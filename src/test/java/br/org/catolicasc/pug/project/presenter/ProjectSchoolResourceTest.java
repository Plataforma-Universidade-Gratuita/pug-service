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
@DisplayName("ProjectsSchoolsResource Integration Tests")
class ProjectSchoolResourceTest extends BaseResourceTest {

  private record Graph(Project project, School school) {}

  private Graph createGraph() throws Exception {
    Project[] project = new Project[1];
    School[] school = new School[1];
    doInTransaction(
        () -> {
          Entity entity = factory.createEntity(factory.getAnyCity());
          Account creator = factory.createAccount(factory.createUser(), AccountType.PARTNER);
          project[0] = factory.createProject(entity, creator);
          school[0] = factory.createSchool();
        });
    return new Graph(project[0], school[0]);
  }

  @Test
  @TestSecurity(user = "admin", roles = {"ADMIN"})
  void createAssociations() throws Exception {
    Graph g = createGraph();

    given()
        .contentType(ContentType.JSON)
        .pathParam("projectId", g.project().getId())
        .body(aProjectSchoolRequest().withAreaOfExpertiseIds(List.of(g.school().getId())).build())
        .when()
        .post("/v1/projects/{projectId}/areas-of-expertise")
        .then()
        .statusCode(204);
  }

  @Test
  @TestSecurity(user = "admin", roles = {"ADMIN"})
  void listAreasOfExpertiseByProjectId() throws Exception {
    Graph g = createGraph();
    createAssociation(g);

    given()
        .pathParam("projectId", g.project().getId())
        .when()
        .get("/v1/projects/{projectId}/areas-of-expertise")
        .then()
        .statusCode(200)
        .body("data", hasSize(greaterThanOrEqualTo(1)));
  }

  @Test
  @TestSecurity(user = "admin", roles = {"ADMIN"})
  void listProjectsBySchoolId() throws Exception {
    Graph g = createGraph();
    createAssociation(g);

    given()
        .pathParam("schoolId", g.school().getId())
        .when()
        .get("/v1/academic/areas-of-expertise/{schoolId}/projects")
        .then()
        .statusCode(200)
        .body("data", hasSize(greaterThanOrEqualTo(1)));
  }

  @Test
  void unauthorizedAccess() {
    given()
        .pathParam("projectId", UuidCreator.getTimeOrderedEpoch())
        .when()
        .get("/v1/projects/{projectId}/areas-of-expertise")
        .then()
        .statusCode(401);
  }

  private void createAssociation(Graph g) {
    given()
        .contentType(ContentType.JSON)
        .pathParam("projectId", g.project().getId())
        .body(aProjectSchoolRequest().withAreaOfExpertiseIds(List.of(g.school().getId())).build())
        .post("/v1/projects/{projectId}/areas-of-expertise");
  }
}
