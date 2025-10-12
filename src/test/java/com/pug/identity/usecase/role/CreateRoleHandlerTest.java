package com.pug.identity.usecase.role;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.pug.identity.domain.Role;
import com.pug.identity.domain.User;
import com.pug.identity.domain.enums.UserRole;
import com.pug.identity.domain.exceptions.DuplicateEmailException;
import com.pug.identity.domain.exceptions.FormerStudentRegistrationException;
import com.pug.identity.infra.persistence.RoleRepository;
import com.pug.identity.usecase.role.create.CreateRoleCommand;
import com.pug.identity.usecase.role.create.CreateRoleHandler;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CreateRoleHandlerTest {

  @Mock RoleRepository repo;

  @SuppressWarnings("unused")
  @Spy
  Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

  @InjectMocks
  CreateRoleHandler handler;

  private static final String VALID_CPF = "93541134780";
  private static final UUID FIXED = UUID.fromString("00000000-0000-7000-8000-000000000001");

  private static User user(UUID id) {
    return User.builder().id(id).cpf(VALID_CPF).name("Ada").build();
  }

  @Test
  void createRoleSuccessPersistsFlushesAndReturnsId() {
    var uid = UUID.randomUUID();
    when(repo.existsByEmail("admin@example.org")).thenReturn(false);
    doAnswer(
            inv -> {
              Role r = inv.getArgument(0);
              r.setId(FIXED);
              return null;
            })
        .when(repo)
        .persist(any(Role.class));

    var id = handler.handle(new CreateRoleCommand(uid, "admin@example.org", UserRole.ADMIN));

    assertNotNull(id);

    InOrder io = inOrder(repo);
    io.verify(repo).existsByEmail("admin@example.org");
    io.verify(repo).persist(any(Role.class));
    io.verify(repo).flush();
    verify(repo, never()).existsFormerStudentByUserId(any());
    verifyNoMoreInteractions(repo);
  }

  @Test
  void createRoleFailsOnValidation() {
    assertThrows(
        ConstraintViolationException.class,
        () -> handler.handle(new CreateRoleCommand(UUID.randomUUID(), "bad", UserRole.ADMIN)));
    verify(repo, never()).persist(any(Role.class));
    verify(repo, never()).flush();
  }

  @Test
  void createRoleFailsOnDuplicateEmailFastPath() {
    when(repo.existsByEmail("dup@example.org")).thenReturn(true);

    assertThrows(
        DuplicateEmailException.class,
        () ->
            handler.handle(
                new CreateRoleCommand(UUID.randomUUID(), "dup@example.org", UserRole.PARTNER)));

    verify(repo).existsByEmail("dup@example.org");
    verify(repo, never()).existsFormerStudentByUserId(any());
    verify(repo, never()).persist(any(Role.class));
    verify(repo, never()).flush();
  }

  @Test
  void createRoleFailsOnSecondFormerStudent() {
    var uid = UUID.randomUUID();
    when(repo.existsByEmail("fs@example.org")).thenReturn(false);
    when(repo.existsFormerStudentByUserId(uid)).thenReturn(true);

    assertThrows(
        FormerStudentRegistrationException.class,
        () ->
            handler.handle(
                new CreateRoleCommand(uid, "fs@example.org", UserRole.FORMER_STUDENT)));

    InOrder io = inOrder(repo);
    io.verify(repo).existsByEmail("fs@example.org");
    io.verify(repo).existsFormerStudentByUserId(uid);
    verifyNoMoreInteractions(repo);
  }

  @Test
  void multiplePartnerRolesAreAllowed() {
    var uid = UUID.randomUUID();
    when(repo.existsByEmail("p1@example.org")).thenReturn(false);
    when(repo.existsByEmail("p2@example.org")).thenReturn(false);
    doAnswer(
            inv -> {
              Role r = inv.getArgument(0);
              r.setId(UUID.randomUUID());
              return null;
            })
        .when(repo)
        .persist(any(Role.class));

    var id1 = handler.handle(new CreateRoleCommand(uid, "p1@example.org", UserRole.PARTNER));
    var id2 = handler.handle(new CreateRoleCommand(uid, "p2@example.org", UserRole.PARTNER));

    assertNotNull(id1);
    assertNotNull(id2);

    verify(repo).existsByEmail("p1@example.org");
    verify(repo).existsByEmail("p2@example.org");
    verify(repo, times(2)).persist(any(Role.class));
    verify(repo, times(2)).flush();
    verify(repo, never()).existsFormerStudentByUserId(any());
    verifyNoMoreInteractions(repo);
  }

  @Test
  void createRoleNormalizesEmailLowercaseBeforeChecks() {
    var uid = UUID.randomUUID();
    when(repo.existsByEmail("admin@example.org")).thenReturn(false);
    doAnswer(
            inv -> {
              Role r = inv.getArgument(0);
              r.setId(UUID.randomUUID());
              return null;
            })
        .when(repo)
        .persist(any(Role.class));

    var id = handler.handle(new CreateRoleCommand(uid, "Admin@Example.ORG", UserRole.ADMIN));

    assertNotNull(id);
    InOrder io = inOrder(repo);
    io.verify(repo).existsByEmail("admin@example.org"); // lowercased
    io.verify(repo).persist(any(Role.class));
    io.verify(repo).flush();
    verifyNoMoreInteractions(repo);
  }

  @Test
  void createFormerStudentSuccessPersistsAndFlushes() {
    var uid = UUID.randomUUID();
    when(repo.existsByEmail("fs@example.org")).thenReturn(false);
    when(repo.existsFormerStudentByUserId(uid)).thenReturn(false);
    doAnswer(
            inv -> {
              Role r = inv.getArgument(0);
              r.setId(UUID.randomUUID());
              return null;
            })
        .when(repo)
        .persist(any(Role.class));

    var id =
        handler.handle(new CreateRoleCommand(uid, "fs@example.org", UserRole.FORMER_STUDENT));

    assertNotNull(id);
    InOrder io = inOrder(repo);
    io.verify(repo).existsByEmail("fs@example.org");
    io.verify(repo).existsFormerStudentByUserId(uid);
    io.verify(repo).persist(any(Role.class));
    io.verify(repo).flush();
    verifyNoMoreInteractions(repo);
  }

  @Test
  void nullUserTriggersConstraintViolationAndSkipsRepo() {
    assertThrows(
        ConstraintViolationException.class,
        () -> handler.handle(new CreateRoleCommand(null, "a@b.org", UserRole.ADMIN)));
    verifyNoInteractions(repo);
  }

  @Test
  void blankEmailTriggersConstraintViolationAndSkipsRepo() {
    assertThrows(
        ConstraintViolationException.class,
        () -> handler.handle(new CreateRoleCommand(UUID.randomUUID(), "  ", UserRole.ADMIN)));
    verifyNoInteractions(repo);
  }

  @Test
  void tooLongEmailTriggersConstraintViolationAndSkipsRepo() {
    String local = "a".repeat(245); // 245 + 12 = 257
    String email = local + "@example.org";
    assertThrows(
        ConstraintViolationException.class,
        () -> handler.handle(new CreateRoleCommand(UUID.randomUUID(), email, UserRole.ADMIN)));
    verifyNoInteractions(repo);
  }
}
