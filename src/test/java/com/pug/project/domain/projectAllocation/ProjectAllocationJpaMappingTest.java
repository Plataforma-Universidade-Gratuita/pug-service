package com.pug.project.domain.projectAllocation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.pug.academic.domain.FieldOfStudy;
import com.pug.geo.domain.City;
import com.pug.identity.domain.Role;
import com.pug.identity.domain.User;
import com.pug.identity.domain.enums.UserRole;
import com.pug.partner.domain.PartnerEntity;
import com.pug.partner.domain.Staff;
import com.pug.project.domain.Project;
import com.pug.project.domain.ProjectAllocation;
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
class ProjectAllocationJpaMappingTest {

  @Inject EntityManager em;

  private static final String CPF_A = "93541134780";
  private static final String CPF_B = "98765432100";
  private static final String CNPJ_A = "27865757000102";

  private City newCity(String name, String ibge) {
    var c = City.builder().name(name).ibgeCode(ibge).build();
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

  private FieldOfStudy newField(String name) {
    var f = FieldOfStudy.builder().name(name).build();
    em.persist(f);
    return f;
  }

  private Project newProject(String name) {
    var city = newCity("Florianópolis2", "4202407");
    var ent = newEntity("Ent", CNPJ_A, city);
    var u = newUser("Grace Hopper", CPF_A);
    var role = newRole(u, UserRole.PARTNER, "p@example.org");
    var staff = newStaff(role, ent);
    var field = newField("Computing");

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

  @Test
  @TestTransaction
  void persistSetsUuidv7AndValuesRoundTrip() {
    var proj = newProject("Community Garden");

    var alloc =
        ProjectAllocation.builder()
            .project(proj)
            .offeredHours(new BigDecimal("12.50"))
            .startDate(LocalDate.of(2025, 1, 5))
            .endDate(LocalDate.of(2025, 2, 5))
            .build();

    em.persist(alloc);
    em.flush();
    em.clear();

    var found = em.find(ProjectAllocation.class, alloc.getId());
    assertNotNull(found.getId());
    assertEquals(7, found.getId().version());
    assertEquals(new BigDecimal("12.50"), found.getOfferedHours());
    assertEquals(LocalDate.of(2025, 1, 5), found.getStartDate());
    assertEquals(LocalDate.of(2025, 2, 5), found.getEndDate());
    assertEquals(proj.getId(), found.getProject().getId());
  }

  @Test
  @TestTransaction
  void updatePersistsNewValues() {
    var proj = newProject("Park Cleanup");
    var alloc =
        ProjectAllocation.builder()
            .project(proj)
            .offeredHours(new BigDecimal("10.00"))
            .startDate(LocalDate.of(2025, 3, 1))
            .endDate(LocalDate.of(2025, 3, 31))
            .build();

    em.persist(alloc);
    em.flush();

    alloc.setOfferedHours(new BigDecimal("15.25"));
    alloc.setEndDate(LocalDate.of(2025, 4, 15));
    em.flush();
    em.clear();

    var found = em.find(ProjectAllocation.class, alloc.getId());
    assertEquals(new BigDecimal("15.25"), found.getOfferedHours());
    assertEquals(LocalDate.of(2025, 4, 15), found.getEndDate());
  }

  @Test
  @TestTransaction
  void fkToProjectEnforcedByDb() {
    assertThrows(
        PersistenceException.class,
        () -> {
          em.createNativeQuery(
                  """
                                            insert into projects_allocations (id, project_id, offered_hours, start_date, end_date)
                                            values (gen_random_uuid(), :pid, 10.00, DATE '2025-01-01', DATE '2025-01-31')
                                            """)
              .setParameter("pid", UUID.randomUUID())
              .executeUpdate();
          em.flush();
        });
  }

  @Test
  @TestTransaction
  void dateCheckConstraintEnforcedByDb() {
    var proj = newProject("Constraint");
    assertThrows(
        PersistenceException.class,
        () -> {
          em.createNativeQuery(
                  """
                                            insert into projects_allocations (id, project_id, offered_hours, start_date, end_date)
                                            values (gen_random_uuid(), :pid, 10.00, DATE '2025-02-10', DATE '2025-02-01')
                                            """)
              .setParameter("pid", proj.getId())
              .executeUpdate();
          em.flush();
        });
  }

  @Test
  @TestTransaction
  void numericPrecisionScaleEnforcedByDb() {
    var proj = newProject("Precision");
    assertThrows(
        PersistenceException.class,
        () -> {
          em.createNativeQuery(
                  """
                                            insert into projects_allocations (id, project_id, offered_hours, start_date, end_date)
                                            values (gen_random_uuid(), :pid, 12345.00, DATE '2025-01-01', DATE '2025-01-31')
                                            """)
              .setParameter("pid", proj.getId())
              .executeUpdate();
          em.flush();
        });

    assertThrows(
        PersistenceException.class,
        () -> {
          em.createNativeQuery(
                  """
                                            insert into projects_allocations (id, project_id, offered_hours, start_date, end_date)
                                            values (gen_random_uuid(), :pid, 10.123, DATE '2025-01-01', DATE '2025-01-31')
                                            """)
              .setParameter("pid", proj.getId())
              .executeUpdate();
          em.flush();
        });
  }
}
