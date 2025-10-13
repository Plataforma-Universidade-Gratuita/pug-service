package com.pug.identity.service;

import static com.pug.identity.domain.IdentityErrorCodes.IDENTITY_CPF_ALREADY_IN_USE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.pug.identity.service.commands.CreateUserCommand;
import com.pug.identity.service.commands.UpdateUserCommand;
import com.pug.shared.application.StringQuery;
import com.pug.shared.application.UuidQuery;
import com.pug.shared.domain.exceptions.AppValidationException;
import io.quarkus.test.TestTransaction;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import java.util.UUID;
import org.junit.jupiter.api.Test;

@QuarkusTest
class UserServiceTest {

  @Inject UserService svc;

  private static final String CPF_A = "93541134780";
  private static final String CPF_B = "52998224725";

  @Test
  @TestTransaction
  void createPersistsAndReturnsUser() {
    var u = svc.create(new CreateUserCommand(CPF_A, "Alice"));
    assertNotNull(u.getId());
    assertEquals("Alice", u.getName());
    assertEquals(CPF_A, u.getCpf().getValue());
  }

  @Test
  @TestTransaction
  void createRejectsDuplicateCpf() {
    svc.create(new CreateUserCommand(CPF_A, "Alice"));
    var ex =
        assertThrows(
            AppValidationException.class,
            () -> svc.create(new CreateUserCommand("935.411.347-80", "Bruna")));
    assertEquals(IDENTITY_CPF_ALREADY_IN_USE, ex.code());
  }

  @Test
  @TestTransaction
  void updateChangesNameOnly() {
    var u = svc.create(new CreateUserCommand(CPF_A, "Alice"));
    var out = svc.update(new UpdateUserCommand(u.getId(), null, "Alice B"));
    assertEquals("Alice B", out.getName());
    assertEquals(CPF_A, out.getCpf().getValue());
  }

  @Test
  @TestTransaction
  void updateChangesCpf() {
    var u = svc.create(new CreateUserCommand(CPF_A, "Alice"));
    var out = svc.update(new UpdateUserCommand(u.getId(), CPF_B, null));
    assertEquals(CPF_B, out.getCpf().getValue());
    assertEquals("Alice", out.getName());
  }

  @Test
  @TestTransaction
  void updateRejectsDuplicateCpfFromAnotherUser() {
    var a = svc.create(new CreateUserCommand(CPF_A, "A"));
    svc.create(new CreateUserCommand(CPF_B, "B"));
    var ex =
        assertThrows(
            AppValidationException.class,
            () -> svc.update(new UpdateUserCommand(a.getId(), CPF_B, null)));
    assertEquals(IDENTITY_CPF_ALREADY_IN_USE, ex.code());
  }

  @Test
  @TestTransaction
  void getByIdAndFindByCpfReturnOptionals() {
    var u = svc.create(new CreateUserCommand(CPF_A, "Alice"));
    assertTrue(svc.getById(new UuidQuery(u.getId())).isPresent());
    assertTrue(svc.findByCpf(new StringQuery("935.411.347-80")).isPresent());
    assertTrue(svc.getById(new UuidQuery(UUID.randomUUID())).isEmpty());
  }
}
