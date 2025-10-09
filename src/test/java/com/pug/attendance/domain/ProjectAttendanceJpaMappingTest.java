package com.pug.attendance.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.pug.academic.domain.Course;
import com.pug.academic.domain.FieldOfStudy;
import com.pug.academic.domain.Student;
import com.pug.attendance.domain.enums.AttendanceStatus;
import com.pug.enrollment.domain.ProjectEnrollment;
import com.pug.enrollment.domain.enums.ProjectEnrollmentStatus;
import com.pug.geo.domain.City;
import com.pug.identity.domain.Role;
import com.pug.identity.domain.User;
import com.pug.identity.domain.enums.UserRole;
import com.pug.partner.domain.PartnerEntity;
import com.pug.partner.domain.Staff;
import com.pug.project.domain.Project;
import com.pug.project.domain.ProjectAllocation;
import com.pug.project.domain.ProjectLocation;
import com.pug.project.domain.enums.ProjectStatus;
import io.quarkus.test.TestTransaction;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
import org.junit.jupiter.api.Test;

@QuarkusTest
class ProjectAttendanceJpaMappingTest {

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
    var city = newCity("Florianópolis3", "4205307");
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
            .status(ProjectStatus.PLANNED)
            .createdBy(staff)
            .updatedBy(staff)
            .build();
    em.persist(p);
    return p;
  }

  private Student newStudent(String reg) {
    var u = newUser("Student", CPF_B);
    var r = newRole(u, UserRole.FORMER_STUDENT, "s@example.org");
    var course = newCourse("Computing3");
    var s = Student.builder().userRole(r).academicRegistration(reg).course(course).build();
    em.persist(s);
    return s;
  }

  private ProjectAllocation newAllocation(Project p, String hours, LocalDate s, LocalDate e) {
    var a =
        ProjectAllocation.builder()
            .project(p)
            .offeredHours(new BigDecimal(hours))
            .startDate(s)
            .endDate(e)
            .build();
    em.persist(a);
    return a;
  }

  private ProjectLocation newLocation(ProjectAllocation a, String addr, String lat, String lng) {
    var l =
        ProjectLocation.builder()
            .projectAllocation(a)
            .address(addr)
            .latitude(lat == null ? null : new BigDecimal(lat))
            .longitude(lng == null ? null : new BigDecimal(lng))
            .build();
    em.persist(l);
    return l;
  }

  private ProjectEnrollment newEnrollment(Project p, Student s) {
    var e =
        ProjectEnrollment.builder()
            .project(p)
            .student(s)
            .status(ProjectEnrollmentStatus.PENDING)
            .build();
    em.persist(e);
    return e;
  }

  @Test
  @TestTransaction
  void persistSetsUuidv7TimestampsDefaultsAndRoundTrips() {
    var p = newProject("Attendance A");
    var a = newAllocation(p, "12.50", LocalDate.of(2025, 1, 1), LocalDate.of(2025, 2, 1));
    var loc = newLocation(a, "Rua X, 100", "-27.595377", "-48.548050");
    var stud = newStudent("AR-1001");
    var enr = newEnrollment(p, stud);

    var att =
        ProjectAttendance.builder()
            .enrollment(enr)
            .projectLocation(loc)
            .duration(new BigDecimal("1.50"))
            .build();

    em.persist(att);
    em.flush();
    em.clear();

    var found = em.find(ProjectAttendance.class, att.getId());
    assertNotNull(found.getId());
    assertEquals(7, found.getId().version());
    assertEquals(AttendanceStatus.PENDING, found.getStatus());
    assertEquals(new BigDecimal("1.50"), found.getDuration());
    assertNotNull(found.getCreatedAt());
    assertNotNull(found.getUpdatedAt());
    assertFalse(found.getUpdatedAt().isBefore(found.getCreatedAt()));
    assertEquals(enr.getId(), found.getEnrollment().getId());
    assertEquals(loc.getId(), found.getProjectLocation().getId());
  }

  @Test
  @TestTransaction
  void uniqueQrHashEnforcedByDb() {
    var p = newProject("Attendance B");
    var a = newAllocation(p, "10.00", LocalDate.of(2025, 3, 1), LocalDate.of(2025, 3, 31));
    var loc = newLocation(a, null, "0.000000", "0.000000");
    var stud = newStudent("AR-1002");
    var enr = newEnrollment(p, stud);

    var att1 =
        ProjectAttendance.builder()
            .enrollment(enr)
            .projectLocation(loc)
            .duration(new BigDecimal("1.00"))
            .qrValidationHash("HASH-1")
            .build();
    em.persist(att1);
    em.flush();

    var att2 =
        ProjectAttendance.builder()
            .enrollment(enr)
            .projectLocation(loc)
            .duration(new BigDecimal("1.00"))
            .qrValidationHash("HASH-1")
            .build();
    em.persist(att2);

    assertThrows(PersistenceException.class, em::flush);
  }

  @Test
  @TestTransaction
  void foreignKeysEnforcedByDb() {
    assertThrows(
        PersistenceException.class,
        () -> {
          em.createNativeQuery(
                  """
                                    insert into projects_attendances
                                      (id, enrollment_id, project_location_id, duration, status, created_at)
                                    values (gen_random_uuid(), :enr, :loc, 1.00, 'PENDING', now())
                                    """)
              .setParameter("enr", UUID.randomUUID())
              .setParameter("loc", UUID.randomUUID())
              .executeUpdate();
          em.flush();
        });
  }
}
