package br.org.catolicasc.pug.project.presenter;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;

import br.org.catolicasc.pug.academic.domain.AreaOfExpertise;
import br.org.catolicasc.pug.academic.domain.Course;
import br.org.catolicasc.pug.academic.domain.FormerStudent;
import br.org.catolicasc.pug.helpers.BaseResourceTest;
import br.org.catolicasc.pug.identity.domain.Account;
import br.org.catolicasc.pug.identity.service.AuthService;
import br.org.catolicasc.pug.partner.domain.Entity;
import br.org.catolicasc.pug.project.domain.Enrollment;
import br.org.catolicasc.pug.project.domain.Project;
import br.org.catolicasc.pug.project.domain.enums.EnrollmentStatus;
import br.org.catolicasc.pug.project.infra.EnrollmentMapper;
import br.org.catolicasc.pug.project.presenter.dtos.enrollments.EnrollmentComplexSearchRequest;
import br.org.catolicasc.pug.project.presenter.dtos.enrollments.EnrollmentUpdateStatusRequest;
import br.org.catolicasc.pug.shared.domain.enums.AccountType;
import com.github.f4b6a3.uuid.UuidCreator;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import io.restassured.http.ContentType;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@QuarkusTest
@DisplayName("EnrollmentResource Integration Tests")
class EnrollmentResourceTest extends BaseResourceTest {

  @InjectMock AuthService authService;

  @Test
  @TestSecurity(
      user = "admin",
      roles = {"ADMIN"})
  @DisplayName("GET /v1/projects/{projectId}/enrollments/{formerStudentId} - Success")
  void getByIdsSuccess() throws Exception {
    FormerStudent[] formerStudent = new FormerStudent[1];
    Project[] project = new Project[1];
    doInTransaction(
        () -> {
          AreaOfExpertise areaOfExpertise = factory.createAreaOfExpertise();
          Course course = factory.createCourse(areaOfExpertise);
          Account acc = factory.createAccount(factory.createUser(), AccountType.FORMER_STUDENT);
          formerStudent[0] = factory.createStudent(acc, course);
          Entity entity = factory.createEntity(factory.getAnyCity());
          Account creator = factory.createAccount(factory.createUser(), AccountType.PARTNER);
          project[0] = factory.createProject(entity, creator);
          factory.createEnrollment(formerStudent[0], project[0]);
        });

    given()
        .pathParam("projectId", project[0].getId())
        .pathParam("formerStudentId", formerStudent[0].getAccountId())
        .when()
        .get("/v1/projects/{projectId}/enrollments/{formerStudentId}")
        .then()
        .statusCode(200)
        .body("data.projectId", is(project[0].getId().toString()));
  }

  @Test
  @TestSecurity(
      user = "admin",
      roles = {"ADMIN"})
  @DisplayName("GET /v1/projects/{projectId}/enrollments/{formerStudentId} - Not Found")
  void getByIdsNotFound() {
    given()
        .pathParam("projectId", UuidCreator.getTimeOrderedEpoch())
        .pathParam("formerStudentId", UuidCreator.getTimeOrderedEpoch())
        .when()
        .get("/v1/projects/{projectId}/enrollments/{formerStudentId}")
        .then()
        .statusCode(404);
  }

  @Test
  @TestSecurity(
      user = "admin",
      roles = {"ADMIN"})
  @DisplayName("GET /v1/projects/enrollments - List All")
  void listAll() throws Exception {
    doInTransaction(
        () -> {
          AreaOfExpertise areaOfExpertise = factory.createAreaOfExpertise();
          Course course = factory.createCourse(areaOfExpertise);
          Account acc = factory.createAccount(factory.createUser(), AccountType.FORMER_STUDENT);
          FormerStudent formerStudent = factory.createStudent(acc, course);
          Entity entity = factory.createEntity(factory.getAnyCity());
          Account creator = factory.createAccount(factory.createUser(), AccountType.PARTNER);
          Project project = factory.createProject(entity, creator);
          factory.createEnrollment(formerStudent, project);
        });

    given()
        .when()
        .get("/v1/projects/enrollments")
        .then()
        .statusCode(200)
        .body("data", hasSize(greaterThanOrEqualTo(1)));
  }

