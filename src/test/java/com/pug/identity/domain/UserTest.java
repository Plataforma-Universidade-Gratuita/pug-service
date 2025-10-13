package com.pug.identity.domain;

import static com.pug.identity.domain.IdentityErrorCodes.IDENTITY_CPF_REQUIRED;
import static com.pug.identity.domain.IdentityErrorCodes.IDENTITY_NAME_REQUIRED;
import static com.pug.identity.domain.IdentityErrorCodes.IDENTITY_NAME_TOO_LONG;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.pug.shared.domain.exceptions.AppValidationException;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class UserTest {

  private static final String CPF_VALID = "93541134780";

  @Test
  void buildValidUser() {
    var u = User.builder().cpf(Cpf.of(CPF_VALID)).name("Alice").build();
    assertNotNull(u);
    assertEquals("Alice", u.getName());
    assertEquals(CPF_VALID, u.getCpf().getValue());
  }

  @Test
  void nullCpfThrows() {
    var ex = assertThrows(AppValidationException.class, () -> User.builder().name("Bob").build());
    assertEquals(IDENTITY_CPF_REQUIRED, ex.code());
  }

  @Test
  void blankNameThrows() {
    var ex =
        assertThrows(
            AppValidationException.class,
            () -> User.builder().cpf(Cpf.of(CPF_VALID)).name(" ").build());
    assertEquals(IDENTITY_NAME_REQUIRED, ex.code());
  }

  @Test
  void tooLongNameThrows() {
    String longName = "x".repeat(151);
    var ex =
        assertThrows(
            AppValidationException.class,
            () -> User.builder().cpf(Cpf.of(CPF_VALID)).name(longName).build());
    assertEquals(IDENTITY_NAME_TOO_LONG, ex.code());
  }

  @Test
  void toBuilderCopiesAndUpdates() {
    var id = UUID.randomUUID();
    var u1 = User.builder().id(id).cpf(Cpf.of(CPF_VALID)).name("Alice").build();
    var u2 = u1.toBuilder().name("Alice B").build();
    assertEquals(id, u2.getId());
    assertEquals(u1.getCpf(), u2.getCpf());
    assertEquals("Alice B", u2.getName());
  }

  @Test
  void equalsBasedOnId() {
    var id = UUID.randomUUID();
    var a = User.builder().id(id).cpf(Cpf.of(CPF_VALID)).name("A").build();
    var b = User.builder().id(id).cpf(Cpf.of(CPF_VALID)).name("B").build();
    assertEquals(a, b);
    assertEquals(a.hashCode(), b.hashCode());
  }
}
