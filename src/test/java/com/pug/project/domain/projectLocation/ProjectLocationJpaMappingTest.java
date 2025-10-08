package com.pug.project.domain.projectLocation;

import static org.junit.jupiter.api.Assertions.*;

import com.pug.academic.domain.FieldOfStudy;
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
class ProjectLocationJpaMappingTest {

  @Inject EntityManager em;

  private static final String CPF_A = "93541134780";
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
    var city = newCity("Florianópolis2", "4305407");
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

  private ProjectAllocation newAllocation(Project p, String hours, LocalDate s, LocalDate e) {
    var a =
        ProjectAllocation.builder()
            .project(p)
            .offeredHours(new java.math.BigDecimal(hours))
            .startDate(s)
            .endDate(e)
            .build();
    em.persist(a);
    return a;
  }

  @Test
  @TestTransaction
  void persistSetsUuidv7TimestampsAndRoundTrips() {
    var p = newProject("Community Garden");
    var a = newAllocation(p, "12.50", LocalDate.of(2025, 1, 1), LocalDate.of(2025, 2, 1));

    var loc =
        ProjectLocation.builder()
            .projectAllocation(a)
            .address("Rua X, 100")
            .latitude(new BigDecimal("-27.595377"))
            .longitude(new BigDecimal("-48.548050"))
            .build();

    em.persist(loc);
    em.flush();
    em.clear();

    var found = em.find(ProjectLocation.class, loc.getId());
    assertNotNull(found.getId());
    assertEquals(7, found.getId().version());
    assertEquals("Rua X, 100", found.getAddress());
    assertEquals(new BigDecimal("-27.595377"), found.getLatitude());
    assertEquals(new BigDecimal("-48.548050"), found.getLongitude());
    assertNotNull(found.getCreatedAt());
    assertNotNull(found.getUpdatedAt());
    assertFalse(found.getUpdatedAt().isBefore(found.getCreatedAt()));
    assertEquals(a.getId(), found.getProjectAllocation().getId());
  }

  @Test
  @TestTransaction
  void dbCheckConstraintsAreEnforced_latRange() {
    var p = newProject("Checks");
    var a = newAllocation(p, "1.00", LocalDate.of(2025, 3, 1), LocalDate.of(2025, 3, 31));

    assertThrows(
        PersistenceException.class,
        () -> {
          em.createNativeQuery(
                  """
                                    insert into projects_locations (id, project_allocation_id, address, latitude, longitude, created_at)
                                    values (gen_random_uuid(), :aid, 'bad lat', 91.000000, 0.000000, now())
                                    """)
              .setParameter("aid", a.getId())
              .executeUpdate();
          em.flush();
        });
  }

  @Test
  @TestTransaction
  void dbCheckConstraintsAreEnforced_lngRange() {
    var p = newProject("Checks 2");
    var a = newAllocation(p, "1.00", LocalDate.of(2025, 4, 1), LocalDate.of(2025, 4, 30));

    assertThrows(
        PersistenceException.class,
        () -> {
          em.createNativeQuery(
                  """
                                    insert into projects_locations (id, project_allocation_id, address, latitude, longitude, created_at)
                                    values (gen_random_uuid(), :aid, 'bad lng', 0.000000, 181.000000, now())
                                    """)
              .setParameter("aid", a.getId())
              .executeUpdate();
          em.flush();
        });
  }

  @Test
  @TestTransaction
  void dbCheckConstraintsAreEnforced_latLngPair() {
    var p = newProject("Checks 3");
    var a = newAllocation(p, "1.00", LocalDate.of(2025, 5, 1), LocalDate.of(2025, 5, 31));

    assertThrows(
        PersistenceException.class,
        () -> {
          em.createNativeQuery(
                  """
                                    insert into projects_locations (id, project_allocation_id, address, latitude, longitude, created_at)
                                    values (gen_random_uuid(), :aid, 'pair', 10.000000, NULL, now())
                                    """)
              .setParameter("aid", a.getId())
              .executeUpdate();
          em.flush();
        });
  }

  @Test
  @TestTransaction
  void fkToAllocationEnforcedByDb() {
    assertThrows(
        PersistenceException.class,
        () -> {
          em.createNativeQuery(
                  """
                                    insert into projects_locations (id, project_allocation_id, created_at)
                                    values (gen_random_uuid(), :aid, now())
                                    """)
              .setParameter("aid", UUID.randomUUID())
              .executeUpdate();
          em.flush();
        });
  }
}
