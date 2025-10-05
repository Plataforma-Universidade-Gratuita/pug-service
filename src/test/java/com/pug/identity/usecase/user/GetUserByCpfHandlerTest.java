package com.pug.identity.usecase.user;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verifyNoInteractions;

import com.pug.identity.infra.persistence.UserRepository;
import com.pug.identity.usecase.user.get.byCpf.GetUserByCpfHandler;
import com.pug.identity.usecase.user.get.byCpf.GetUserByCpfQuery;
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
class GetUserByCpfHandlerTest {

  @Mock UserRepository repo;

  @SuppressWarnings("unused")
  @Spy
  Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

  @InjectMocks GetUserByCpfHandler handler;

  @Test
  void nullCpfFailsAndSkipsRepo() {
    assertThrows(
        ConstraintViolationException.class, () -> handler.handle(new GetUserByCpfQuery(null)));
    verifyNoInteractions(repo);
  }

  @Test
  void blankCpfFailsAndSkipsRepo() {
    assertThrows(
        ConstraintViolationException.class, () -> handler.handle(new GetUserByCpfQuery("  ")));
    verifyNoInteractions(repo);
  }

  @Test
  void invalidCpfFailsAndSkipsRepo() {
    assertThrows(
        ConstraintViolationException.class,
        () -> handler.handle(new GetUserByCpfQuery("1234567890a")));
    verifyNoInteractions(repo);
  }
}
