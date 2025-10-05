package com.pug.identity.usecase.role;

import static org.junit.jupiter.api.Assertions.assertFalse;
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
import com.pug.identity.usecase.role.activation.ChangeActiveRoleStatusCommand;
import com.pug.identity.usecase.role.activation.ChangeActiveRoleStatusHandler;
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
class ChangeActiveRoleStatusHandlerTest {

  @Mock RoleRepository repo;

  @SuppressWarnings("unused")
  @Spy
  Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

  @InjectMocks ChangeActiveRoleStatusHandler handler;

  private static Role role(UUID id, boolean active) {
    var u = User.builder().id(UUID.randomUUID()).cpf("93541134780").name("Ada").build();
    return Role.builder()
        .id(id)
        .user(u)
        .email("r@example.org")
        .role(UserRole.ADMIN)
        .active(active)
        .build();
  }

  @Test
  void togglesFromActiveToInactiveAndFlushes() {
    var id = UUID.randomUUID();
    var r = role(id, true);
    when(repo.findByIdOptional(id)).thenReturn(Optional.of(r));

    handler.handle(new ChangeActiveRoleStatusCommand(id));

    assertFalse(r.isActive());
    verify(repo).findByIdOptional(id);
    verify(repo).flush();
    verifyNoMoreInteractions(repo);
  }

  @Test
  void togglesFromInactiveToActiveAndFlushes() {
    var id = UUID.randomUUID();
    var r = role(id, false);
    when(repo.findByIdOptional(id)).thenReturn(Optional.of(r));

    handler.handle(new ChangeActiveRoleStatusCommand(id));

    assertTrue(r.isActive());
    verify(repo).findByIdOptional(id);
    verify(repo).flush();
    verifyNoMoreInteractions(repo);
  }

  @Test
  void invalidCommandIdTriggersConstraintViolation() {
    assertThrows(
        ConstraintViolationException.class,
        () -> handler.handle(new ChangeActiveRoleStatusCommand(null)));
    verifyNoInteractions(repo);
  }

  @Test
  void roleNotFoundThrows() {
    var id = UUID.randomUUID();
    when(repo.findByIdOptional(id)).thenReturn(Optional.empty());

    assertThrows(
        RoleNotFoundException.class, () -> handler.handle(new ChangeActiveRoleStatusCommand(id)));

    verify(repo).findByIdOptional(id);
    verifyNoMoreInteractions(repo);
  }
}
