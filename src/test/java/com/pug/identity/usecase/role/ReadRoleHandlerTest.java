package com.pug.identity.usecase.role;

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
import com.pug.identity.usecase.role.read.ReadRoleByEmailQuery;
import com.pug.identity.usecase.role.read.ReadRoleHandler;
import com.pug.shared.dtos.ReadByIdQuery;
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
class ReadRoleHandlerTest {

  @Mock RoleRepository repo;

  @SuppressWarnings("unused")
  @Spy
  Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

  @InjectMocks ReadRoleHandler handler;

  private static Role role(String email) {
    var u = User.builder().id(UUID.randomUUID()).cpf("93541134780").name("Ada").build();
    return Role.builder().id(UUID.randomUUID()).user(u).email(email).role(UserRole.ADMIN).build();
  }

  @Test
  void returnsRoleWhenFoundWithTrimAndLowercaseNormalization() {
    var raw = "Admin@Example.ORG";
    var normalized = "admin@example.org";
    when(repo.findByEmail(normalized)).thenReturn(Optional.of(role(normalized)));

    var out = handler.handle(new ReadRoleByEmailQuery(raw));

    assertNotNull(out);
    assertEquals(normalized, out.getEmail());
    verify(repo).findByEmail(normalized);
    verifyNoMoreInteractions(repo);
  }

  @Test
  void nullEmailThrowsConstraintViolationAndSkipsRepo() {
    assertThrows(
        ConstraintViolationException.class, () -> handler.handle(new ReadRoleByEmailQuery(null)));
    verifyNoInteractions(repo);
  }

  @Test
  void blankEmailThrowsConstraintViolationAndSkipsRepo() {
    assertThrows(
        ConstraintViolationException.class, () -> handler.handle(new ReadRoleByEmailQuery("  ")));
    verifyNoInteractions(repo);
  }

  @Test
  void invalidFormatEmail_throwsConstraintViolationAndSkipsRepo() {
    assertThrows(
        ConstraintViolationException.class, () -> handler.handle(new ReadRoleByEmailQuery("bad")));
    verifyNoInteractions(repo);
  }

  @Test
  void tooLongEmailThrowsConstraintViolationAndSkipsRepo() {
    String local = "a".repeat(245);
    String email = local + "@example.org";
    assertTrue(email.length() > 254);

    assertThrows(
        ConstraintViolationException.class, () -> handler.handle(new ReadRoleByEmailQuery(email)));
    verifyNoInteractions(repo);
  }

  @Test
  void notFoundThrowsRoleNotFoundException() {
    var email = "missing@example.org";
    when(repo.findByEmail(email)).thenReturn(Optional.empty());

    assertThrows(
        RoleNotFoundException.class, () -> handler.handle(new ReadRoleByEmailQuery(email)));

    verify(repo).findByEmail(email);
    verifyNoMoreInteractions(repo);
  }

  private static Role role(UUID id) {
    var u = User.builder().id(UUID.randomUUID()).cpf("93541134780").name("Ada").build();
    return Role.builder().id(id).user(u).email("r@example.org").role(UserRole.ADMIN).build();
  }

  @Test
  void returnsRoleWhenFound() {
    var id = UUID.randomUUID();
    var r = role(id);
    when(repo.findByIdOptional(id)).thenReturn(Optional.of(r));

    var out = handler.handle(new ReadByIdQuery(id));

    assertNotNull(out);
    assertEquals(id, out.getId());
    verify(repo).findByIdOptional(id);
    verifyNoMoreInteractions(repo);
  }

  @Test
  void throwsWhenNotFound() {
    var id = UUID.randomUUID();
    when(repo.findByIdOptional(id)).thenReturn(Optional.empty());

    assertThrows(RoleNotFoundException.class, () -> handler.handle(new ReadByIdQuery(id)));

    verify(repo).findByIdOptional(id);
    verifyNoMoreInteractions(repo);
  }

  @Test
  void throwsWhenIdIsNull() {
    assertThrows(ConstraintViolationException.class, () -> handler.handle(new ReadByIdQuery(null)));
    verifyNoInteractions(repo);
  }
}
