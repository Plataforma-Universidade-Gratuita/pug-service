package com.pug.identity.usecase.user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.pug.identity.domain.User;
import com.pug.identity.domain.exceptions.DuplicateCpfException;
import com.pug.identity.domain.exceptions.UserNotFoundException;
import com.pug.identity.infra.persistence.UserRepository;
import com.pug.identity.usecase.user.update.UpdateUserCommand;
import com.pug.identity.usecase.user.update.UpdateUserHandler;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UpdateUserHandlerTest {

  @Mock UserRepository repo;

  @SuppressWarnings("unused")
  @Spy
  Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

  @InjectMocks UpdateUserHandler handler;

  private static final String CPF_OLD = "28612017004";
  private static final String CPF_NEW_MASKED = "935.411.347-80";
  private static final String CPF_NEW = "93541134780";

  @Test
  void updateUserSuccessAdminUpdatesAndFlushes() {
    UUID id = UUID.randomUUID();
    User existing = User.builder().id(id).cpf(CPF_OLD).name("Alan").build();
    when(repo.findByIdOptional(id)).thenReturn(Optional.of(existing));
    when(repo.existsByCpfForAnother(CPF_NEW, id)).thenReturn(false);

    User out = handler.handle(new UpdateUserCommand(id, CPF_NEW_MASKED, "Alan Turing"));

    assertEquals(CPF_NEW, out.getCpf());
    assertEquals("Alan Turing", out.getName());

    InOrder io = inOrder(repo);
    io.verify(repo).findByIdOptional(id);
    io.verify(repo).existsByCpfForAnother(CPF_NEW, id);
    io.verify(repo).flush();
    verifyNoMoreInteractions(repo);
  }

  @Test
  void updateUserFailsWhenUserNotFound() {
    UUID id = UUID.randomUUID();
    when(repo.findByIdOptional(id)).thenReturn(Optional.empty());

    assertThrows(
        UserNotFoundException.class, () -> handler.handle(new UpdateUserCommand(id, CPF_NEW, "X")));

    verify(repo).findByIdOptional(id);
    verifyNoMoreInteractions(repo);
  }

  @Test
  void updateUserFailsOnValidationAfterSetters() {
    UUID id = UUID.randomUUID();
    User existing = User.builder().id(id).cpf(CPF_OLD).name("Alan").build();
    when(repo.findByIdOptional(id)).thenReturn(Optional.of(existing));

    assertThrows(
        ConstraintViolationException.class,
        () -> handler.handle(new UpdateUserCommand(id, "invalid", " ")));

    verify(repo).findByIdOptional(id);
    verify(repo).existsByCpfForAnother("", id);
    verify(repo, never()).flush();
    verifyNoMoreInteractions(repo);
  }

  @Test
  void updateUserFailsOnDuplicateCpfOtherUser() {
    UUID id = UUID.randomUUID();
    User existing = User.builder().id(id).cpf(CPF_OLD).name("Alan").build();
    when(repo.findByIdOptional(id)).thenReturn(Optional.of(existing));
    when(repo.existsByCpfForAnother(CPF_NEW, id)).thenReturn(true); // digits

    assertThrows(
        DuplicateCpfException.class,
        () -> handler.handle(new UpdateUserCommand(id, CPF_NEW_MASKED, "Alan")));

    verify(repo).findByIdOptional(id);
    verify(repo).existsByCpfForAnother(CPF_NEW, id);
    verify(repo, never()).flush();
    verifyNoMoreInteractions(repo);
  }

  @Test
  void updateUserNormalizesMaskedBeforeDuplicateCheck() {
    UUID id = UUID.randomUUID();
    User existing = User.builder().id(id).cpf(CPF_OLD).name("Alan").build();
    when(repo.findByIdOptional(id)).thenReturn(Optional.of(existing));
    when(repo.existsByCpfForAnother(CPF_NEW, id)).thenReturn(false);

    handler.handle(new UpdateUserCommand(id, CPF_NEW_MASKED, "Alan Turing"));

    verify(repo).existsByCpfForAnother(CPF_NEW, id);
  }
}
