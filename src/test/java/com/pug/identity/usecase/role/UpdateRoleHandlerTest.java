package com.pug.identity.usecase.role;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.pug.identity.domain.Role;
import com.pug.identity.domain.User;
import com.pug.identity.domain.enums.UserRole;
import com.pug.identity.domain.exceptions.DuplicateEmailException;
import com.pug.identity.domain.exceptions.FormerStudentRegistrationException;
import com.pug.identity.domain.exceptions.RoleNotFoundException;
import com.pug.identity.infra.persistence.RoleRepository;
import com.pug.identity.usecase.role.update.UpdateRoleCommand;
import com.pug.identity.usecase.role.update.UpdateRoleHandler;
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
class UpdateRoleHandlerTest {

  @Mock RoleRepository repo;

  @SuppressWarnings("unused")
  @Spy
  Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

  @InjectMocks UpdateRoleHandler handler;

  private static final String VALID_CPF = "93541134780";

  private static User user(UUID id) {
    return User.builder().id(id).cpf(VALID_CPF).name("Ada").build();
  }

  private static Role role(UUID id, User u, String email, UserRole r) {
    return Role.builder().id(id).user(u).email(email).role(r).build();
  }

  @Test
  void updateRoleSuccessFlushesAndReturnsEntity() {
    var uid = UUID.randomUUID();
    var rid = UUID.randomUUID();
    var u = user(uid);
    var existing = role(rid, u, "old@example.org", UserRole.ADMIN);

    when(repo.findByIdOptional(rid)).thenReturn(Optional.of(existing));
    when(repo.existsByEmailForAnother("new@example.org", rid)).thenReturn(false);

    var cmd = new UpdateRoleCommand(rid, "new@example.org", UserRole.PARTNER);
    var out = handler.handle(cmd);

    assertNotNull(out);
    assertEquals(rid, out.getId());
    assertEquals("new@example.org", out.getEmail());
    assertEquals(UserRole.PARTNER, out.getRole());

    verify(repo).findByIdOptional(rid);
    verify(repo).existsByEmailForAnother("new@example.org", rid);
    verify(repo, never()).existsFormerStudentForAnother(any(), any());
    verify(repo).flush();
    verifyNoMoreInteractions(repo);
  }

  @Test
  void duplicateEmailFastPathThrows() {
    var uid = UUID.randomUUID();
    var rid = UUID.randomUUID();
    var u = user(uid);
    var existing = role(rid, u, "old@example.org", UserRole.ADMIN);

    when(repo.findByIdOptional(rid)).thenReturn(Optional.of(existing));
    when(repo.existsByEmailForAnother("dup@example.org", rid)).thenReturn(true);

    var cmd = new UpdateRoleCommand(rid, "dup@example.org", UserRole.ADMIN);

    assertThrows(DuplicateEmailException.class, () -> handler.handle(cmd));

    verify(repo).findByIdOptional(rid);
    verify(repo).existsByEmailForAnother("dup@example.org", rid);
    verify(repo, never()).existsFormerStudentForAnother(any(), any());
    verify(repo, never()).flush();
    verifyNoMoreInteractions(repo);
  }

  @Test
  void updatingToFormerStudentChecksUniquenessAndThrows() {
    var uid = UUID.randomUUID();
    var rid = UUID.randomUUID();
    var u = user(uid);
    var existing = role(rid, u, "old@example.org", UserRole.ADMIN);

    when(repo.findByIdOptional(rid)).thenReturn(Optional.of(existing));
    when(repo.existsByEmailForAnother("fs@example.org", rid)).thenReturn(false);
    when(repo.existsFormerStudentForAnother(uid, rid)).thenReturn(true);

    var cmd = new UpdateRoleCommand(rid, "fs@example.org", UserRole.FORMER_STUDENT);

    assertThrows(FormerStudentRegistrationException.class, () -> handler.handle(cmd));

    verify(repo).findByIdOptional(rid);
    verify(repo).existsByEmailForAnother("fs@example.org", rid);
    verify(repo).existsFormerStudentForAnother(uid, rid);
    verify(repo, never()).flush();
    verifyNoMoreInteractions(repo);
  }

  @Test
  void roleNotFoundThrows() {
    var rid = UUID.randomUUID();
    when(repo.findByIdOptional(rid)).thenReturn(Optional.empty());

    var cmd = new UpdateRoleCommand(rid, "a@b.org", UserRole.ADMIN);

    assertThrows(RoleNotFoundException.class, () -> handler.handle(cmd));

    verify(repo).findByIdOptional(rid);
    verifyNoMoreInteractions(repo);
  }

  @Test
  void commandValidationFailureThrowsConstraintViolation() {
    var rid = UUID.randomUUID();
    var cmd = new UpdateRoleCommand(rid, "bad", UserRole.ADMIN);

    assertThrows(ConstraintViolationException.class, () -> handler.handle(cmd));

    verifyNoInteractions(repo);
  }
}
