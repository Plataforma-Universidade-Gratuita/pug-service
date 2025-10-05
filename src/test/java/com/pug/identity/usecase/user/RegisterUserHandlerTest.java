package com.pug.identity.usecase.user;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.pug.identity.infra.persistence.UserRepository;
import com.pug.identity.usecase.user.create.RegisterUserCommand;
import com.pug.identity.usecase.user.create.RegisterUserHandler;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RegisterUserHandlerTest {

  @Mock UserRepository repo;

  @SuppressWarnings("unused")
  @Spy
  Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

  @InjectMocks RegisterUserHandler handler;

  @Test
  void nullCpfFailsAndSkipsRepo() {
    assertThrows(
        ConstraintViolationException.class,
        () -> handler.handle(new RegisterUserCommand(null, "Ada")));
    verifyNoInteractions(repo);
  }

  @Test
  void blankCpfFailsAndSkipsRepo() {
    assertThrows(
        ConstraintViolationException.class,
        () -> handler.handle(new RegisterUserCommand("  ", "Ada")));
    verifyNoInteractions(repo);
  }

  @Test
  void invalidCpfFailsAndSkipsRepo() {
    assertThrows(
        ConstraintViolationException.class,
        () -> handler.handle(new RegisterUserCommand("1234567890a", "Ada")));
    verifyNoInteractions(repo);
  }

  @Test
  void blankNameFailsAndSkipsRepo() {
    assertThrows(
        ConstraintViolationException.class,
        () -> handler.handle(new RegisterUserCommand("935.411.347-80", " ")));
    verifyNoInteractions(repo);
  }

  @Test
  void overlongNameFailsAndSkipsRepo() {
    assertThrows(
        ConstraintViolationException.class,
        () -> handler.handle(new RegisterUserCommand("935.411.347-80", "x".repeat(151))));
    verifyNoInteractions(repo);
  }

  @Test
  void acceptsDigitsCpfWithoutMask() {
    when(repo.existsByCpf("93541134780")).thenReturn(false);
    handler.handle(new RegisterUserCommand("93541134780", "Ada"));
    verify(repo).existsByCpf("93541134780");
  }

  @Test
  void maskedCpfIsNormalizedBeforePersist() {
    when(repo.existsByCpf("93541134780")).thenReturn(false);
    handler.handle(new RegisterUserCommand("935.411.347-80", "Ada"));
    verify(repo).existsByCpf("93541134780");
  }
}
