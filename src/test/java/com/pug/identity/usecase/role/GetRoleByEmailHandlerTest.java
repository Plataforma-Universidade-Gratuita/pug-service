// src/test/java/com/pug/identity/usecase/role/get/byEmail/GetRoleByEmailHandlerTest.java
package com.pug.identity.usecase.role.get.byEmail;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.pug.identity.domain.Role;
import com.pug.identity.domain.User;
import com.pug.identity.domain.enums.UserRole;
import com.pug.identity.domain.exceptions.RoleNotFoundException;
import com.pug.identity.infra.persistence.RoleRepository;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GetRoleByEmailHandlerTest {

  @Mock RoleRepository repo;

  @SuppressWarnings("unused")
  @Spy
  Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

  @InjectMocks GetRoleByEmailHandler handler;

  private static Role role(String email) {
    var u = User.builder().id(UUID.randomUUID()).cpf("93541134780").name("Ada").build();
    return Role.builder().id(UUID.randomUUID()).user(u).email(email).role(UserRole.ADMIN).build();
  }

  @Test
  void returnsRoleWhenFoundWithTrimAndLowercaseNormalization() {
    var raw = "Admin@Example.ORG";
    var normalized = "admin@example.org";
    when(repo.findByEmail(normalized)).thenReturn(Optional.of(role(normalized)));

    var out = handler.handle(new GetRoleByEmailQuery(raw));

    assertNotNull(out);
    assertEquals(normalized, out.getEmail());
    verify(repo).findByEmail(normalized);
    verifyNoMoreInteractions(repo);
  }

  @Test
  void nullEmailThrowsConstraintViolationAndSkipsRepo() {
    assertThrows(
        ConstraintViolationException.class, () -> handler.handle(new GetRoleByEmailQuery(null)));
    verifyNoInteractions(repo);
  }

  @Test
  void blankEmailThrowsConstraintViolationAndSkipsRepo() {
    assertThrows(
        ConstraintViolationException.class, () -> handler.handle(new GetRoleByEmailQuery("  ")));
    verifyNoInteractions(repo);
  }

  @Test
  void invalidFormatEmail_throwsConstraintViolationAndSkipsRepo() {
    assertThrows(
        ConstraintViolationException.class, () -> handler.handle(new GetRoleByEmailQuery("bad")));
    verifyNoInteractions(repo);
  }

  @Test
  void tooLongEmailThrowsConstraintViolationAndSkipsRepo() {
    String local = "a".repeat(245);
    String email = local + "@example.org";
    assertTrue(email.length() > 254);

    assertThrows(
        ConstraintViolationException.class, () -> handler.handle(new GetRoleByEmailQuery(email)));
    verifyNoInteractions(repo);
  }

  @Test
  void notFoundThrowsRoleNotFoundException() {
    var email = "missing@example.org";
    when(repo.findByEmail(email)).thenReturn(Optional.empty());

    assertThrows(RoleNotFoundException.class, () -> handler.handle(new GetRoleByEmailQuery(email)));

    verify(repo).findByEmail(email);
    verifyNoMoreInteractions(repo);
  }
}
