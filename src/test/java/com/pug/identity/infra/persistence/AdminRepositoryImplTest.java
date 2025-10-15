package com.pug.identity.infra.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.pug.identity.domain.Cpf;
import com.pug.identity.domain.User;
import io.quarkus.test.TestTransaction;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;

@QuarkusTest
class AdminRepositoryImplTest {

  @Inject AdminRepositoryImpl admins;
  @Inject UserRepositoryImpl users;

  private static User newUser(String name, String cpf) {
    return User.builder().cpf(Cpf.of(cpf)).name(name).build();
  }

  @Test
  @TestTransaction
  void grantFindListAndRevoke() {
    var u = users.save(newUser("Alice Admin", "935.411.347-80"));
    var id = u.getId();

    assertFalse(admins.isAdmin(id));
    assertTrue(admins.findByUserId(id).isEmpty());

    var t0 = Instant.now();
    admins.grant(id);

    assertTrue(admins.isAdmin(id));
    var opt = admins.findByUserId(id);
    assertTrue(opt.isPresent());
    var a = opt.get();
    assertEquals(id, a.userId());
    assertNotNull(a.grantedAt());
    assertFalse(a.grantedAt().isAfter(Instant.now().plusSeconds(1)));
    assertFalse(a.grantedAt().isBefore(t0.minus(Duration.ofSeconds(5))));

    admins.grant(id);
    long occurrences = admins.listAllAdmins().stream().filter(x -> x.userId().equals(id)).count();
    assertEquals(1, occurrences);

    admins.revoke(id);
    assertFalse(admins.isAdmin(id));
    assertTrue(admins.findByUserId(id).isEmpty());
  }

  @Test
  @TestTransaction
  void isAdminFalseForUnknownUser() {
    assertFalse(admins.isAdmin(UUID.randomUUID()));
  }

  @Test
  @TestTransaction
  void listAllContainsNewlyGranted() {
    var u = users.save(newUser("Bob Admin", "529.982.247-25"));
    admins.grant(u.getId());
    assertTrue(admins.listAllAdmins().stream().anyMatch(a -> a.userId().equals(u.getId())));
  }
}
