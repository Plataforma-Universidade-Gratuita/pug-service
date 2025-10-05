package com.pug.identity.usecase.user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.pug.identity.domain.User;
import com.pug.identity.infra.persistence.UserRepository;
import com.pug.identity.usecase.user.get.byCpf.GetUserByCpfHandler;
import com.pug.identity.usecase.user.get.byCpf.GetUserByCpfQuery;
import com.pug.shared.errors.DomainException;
import com.pug.shared.errors.ErrorCodes;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.util.Optional;
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

  private static final String MASKED = "935.411.347-80";
  private static final String DIGITS = "93541134780";

  @Test
  void returnsEntityWhenFound_normalizesCpf() {
    var u = User.builder().id(java.util.UUID.randomUUID()).cpf(DIGITS).name("Ada").build();
    when(repo.findByCpf(DIGITS)).thenReturn(Optional.of(u));

    var out = handler.handle(new GetUserByCpfQuery(MASKED));

    assertSame(u, out);
    verify(repo).findByCpf(DIGITS);
    verifyNoMoreInteractions(repo);
  }

  @Test
  void throwsNotFoundWithCodeAndCpfDigits() {
    when(repo.findByCpf(DIGITS)).thenReturn(Optional.empty());

    DomainException ex =
        assertThrows(DomainException.class, () -> handler.handle(new GetUserByCpfQuery(MASKED)));

    assertEquals(ErrorCodes.USER_NOT_FOUND, ex.getCode());
    assertEquals(1, ex.getArgs().length);
    assertEquals(DIGITS, ex.getArgs()[0]);
    verify(repo).findByCpf(DIGITS);
    verifyNoMoreInteractions(repo);
  }
}
