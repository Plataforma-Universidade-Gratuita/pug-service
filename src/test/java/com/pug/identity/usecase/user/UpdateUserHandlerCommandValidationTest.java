package com.pug.identity.usecase.user;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verifyNoInteractions;

import com.pug.identity.infra.persistence.UserRepository;
import com.pug.identity.usecase.user.update.UpdateUserCommand;
import com.pug.identity.usecase.user.update.UpdateUserHandler;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UpdateUserHandlerCommandValidationTest {

  @Mock UserRepository repo;

  @SuppressWarnings("unused")
  @Spy
  Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

  @InjectMocks UpdateUserHandler handler;

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
