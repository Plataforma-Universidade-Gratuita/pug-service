package com.pug.identity.usecase.role;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.pug.identity.domain.Role;
import com.pug.identity.domain.User;
import com.pug.identity.domain.enums.UserRole;
import com.pug.identity.domain.exceptions.RoleNotFoundException;
import com.pug.identity.infra.persistence.RoleRepository;
import com.pug.identity.usecase.role.get.byId.GetRoleByIdHandler;
import com.pug.identity.usecase.role.get.byId.GetRoleByIdQuery;
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
class GetRoleByIdHandlerTest {

  @Mock RoleRepository repo;

  @SuppressWarnings("unused")
  @Spy
  Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

  @InjectMocks GetRoleByIdHandler handler;

  private static Role role(UUID id) {
    var u = User.builder().id(UUID.randomUUID()).cpf("93541134780").name("Ada").build();
    return Role.builder().id(id).user(u).email("r@example.org").role(UserRole.ADMIN).build();
  }

  @Test
  void returnsRoleWhenFound() {
    var id = UUID.randomUUID();
    var r = role(id);
    when(repo.findByIdOptional(id)).thenReturn(Optional.of(r));

    var out = handler.handle(new GetRoleByIdQuery(id));

    assertNotNull(out);
    assertEquals(id, out.getId());
    verify(repo).findByIdOptional(id);
    verifyNoMoreInteractions(repo);
  }

  @Test
  void throwsWhenNotFound() {
    var id = UUID.randomUUID();
    when(repo.findByIdOptional(id)).thenReturn(Optional.empty());

    assertThrows(RoleNotFoundException.class, () -> handler.handle(new GetRoleByIdQuery(id)));

    verify(repo).findByIdOptional(id);
    verifyNoMoreInteractions(repo);
  }

  @Test
  void throwsWhenIdIsNull() {
    assertThrows(
        ConstraintViolationException.class, () -> handler.handle(new GetRoleByIdQuery(null)));
    verifyNoInteractions(repo);
  }
}
