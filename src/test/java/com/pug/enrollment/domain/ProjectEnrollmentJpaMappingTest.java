package com.pug.enrollment.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.pug.academic.domain.Course;
import com.pug.academic.domain.FieldOfStudy;
import com.pug.academic.domain.Student;
import com.pug.enrollment.domain.enums.ProjectEnrollmentStatus;
import com.pug.geo.domain.City;
import com.pug.identity.domain.Role;
import com.pug.identity.domain.User;
import com.pug.identity.domain.enums.UserRole;
import com.pug.partner.domain.PartnerEntity;
import com.pug.partner.domain.Staff;
import com.pug.project.domain.Project;
import io.quarkus.test.TestTransaction;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceException;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;

@QuarkusTest
class ProjectEnrollmentJpaMappingTest {

  @Inject EntityManager em;

  private static final String CPF_A = "93541134780";
  private static final String CPF_B = "11144477735";
  private static final String CNPJ_A = "27865757000102";

  private City newCity(String name, String ibge) {
    var c = City.builder().name(name).ibgeCode(ibge).build();
    em.persist(c);
    return c;
  }

  private FieldOfStudy newField(String name) {
    var f = FieldOfStudy.builder().name(name).build();
    em.persist(f);
    return f;
  }

  private Course newCourse(String name) {
    var fos = newField("Computing");
    var c = Course.builder().name(name).field(fos).build();
    em.persist(c);
    return c;
  }

  private PartnerEntity newEntity(String name, String cnpj, City city) {
    var e = PartnerEntity.builder().name(name).cnpj(cnpj).city(city).active(true).build();
    em.persist(e);
    return e;
  }

  private User newUser(String name, String cpf) {
    var u = User.builder().name(name).cpf(cpf).build();
    em.persist(u);
    return u;
  }

  private Role newRole(User u, UserRole r, String email) {
    var role = Role.builder().user(u).role(r).email(email).build();
    em.persist(role);
    return role;
  }

  private Staff newStaff(Role role, PartnerEntity entity) {
    var s = Staff.builder().userRole(role).entity(entity).build();
    em.persist(s);
    return s;
  }

  private Project newProject(String name) {
    var city = newCity("Florianópolis2", "4202407");
    var ent = newEntity("Ent", CNPJ_A, city);
    var u = newUser("Grace Hopper", CPF_A);
    var role = newRole(u, UserRole.PARTNER, "p@example.org");
    var staff = newStaff(role, ent);
    var field = newField("Computing2");
    var p =
        Project.builder()
            .name(name)
            .entity(ent)
            .field(field)
            .createdBy(staff)
            .updatedBy(staff)
            .build();
    em.persist(p);
    return p;
  }

  private Student newStudent(String reg) {
    var u = newUser("Student User", CPF_B);
    var r = newRole(u, UserRole.FORMER_STUDENT, "s@example.org");
    var course = newCourse("Computing");
    var s = Student.builder().userRole(r).academicRegistration(reg).course(course).build();
    em.persist(s);
    return s;
  }

  @Test
  @TestTransaction
  void persistEnrollmentDefaults() {
    var proj = newProject("Enrollment A");
    var stud = newStudent("AR-0001");

    var e = ProjectEnrollment.builder().project(proj).student(stud).build();
    em.persist(e);
    em.flush();
    em.clear();

    var found = em.find(ProjectEnrollment.class, e.getId());
    assertNotNull(found.getId());
    assertEquals(7, found.getId().version());
    assertEquals(ProjectEnrollmentStatus.PENDING, found.getStatus());
    assertNotNull(found.getRequestAt());
    assertNull(found.getAcceptedAt());
    assertNull(found.getClosingStatusAt());
  }

  @Test
  @TestTransaction
  void updateAcceptedAllowsAcceptedAt() throws InterruptedException {
    var proj = newProject("Enrollment B");
    var stud = newStudent("AR-0002");

    var e = ProjectEnrollment.builder().project(proj).student(stud).build();
    em.persist(e);
    em.flush();

    Thread.sleep(5);
    e.setStatus(ProjectEnrollmentStatus.ACCEPTED);
    e.setAcceptedAt(Instant.now());
    em.flush();

    assertEquals(ProjectEnrollmentStatus.ACCEPTED, e.getStatus());
    assertNotNull(e.getAcceptedAt());
    assertTrue(e.getAcceptedAt().isAfter(e.getRequestAt()));
  }

  @Test
  @TestTransaction
  void statusNotNullEnforcedByDb() {
    var proj = newProject("Enrollment C");
    var stud = newStudent("AR-0003");

    assertThrows(
        PersistenceException.class,
        () -> {
          em.createNativeQuery(
                  """
                                            insert into projects_enrollments
                                              (id, project_id, student_id, status, request_at)
                                            values
                                              (gen_random_uuid(), :pid, :sid, NULL, now())
                                            """)
              .setParameter("pid", proj.getId())
              .setParameter("sid", stud.getId())
              .executeUpdate();
          em.flush();
        });
  }

  @Test
  @TestTransaction
  void foreignKeysEnforcedByDb() {
    assertThrows(
        PersistenceException.class,
        () -> {
          em.createNativeQuery(
                  """
                                            insert into projects_enrollments
                                              (id, project_id, student_id, status, request_at)
                                            values
                                              (gen_random_uuid(), :pid, :sid, 'PENDING', now())
                                            """)
              .setParameter("pid", UUID.randomUUID())
              .setParameter("sid", UUID.randomUUID())
              .executeUpdate();
          em.flush();
        });
  }
}
