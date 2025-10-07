package com.pug.identity.usecase.user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
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

  private static final String CPF_OLD = "93541134780";
  private static final String CPF_NEW_MASKED = "390.533.447-05";
  private static final String CPF_NEW = "39053344705";

  @Test
  void successUpdatesAndFlushes() {
    UUID id = UUID.randomUUID();
    var existing = User.builder().id(id).cpf(CPF_OLD).name("Alan").build();
    when(repo.findByIdOptional(id)).thenReturn(Optional.of(existing));
    when(repo.existsByCpfForAnother(CPF_NEW, id)).thenReturn(false);

    var out = handler.handle(new UpdateUserCommand(id, CPF_NEW_MASKED, "Alan Turing"));

    assertEquals(CPF_NEW, out.getCpf());
    assertEquals("Alan Turing", out.getName());
    InOrder io = inOrder(repo);
    io.verify(repo).findByIdOptional(id);
    io.verify(repo).existsByCpfForAnother(CPF_NEW, id);
    io.verify(repo).flush();
    verifyNoMoreInteractions(repo);
  }

  @Test
  void userNotFoundThrows() {
    UUID id = UUID.randomUUID();
    when(repo.findByIdOptional(id)).thenReturn(Optional.empty());

    assertThrows(
        UserNotFoundException.class,
        () -> handler.handle(new UpdateUserCommand(id, CPF_NEW_MASKED, "X")));

    verify(repo).findByIdOptional(id);
    verifyNoMoreInteractions(repo);
  }

  @Test
  void duplicateCpfThrowsBeforeMutate() {
    UUID id = UUID.randomUUID();
    var existing = User.builder().id(id).cpf(CPF_OLD).name("Alan").build();
    when(repo.findByIdOptional(id)).thenReturn(Optional.of(existing));
    when(repo.existsByCpfForAnother(CPF_NEW, id)).thenReturn(true);

    assertThrows(
        DuplicateCpfException.class,
        () -> handler.handle(new UpdateUserCommand(id, CPF_NEW_MASKED, "Alan")));

    verify(repo).findByIdOptional(id);
    verify(repo).existsByCpfForAnother(CPF_NEW, id);
    verify(repo, never()).flush();
    verifyNoMoreInteractions(repo);
  }

  @Test
  void unchangedCpfAllowsNameChangeOnlyChecksDuplicateOnOldCpf() {
    UUID id = UUID.randomUUID();
    var existing = User.builder().id(id).cpf(CPF_OLD).name("Alan").build();
    when(repo.findByIdOptional(id)).thenReturn(Optional.of(existing));
    when(repo.existsByCpfForAnother(CPF_OLD, id)).thenReturn(false);

    var out = handler.handle(new UpdateUserCommand(id, CPF_OLD, "Alan M. Turing"));

    assertEquals(CPF_OLD, out.getCpf());
    assertEquals("Alan M. Turing", out.getName());
    verify(repo).existsByCpfForAnother(CPF_OLD, id);
    verify(repo).flush();
  }

  @Test
  void maskedCpfIsNormalizedBeforeDuplicateCheck() {
    UUID id = UUID.randomUUID();
    var existing = User.builder().id(id).cpf(CPF_OLD).name("Alan").build();
    when(repo.findByIdOptional(id)).thenReturn(Optional.of(existing));
    when(repo.existsByCpfForAnother(CPF_NEW, id)).thenReturn(false);

    handler.handle(new UpdateUserCommand(id, CPF_NEW_MASKED, "Alan Turing"));

    verify(repo).existsByCpfForAnother(CPF_NEW, id);
  }

  @Test
  void nullIdFailsAndSkipsRepo() {
    assertThrows(
        ConstraintViolationException.class,
        () -> handler.handle(new UpdateUserCommand(null, "935.411.347-80", "Ada")));
    verifyNoInteractions(repo);
  }

  @Test
  void blankCpfFailsAndSkipsRepo() {
    assertThrows(
        ConstraintViolationException.class,
        () -> handler.handle(new UpdateUserCommand(UUID.randomUUID(), "   ", "Ada")));
    verifyNoInteractions(repo);
  }

  @Test
  void invalidCpfFormatFailsAndSkipsRepo() {
    assertThrows(
        ConstraintViolationException.class,
        () -> handler.handle(new UpdateUserCommand(UUID.randomUUID(), "1234567890a", "Ada")));
    verifyNoInteractions(repo);
  }

  @Test
  void blankNameFailsAndSkipsRepo() {
    assertThrows(
        ConstraintViolationException.class,
        () -> handler.handle(new UpdateUserCommand(UUID.randomUUID(), "935.411.347-80", " ")));
    verifyNoInteractions(repo);
  }

  @Test
  void nameOver150FailsAndSkipsRepo() {
    assertThrows(
        ConstraintViolationException.class,
        () ->
            handler.handle(
                new UpdateUserCommand(UUID.randomUUID(), "935.411.347-80", "x".repeat(151))));
    verifyNoInteractions(repo);
  }
}
