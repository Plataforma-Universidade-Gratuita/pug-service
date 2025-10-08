package com.pug.project.domain.project;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.pug.academic.domain.FieldOfStudy;
import com.pug.geo.domain.City;
import com.pug.identity.domain.Role;
import com.pug.identity.domain.User;
import com.pug.identity.domain.enums.UserRole;
import com.pug.partner.domain.PartnerEntity;
import com.pug.partner.domain.Staff;
import com.pug.project.domain.Project;
import com.pug.project.domain.enums.ProjectStatus;
import io.quarkus.test.TestTransaction;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceException;
import java.util.UUID;
import org.junit.jupiter.api.Test;

@QuarkusTest
class ProjectJpaMappingTest {

  @Inject EntityManager em;

  private static final String CPF_A = "93541134780";
  private static final String CPF_B = "98765432100";
  private static final String CNPJ_A = "27865757000102";
  private static final String CNPJ_B = "04252011000110";

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

  @Test
  @TestTransaction
  void persistSetsUuidv7TimestampsAndDefaultStatus() {
    var city = newCity("Jaraguá do Sul2", "4109102");
    var ent = newEntity("Ent A", CNPJ_A, city);
    var u = newUser("Grace Hopper", CPF_A);
    var role = newRole(u, UserRole.PARTNER, "p1@example.org");
    var staff = newStaff(role, ent);
    var field = newField("Computing");

    var p =
        Project.builder()
            .name("Community Garden")
            .description("Init")
            .entity(ent)
            .field(field)
            .createdBy(staff)
            .updatedBy(staff)
            .build();

    em.persist(p);
    em.flush();
    em.clear();

    var found = em.find(Project.class, p.getId());
    assertNotNull(found.getId());
    assertEquals(7, found.getId().version());
    assertEquals(ProjectStatus.PLANNED, found.getStatus());
    assertNotNull(found.getCreatedAt());
    assertNotNull(found.getUpdatedAt());
    assertFalse(found.getUpdatedAt().isBefore(found.getCreatedAt()));
  }

  @Test
  @TestTransaction
  void updateChangesUpdatedAt() throws InterruptedException {
    var city = newCity("Blumenau1", "4211404");
    var ent = newEntity("Ent B", CNPJ_B, city);
    var u = newUser("Alan Turing", CPF_A);
    var role = newRole(u, UserRole.PARTNER, "p2@example.org");
    var staff = newStaff(role, ent);
    var field = newField("Math");

    var p =
        Project.builder()
            .name("Park Cleanup")
            .entity(ent)
            .field(field)
            .createdBy(staff)
            .updatedBy(staff)
            .build();

    em.persist(p);
    em.flush();
    var before = p.getUpdatedAt();
    Thread.sleep(5);
    p.setDescription("Scope changed");
    em.flush();

    assertTrue(p.getUpdatedAt().isAfter(before));
  }

  @Test
  @TestTransaction
  void statusNotNullEnforcedByDb() {
    var ids = insertMinimalEntityGraph();

    assertThrows(
        PersistenceException.class,
        () -> {
          em.createNativeQuery(
                  """
                                            insert into projects (id, name, description, entity_id, field_id, status, max_participants, created_by, created_at, updated_by, updated_at)
                                            values (gen_random_uuid(), :name, :desc, :entity, :field, NULL, NULL, :created_by, now(), :updated_by, now())
                                            """)
              .setParameter("name", "Null Status")
              .setParameter("desc", "x")
              .setParameter("entity", ids.entityId)
              .setParameter("field", ids.fieldId)
              .setParameter("created_by", ids.staffId)
              .setParameter("updated_by", ids.staffId)
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
                                            insert into projects (id, name, entity_id, field_id, status, created_by, created_at, updated_by, updated_at)
                                            values (gen_random_uuid(), 'FK test', :entity, :field, 'PLANNED', :created_by, now(), :updated_by, now())
                                            """)
              .setParameter("entity", UUID.randomUUID())
              .setParameter("field", UUID.randomUUID())
              .setParameter("created_by", UUID.randomUUID())
              .setParameter("updated_by", UUID.randomUUID())
              .executeUpdate();
          em.flush();
        });
  }

  @Test
  @TestTransaction
  void createdByAndUpdatedByPersistToCorrectColumns() {
    var city = newCity("Joinville3", "4203101");
    var ent = newEntity("Ent C", CNPJ_A, city);
    var u1 = newUser("User A", CPF_A);
    var u2 = newUser("User B", CPF_B);
    var r1 = newRole(u1, UserRole.PARTNER, "s1@example.org");
    var r2 = newRole(u2, UserRole.PARTNER, "s2@example.org");
    var s1 = newStaff(r1, ent);
    var s2 = newStaff(r2, ent);
    var field = newField("Health");

    var p =
        Project.builder()
            .name("Tree Planting")
            .entity(ent)
            .field(field)
            .createdBy(s1)
            .updatedBy(s1)
            .build();

    em.persist(p);
    em.flush();

    p.setUpdatedBy(s2);
    em.flush();

    var row =
        (Object[])
            em.createNativeQuery("select created_by, updated_by from projects where id = :id")
                .setParameter("id", p.getId())
                .getSingleResult();

    assertEquals(s1.getId(), row[0]);
    assertEquals(s2.getId(), row[1]);
  }

  private record GraphIds(UUID entityId, UUID staffId, UUID fieldId) {}

  private GraphIds insertMinimalEntityGraph() {
    UUID cityId = UUID.randomUUID();
    em.createNativeQuery(
            "insert into cities (id, name, ibge_code) values (:id, 'CityX', '4200000')")
        .setParameter("id", cityId)
        .executeUpdate();

    UUID entityId = UUID.randomUUID();
    em.createNativeQuery(
            """
                                insert into entities (id, cnpj, name, city_id, active, created_at)
                                values (:id, :cnpj, 'EntX', :city, true, now())
                                """)
        .setParameter("id", entityId)
        .setParameter("cnpj", CNPJ_A)
        .setParameter("city", cityId)
        .executeUpdate();

    UUID userId = UUID.randomUUID();
    em.createNativeQuery(
            "insert into users (id, cpf, name, created_at) values (:id, :cpf, 'U', now())")
        .setParameter("id", userId)
        .setParameter("cpf", CPF_A)
        .executeUpdate();

    UUID roleId = UUID.randomUUID();
    em.createNativeQuery(
            """
                                insert into users_roles (id, user_id, role, email, active, created_at)
                                values (:id, :user_id, 'PARTNER', 'x@example.org', true, now())
                                """)
        .setParameter("id", roleId)
        .setParameter("user_id", userId)
        .executeUpdate();

    UUID staffId = UUID.randomUUID();
    em.createNativeQuery(
            "insert into staff (id, user_role_id, entity_id) values (:id, :role_id, :entity_id)")
        .setParameter("id", staffId)
        .setParameter("role_id", roleId)
        .setParameter("entity_id", entityId)
        .executeUpdate();

    UUID fieldId = UUID.randomUUID();
    em.createNativeQuery("insert into fields_of_study (id, name) values (:id, 'FieldX')")
        .setParameter("id", fieldId)
        .executeUpdate();

    return new GraphIds(entityId, staffId, fieldId);
  }
}
