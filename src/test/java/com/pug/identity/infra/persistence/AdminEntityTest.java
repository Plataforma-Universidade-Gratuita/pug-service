package com.pug.identity.infra.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.pug.identity.domain.Cpf;
import com.pug.identity.domain.User;
import io.quarkus.test.TestTransaction;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;

@QuarkusTest
class AdminEntityTest {

  @Inject EntityManager em;
  @Inject UserRepositoryImpl users;

  private static User newUser(String name, String cpf) {
    return User.builder().cpf(Cpf.of(cpf)).name(name).build();
  }

  @Test
  @TestTransaction
  void constructorSetsUserIdAndDbSetsGrantedAt() {
    var u = users.save(newUser("Admin Entity User", "935.411.347-80"));
    var ae = new AdminEntity(u.getId());
    em.persist(ae);
    em.flush();
    em.clear();

    var found = em.find(AdminEntity.class, u.getId());
    assertNotNull(found);
    assertEquals(u.getId(), found.getUserId());
    assertNotNull(found.getGrantedAt());
  }
}