  @Test
  @TestSecurity(
      user = "admin",
      roles = {"ADMIN"})
  @DisplayName("GET /v1/projects/enrollments?projectId={projectId} - List By Project")
  void listByProjectId() throws Exception {
    Project[] project = new Project[1];
    doInTransaction(
        () -> {
          AreaOfExpertise areaOfExpertise = factory.createAreaOfExpertise();
          Course course = factory.createCourse(areaOfExpertise);
          Account acc = factory.createAccount(factory.createUser(), AccountType.FORMER_STUDENT);
          FormerStudent formerStudent = factory.createStudent(acc, course);
          Entity entity = factory.createEntity(factory.getAnyCity());
          Account creator = factory.createAccount(factory.createUser(), AccountType.PARTNER);
          project[0] = factory.createProject(entity, creator);
          factory.createEnrollment(formerStudent, project[0]);
        });

    given()
        .queryParam("projectId", project[0].getId())
        .when()
        .get("/v1/projects/enrollments")
        .then()
        .statusCode(200)
        .body("data", hasSize(greaterThanOrEqualTo(1)));
  }

  @Test
  @TestSecurity(
      user = "admin",
      roles = {"ADMIN"})
  @DisplayName(
      "GET /v1/projects/enrollments?formerStudentId={formerStudentId} - List By FormerStudent")
  void listByStudentId() throws Exception {
    FormerStudent[] formerStudent = new FormerStudent[1];
    doInTransaction(
        () -> {
          AreaOfExpertise areaOfExpertise = factory.createAreaOfExpertise();
          Course course = factory.createCourse(areaOfExpertise);
          Account acc = factory.createAccount(factory.createUser(), AccountType.FORMER_STUDENT);
          formerStudent[0] = factory.createStudent(acc, course);
          Entity entity = factory.createEntity(factory.getAnyCity());
          Account creator = factory.createAccount(factory.createUser(), AccountType.PARTNER);
          Project project = factory.createProject(entity, creator);
          factory.createEnrollment(formerStudent[0], project);
        });

    given()
        .queryParam("formerStudentId", formerStudent[0].getAccountId())
        .when()
        .get("/v1/projects/enrollments")
        .then()
        .statusCode(200)
        .body("data", hasSize(greaterThanOrEqualTo(1)));
  }

  @Test
  @TestSecurity(
      user = "formerStudent",
      roles = {"STUDENT"})
  @DisplayName("GET /v1/projects/{projectId}/enrollments/me - Success")
  void getMineSuccess() throws Exception {
    Account[] acc = new Account[1];
    Project[] project = new Project[1];
    doInTransaction(
        () -> {
          AreaOfExpertise areaOfExpertise = factory.createAreaOfExpertise();
          Course course = factory.createCourse(areaOfExpertise);
          acc[0] = factory.createAccount(factory.createUser(), AccountType.FORMER_STUDENT);
          FormerStudent formerStudent = factory.createStudent(acc[0], course);
          Entity entity = factory.createEntity(factory.getAnyCity());
          Account creator = factory.createAccount(factory.createUser(), AccountType.PARTNER);
          project[0] = factory.createProject(entity, creator);
          factory.createEnrollment(formerStudent, project[0]);
        });

    when(authService.getCurrentAccountId()).thenReturn(acc[0].getId());

    given()
        .pathParam("projectId", project[0].getId())
        .when()
        .get("/v1/projects/{projectId}/enrollments/me")
        .then()
        .statusCode(200)
        .body("data.projectId", is(project[0].getId().toString()));
  }

  @Test
  @TestSecurity(
      user = "formerStudent",
      roles = {"STUDENT"})
  @DisplayName("GET /v1/projects/enrollments/me - Success")
  void listMineSuccess() throws Exception {
    Account[] acc = new Account[1];
    doInTransaction(
        () -> {
          AreaOfExpertise areaOfExpertise = factory.createAreaOfExpertise();
          Course course = factory.createCourse(areaOfExpertise);
          acc[0] = factory.createAccount(factory.createUser(), AccountType.FORMER_STUDENT);
          FormerStudent formerStudent = factory.createStudent(acc[0], course);
          Entity entity = factory.createEntity(factory.getAnyCity());
          Account creator = factory.createAccount(factory.createUser(), AccountType.PARTNER);
          Project project = factory.createProject(entity, creator);
          factory.createEnrollment(formerStudent, project);
        });

    when(authService.getCurrentAccountId()).thenReturn(acc[0].getId());

    given()
        .when()
        .get("/v1/projects/enrollments/me")
        .then()
        .statusCode(200)
        .body("data", hasSize(greaterThanOrEqualTo(1)));
  }

