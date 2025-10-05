package com.pug.identity.usecase.user;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.pug.identity.domain.User;
import com.pug.identity.domain.exceptions.DuplicateCpfException;
import com.pug.identity.infra.persistence.UserRepository;
import com.pug.identity.usecase.user.create.CreateUserCommand;
import com.pug.identity.usecase.user.create.CreateUserHandler;
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
class CreateUserHandlerTest {

  @Mock UserRepository repo;

  @SuppressWarnings("unused")
  @Spy
  Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

  @InjectMocks CreateUserHandler handler;

  private static final String VALID_MASKED = "935.411.347-80";
  private static final String VALID_DIGITS = "93541134780";
  private static final UUID FIXED = UUID.fromString("00000000-0000-7000-8000-000000000001");

  @Test
  void createUserSuccessPersistsFlushesAndReturnsId() {
    when(repo.existsByCpf(VALID_DIGITS)).thenReturn(false);
    doAnswer(
            inv -> {
              User u = inv.getArgument(0);
              u.setId(FIXED);
              return null;
            })
        .when(repo)
        .persist(any(User.class));

    UUID id = handler.handle(new CreateUserCommand(VALID_MASKED, "Ada"));

    assertNotNull(id);

    InOrder io = inOrder(repo);
    io.verify(repo).existsByCpf(VALID_DIGITS);
    io.verify(repo).persist(any(User.class));
    io.verify(repo).flush();
    verifyNoMoreInteractions(repo);
  }

  @Test
  void createUserFailsOnValidation() {
    assertThrows(
        ConstraintViolationException.class,
        () -> handler.handle(new CreateUserCommand("invalid", " ")));
    verify(repo, never()).persist(any(User.class));
    verify(repo, never()).flush();
  }

  @Test
  void createUserFailsOnDuplicateCpfFastPath() {
    when(repo.existsByCpf(VALID_DIGITS)).thenReturn(true);

    assertThrows(
        DuplicateCpfException.class,
        () -> handler.handle(new CreateUserCommand(VALID_MASKED, "Ada")));

    verify(repo).existsByCpf(VALID_DIGITS);
    verify(repo, never()).persist(any(User.class));
    verify(repo, never()).flush();
  }

  @Test
  void createUserNormalizesMaskedBeforeChecks() {
    when(repo.existsByCpf(VALID_DIGITS)).thenReturn(false);

    handler.handle(new CreateUserCommand(VALID_MASKED, "Ada"));

    verify(repo).existsByCpf(VALID_DIGITS);
  }
}
