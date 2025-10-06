package com.pug.partner.domain.staff;

import static org.junit.jupiter.api.Assertions.*;

import com.pug.geo.domain.City;
import com.pug.identity.domain.Role;
import com.pug.identity.domain.User;
import com.pug.identity.domain.enums.UserRole;
import com.pug.partner.domain.PartnerEntity;
import com.pug.partner.domain.Staff;
import io.quarkus.test.TestTransaction;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceException;
import org.junit.jupiter.api.Test;

@QuarkusTest
class StaffJpaMappingTest {

  @Inject EntityManager em;

  private static final String VALID_CPF = "93541134780";
  private static final String VALID_CNPJ = "11222333000181";

  private City newCity(String name, String ibge) {
    var c = City.builder().name(name).ibgeCode(ibge).build();
    em.persist(c);
    return c;
  }

  private PartnerEntity newEntity(City city, String cnpj, String name) {
    var e = PartnerEntity.builder().cnpj(cnpj).name(name).city(city).build();
    em.persist(e);
    return e;
  }

  private User newUser(String name, String cpf) {
    var u = User.builder().name(name).cpf(cpf).build();
    em.persist(u);
    return u;
  }

  private Role newRole(User u, String email, UserRole r) {
    var role = Role.builder().user(u).email(email).role(r).build();
    em.persist(role);
    return role;
  }

  @Test
  @TestTransaction
  void persistSetsUuidv7AndLinks() {
    var city = newCity("Florianópolis2", "2205407");
    var ent = newEntity(city, VALID_CNPJ, "Org A");
    var u = newUser("Ada", VALID_CPF);
    var role = newRole(u, "admin@example.org", UserRole.ADMIN);

    var s = Staff.builder().entity(ent).userRole(role).build();
    em.persist(s);
    em.flush();
    em.clear();

    var found = em.find(Staff.class, s.getId());
    assertNotNull(found.getId());
    assertEquals(7, found.getId().version());
    assertEquals(role.getId(), found.getUserRole().getId());
    assertEquals(ent.getId(), found.getEntity().getId());
  }

  @Test
  @TestTransaction
  void userRoleIsUniqueAcrossStaff() {
    var city = newCity("Joinville3", "4229102");
    var ent1 = newEntity(city, VALID_CNPJ, "Org A");
    var ent2 = newEntity(city, "19131243000197", "Org B");
    var u = newUser("Bob", VALID_CPF);
    var role = newRole(u, "r@example.org", UserRole.PARTNER);

    var a = Staff.builder().entity(ent1).userRole(role).build();
    em.persist(a);
    em.flush();

    var b = Staff.builder().entity(ent2).userRole(role).build();
    em.persist(b);
    assertThrows(PersistenceException.class, em::flush);
  }

  @Test
  @TestTransaction
  void notNullConstraintsEnforcedByDb() {
    var city = newCity("Lages2", "4229300");
    var ent = newEntity(city, VALID_CNPJ, "Org C");
    var u = newUser("Carol", VALID_CPF);
    var role = newRole(u, "c@example.org", UserRole.ADMIN);
    em.flush();

    assertThrows(
        PersistenceException.class,
        () -> {
          em.createNativeQuery(
                  """
                                    insert into staff (id, user_role_id, entity_id)
                                    values (gen_random_uuid(), null, :eid)
                                    """)
              .setParameter("eid", ent.getId())
              .executeUpdate();
          em.flush();
        });

    assertThrows(
        PersistenceException.class,
        () -> {
          em.createNativeQuery(
                  """
                                    insert into staff (id, user_role_id, entity_id)
                                    values (gen_random_uuid(), :rid, null)
                                    """)
              .setParameter("rid", role.getId())
              .executeUpdate();
          em.flush();
        });
  }
}