  @Test
  @TestSecurity(
      user = "admin",
      roles = {"ADMIN"})
  @DisplayName("POST /v1/projects/enrollments/search - Success")
  void searchSuccess() throws Exception {
    FormerStudent[] formerStudent = new FormerStudent[1];
    Project[] project = new Project[1];
    doInTransaction(
        () -> {
          AreaOfExpertise areaOfExpertise = factory.createAreaOfExpertise();
          Course course = factory.createCourse(areaOfExpertise);
          Account acc = factory.createAccount(factory.createUser(), AccountType.FORMER_STUDENT);
          formerStudent[0] = factory.createStudent(acc, course);
          Entity entity = factory.createEntity(factory.getAnyCity());
          Account creator = factory.createAccount(factory.createUser(), AccountType.PARTNER);
          project[0] = factory.createProject(entity, creator);
          factory.createEnrollment(formerStudent[0], project[0]);
        });

    given()
        .contentType(ContentType.JSON)
        .queryParam("page", 0)
        .queryParam("size", 25)
        .body(
            new EnrollmentComplexSearchRequest(
                List.of(project[0].getId()),
                List.of(formerStudent[0].getAccountId()),
                List.of(EnrollmentStatus.PENDING),
                null,
                null,
                null,
                null))
        .when()
        .post("/v1/projects/enrollments/search")
        .then()
        .statusCode(200)
        .body("data.content", hasSize(greaterThanOrEqualTo(1)))
        .body("data.content[0].status.status", is("PENDING"));
  }

  @Test
  @TestSecurity(
      user = "admin",
      roles = {"ADMIN"})
  @DisplayName("POST /v1/projects/{projectId}/enrollments - Success")
  void createSuccess() throws Exception {
    Account[] acc = new Account[1];
    Project[] project = new Project[1];
    doInTransaction(
        () -> {
          AreaOfExpertise areaOfExpertise = factory.createAreaOfExpertise();
          Course course = factory.createCourse(areaOfExpertise);
          acc[0] = factory.createAccount(factory.createUser(), AccountType.FORMER_STUDENT);
          factory.createStudent(acc[0], course);
          Entity entity = factory.createEntity(factory.getAnyCity());
          Account creator = factory.createAccount(factory.createUser(), AccountType.PARTNER);
          project[0] = factory.createProject(entity, creator);
        });

    when(authService.getCurrentAccountType()).thenReturn(AccountType.ADMIN);

    given()
        .pathParam("projectId", project[0].getId())
        .queryParam("formerStudentId", acc[0].getId())
        .when()
        .post("/v1/projects/{projectId}/enrollments")
        .then()
        .statusCode(201);
  }

  @Test
  @TestSecurity(
      user = "admin",
      roles = {"ADMIN"})
  @DisplayName(
      "PATCH /v1/projects/{projectId}/enrollments/{formerStudentId} status=APPROVED - Success")
  void acceptSuccess() throws Exception {
    FormerStudent[] formerStudent = new FormerStudent[1];
    Project[] project = new Project[1];
    doInTransaction(
        () -> {
          AreaOfExpertise areaOfExpertise = factory.createAreaOfExpertise();
          Course course = factory.createCourse(areaOfExpertise);
          Account acc = factory.createAccount(factory.createUser(), AccountType.FORMER_STUDENT);
          formerStudent[0] = factory.createStudent(acc, course);
          Entity entity = factory.createEntity(factory.getAnyCity());
          Account creator = factory.createAccount(factory.createUser(), AccountType.PARTNER);
          project[0] = factory.createProject(entity, creator);
          factory.createEnrollment(formerStudent[0], project[0]);
        });

    given()
        .contentType(ContentType.JSON)
        .pathParam("projectId", project[0].getId())
        .pathParam("formerStudentId", formerStudent[0].getAccountId())
        .body(new EnrollmentUpdateStatusRequest(EnrollmentStatus.APPROVED))
        .when()
        .patch("/v1/projects/{projectId}/enrollments/{formerStudentId}")
        .then()
        .statusCode(200)
        .body("data.status.status", is("APPROVED"));
  }

