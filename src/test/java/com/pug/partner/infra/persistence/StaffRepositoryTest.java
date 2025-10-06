package com.pug.partner.infra.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
import java.util.UUID;
import org.junit.jupiter.api.Test;

@QuarkusTest
class StaffRepositoryTest {

  @Inject StaffRepository repo;
  @Inject EntityManager em;

  private static final String CPF = "93541134780";
  private static final String CNPJ = "11222333000181";

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

  private Staff newStaff(Role role, PartnerEntity entity) {
    var s = Staff.builder().userRole(role).entity(entity).build();
    em.persist(s);
    return s;
  }

  @Test
  @TestTransaction
  void findByUserRoleIdReturnsStaff() {
    var city = newCity("Florianópolis2", "4235407");
    var ent = newEntity(city, CNPJ, "Org A");
    var u = newUser("Ada", CPF);
    var role = newRole(u, "admin@example.org", UserRole.ADMIN);
    var s = newStaff(role, ent);
    em.flush();
    em.clear();

    var found = repo.findByUserRoleId(role.getId());

    assertTrue(found.isPresent());
    assertEquals(s.getId(), found.get().getId());
    assertEquals(role.getId(), found.get().getUserRole().getId());
    assertEquals(ent.getId(), found.get().getEntity().getId());
  }

  @Test
  @TestTransaction
  void findByUserRoleIdEmptyWhenMissing() {
    var missing = UUID.randomUUID();
    assertTrue(repo.findByUserRoleId(missing).isEmpty());
  }
}
