package com.pug.identity.usecase.user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.pug.identity.domain.User;
import com.pug.identity.infra.persistence.UserRepository;
import com.pug.identity.usecase.user.read.ReadUserByCpfQuery;
import com.pug.identity.usecase.user.read.ReadUserByIdQuery;
import com.pug.identity.usecase.user.read.ReadUserHandler;
import com.pug.shared.errors.DomainException;
import com.pug.shared.errors.ErrorCodes;
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
class RetrieveUserHandlerTest {

  @Mock UserRepository repo;

  @SuppressWarnings("unused")
  @Spy
  Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

  @InjectMocks
  ReadUserHandler handler;

  @Test
  void nullCpfFailsAndSkipsRepo() {
    assertThrows(
        ConstraintViolationException.class, () -> handler.handle(new ReadUserByCpfQuery(null)));
    verifyNoInteractions(repo);
  }

  @Test
  void blankCpfFailsAndSkipsRepo() {
    assertThrows(
        ConstraintViolationException.class, () -> handler.handle(new ReadUserByCpfQuery("  ")));
    verifyNoInteractions(repo);
  }

  @Test
  void invalidCpfFailsAndSkipsRepo() {
    assertThrows(
        ConstraintViolationException.class,
        () -> handler.handle(new ReadUserByCpfQuery("1234567890a")));
    verifyNoInteractions(repo);
  }

  @Test
  void returnsEntityWhenFound() {
    UUID id = UUID.randomUUID();
    var u = User.builder().id(id).cpf("93541134780").name("Ada").build();
    when(repo.findByIdOptional(id)).thenReturn(Optional.of(u));

    var out = handler.handle(new ReadUserByIdQuery(id));

    assertSame(u, out);
    verify(repo).findByIdOptional(id);
    verifyNoMoreInteractions(repo);
  }

  @Test
  void throwsNotFoundWithCodeAndId() {
    UUID id = UUID.randomUUID();
    when(repo.findByIdOptional(id)).thenReturn(Optional.empty());

    DomainException ex =
        assertThrows(DomainException.class, () -> handler.handle(new ReadUserByIdQuery(id)));

    assertEquals(ErrorCodes.USER_NOT_FOUND, ex.getCode());
    assertEquals(1, ex.getArgs().length);
    assertEquals(id, ex.getArgs()[0]);
    verify(repo).findByIdOptional(id);
    verifyNoMoreInteractions(repo);
  }

  @Test
  void nullIdFailsValidationAndSkipsRepo() {
    assertThrows(
        ConstraintViolationException.class, () -> handler.handle(new ReadUserByIdQuery(null)));
    verifyNoMoreInteractions(repo);
  }
}