  @Test
  @TestSecurity(
      user = "admin",
      roles = {"ADMIN"})
  @DisplayName(
      "PATCH /v1/projects/{projectId}/enrollments/{formerStudentId} status=CANCELED - Success")
  void cancelSuccess() throws Exception {
    Enrollment[] enr = new Enrollment[1];
    doInTransaction(
        () -> {
          enr[0] = setupApprovedEnrollment();
          em.merge(EnrollmentMapper.toEntity(enr[0]));
        });

    given()
        .contentType(ContentType.JSON)
        .pathParam("projectId", enr[0].getIdentifier().getProjectId())
        .pathParam("formerStudentId", enr[0].getIdentifier().getFormerStudentId())
        .body(new EnrollmentUpdateStatusRequest(EnrollmentStatus.CANCELED))
        .when()
        .patch("/v1/projects/{projectId}/enrollments/{formerStudentId}")
        .then()
        .statusCode(200)
        .body("data.status.status", is("CANCELED"));
  }

  @Test
  @TestSecurity(
      user = "admin",
      roles = {"ADMIN"})
  @DisplayName(
      "PATCH /v1/projects/{projectId}/enrollments/{formerStudentId} status=COMPLETED - Success")
  void completeSuccess() throws Exception {
    Enrollment[] enr = new Enrollment[1];
    doInTransaction(
        () -> {
          enr[0] = setupApprovedEnrollment();
          em.merge(EnrollmentMapper.toEntity(enr[0]));
        });

    given()
        .contentType(ContentType.JSON)
        .pathParam("projectId", enr[0].getIdentifier().getProjectId())
        .pathParam("formerStudentId", enr[0].getIdentifier().getFormerStudentId())
        .body(new EnrollmentUpdateStatusRequest(EnrollmentStatus.COMPLETED))
        .when()
        .patch("/v1/projects/{projectId}/enrollments/{formerStudentId}")
        .then()
        .statusCode(200)
        .body("data.status.status", is("COMPLETED"));
  }

  @Test
  @TestSecurity(
      user = "formerStudent",
      roles = {"STUDENT"})
  @DisplayName("PATCH /v1/projects/{projectId}/enrollments/me status=EXITED - Success")
  void exitSuccess() throws Exception {
    Account[] acc = new Account[1];
    Project[] project = new Project[1];
    doInTransaction(
        () -> {
          AreaOfExpertise areaOfExpertise = factory.createAreaOfExpertise();
          Course course = factory.createCourse(areaOfExpertise);
          acc[0] = factory.createAccount(factory.createUser(), AccountType.FORMER_STUDENT);
          FormerStudent formerStudent = factory.createStudent(acc[0], course);
          Entity entity = factory.createEntity(factory.getAnyCity());
          Account creator = factory.createAccount(factory.createUser(), AccountType.PARTNER);
          project[0] = factory.createProject(entity, creator);
          Enrollment enr =
              factory
                  .createEnrollment(formerStudent, project[0])
                  .changeStatus(EnrollmentStatus.APPROVED);
          em.merge(EnrollmentMapper.toEntity(enr));
        });

    when(authService.getCurrentAccountId()).thenReturn(acc[0].getId());

    given()
        .contentType(ContentType.JSON)
        .pathParam("projectId", project[0].getId())
        .body(new EnrollmentUpdateStatusRequest(EnrollmentStatus.EXITED))
        .when()
        .patch("/v1/projects/{projectId}/enrollments/me")
        .then()
        .statusCode(200)
        .body("data.status.status", is("EXITED"));
  }

  @Test
  @TestSecurity(
      user = "admin",
      roles = {"ADMIN"})
  @DisplayName(
      "PATCH /v1/projects/{projectId}/enrollments/{formerStudentId} status=REJECTED - Success")
  void rejectSuccess() throws Exception {
    FormerStudent[] formerStudent = new FormerStudent[1];
    Project[] project = new Project[1];
    doInTransaction(
        () -> {
          AreaOfExpertise areaOfExpertise = factory.createAreaOfExpertise();
          Course course = factory.createCourse(areaOfExpertise);
          Account acc = factory.createAccount(factory.createUser(), AccountType.FORMER_STUDENT);
          formerStudent[0] = factory.createStudent(acc, course);
          Entity entity = factory.createEntity(factory.getAnyCity());
          Account creator = factory.createAccount(factory.createUser(), AccountType.PARTNER);
          project[0] = factory.createProject(entity, creator);
          factory.createEnrollment(formerStudent[0], project[0]);
        });

    given()
        .contentType(ContentType.JSON)
        .pathParam("projectId", project[0].getId())
        .pathParam("formerStudentId", formerStudent[0].getAccountId())
        .body(new EnrollmentUpdateStatusRequest(EnrollmentStatus.REJECTED))
        .when()
        .patch("/v1/projects/{projectId}/enrollments/{formerStudentId}")
        .then()
        .statusCode(200)
        .body("data.status.status", is("REJECTED"));
  }

  @Test
  @TestSecurity(
      user = "admin",
      roles = {"ADMIN"})
  @DisplayName(
      "PATCH /v1/projects/{projectId}/enrollments/{formerStudentId} status=REMOVED - Success")
  void removeSuccess() throws Exception {
    Enrollment[] enr = new Enrollment[1];
    doInTransaction(
        () -> {
          enr[0] = setupApprovedEnrollment();
          em.merge(EnrollmentMapper.toEntity(enr[0]));
        });

    given()
        .contentType(ContentType.JSON)
        .pathParam("projectId", enr[0].getIdentifier().getProjectId())
        .pathParam("formerStudentId", enr[0].getIdentifier().getFormerStudentId())
        .body(new EnrollmentUpdateStatusRequest(EnrollmentStatus.REMOVED))
        .when()
        .patch("/v1/projects/{projectId}/enrollments/{formerStudentId}")
        .then()
        .statusCode(200)
        .body("data.status.status", is("REMOVED"));
  }

  @Test
  @TestSecurity(
      user = "admin",
      roles = {"ADMIN"})
  @DisplayName("DELETE /v1/projects/{projectId}/enrollments/{formerStudentId} - Success")
  void deleteSuccess() throws Exception {
    FormerStudent[] formerStudent = new FormerStudent[1];
    Project[] project = new Project[1];
    doInTransaction(
        () -> {
          AreaOfExpertise areaOfExpertise = factory.createAreaOfExpertise();
          Course course = factory.createCourse(areaOfExpertise);
          Account acc = factory.createAccount(factory.createUser(), AccountType.FORMER_STUDENT);
          formerStudent[0] = factory.createStudent(acc, course);
          Entity entity = factory.createEntity(factory.getAnyCity());
          Account creator = factory.createAccount(factory.createUser(), AccountType.PARTNER);
          project[0] = factory.createProject(entity, creator);
          factory.createEnrollment(formerStudent[0], project[0]);
        });

    given()
        .pathParam("projectId", project[0].getId())
        .pathParam("formerStudentId", formerStudent[0].getAccountId())
        .when()
        .delete("/v1/projects/{projectId}/enrollments/{formerStudentId}")
        .then()
        .statusCode(204);
  }

  private Enrollment setupApprovedEnrollment() {
    AreaOfExpertise areaOfExpertise = factory.createAreaOfExpertise();
    Course course = factory.createCourse(areaOfExpertise);
    Account acc = factory.createAccount(factory.createUser(), AccountType.FORMER_STUDENT);
    FormerStudent formerStudent = factory.createStudent(acc, course);
    Entity entity = factory.createEntity(factory.getAnyCity());
    Account creator = factory.createAccount(factory.createUser(), AccountType.PARTNER);
    Project project = factory.createProject(entity, creator);
    return factory.createEnrollment(formerStudent, project).changeStatus(EnrollmentStatus.APPROVED);
  }
}
