package com.pug.academic.usecase.fieldOfStudy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.pug.academic.domain.FieldOfStudy;
import com.pug.academic.domain.exceptions.FieldOfStudyNotFoundException;
import com.pug.academic.infra.persistence.FieldOfStudyRepository;
import com.pug.academic.usecase.fieldOfStudy.read.ReadFieldOfStudyHandler;
import com.pug.shared.dtos.ReadByIdQuery;
import com.pug.shared.dtos.ReadByPatternQuery;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ReadFieldOfStudyHandlerTest {

  @Mock FieldOfStudyRepository repo;

  @SuppressWarnings("unused")
  @Spy
  Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

  @InjectMocks ReadFieldOfStudyHandler handler;

  @Test
  void readByIdNullIdFailsAndSkipsRepo() {
    assertThrows(ConstraintViolationException.class, () -> handler.handle(new ReadByIdQuery(null)));
    verifyNoInteractions(repo);
  }

  @Test
  void readByIdNotFoundThrows404() {
    UUID id = UUID.randomUUID();
    when(repo.findByIdOptional(id)).thenReturn(Optional.empty());
    assertThrows(FieldOfStudyNotFoundException.class, () -> handler.handle(new ReadByIdQuery(id)));
    verify(repo).findByIdOptional(id);
    verifyNoMoreInteractions(repo);
  }

  @Test
  void readByIdFoundReturnsEntity() {
    UUID id = UUID.randomUUID();
    var fos = FieldOfStudy.builder().id(id).name("engineering").build();
    when(repo.findByIdOptional(id)).thenReturn(Optional.of(fos));

    var out = handler.handle(new ReadByIdQuery(id));

    assertEquals(fos, out);
    verify(repo).findByIdOptional(id);
    verifyNoMoreInteractions(repo);
  }

  @Test
  void readByPatternNullQueryFailsAndSkipsRepo() {
    assertThrows(
        ConstraintViolationException.class, () -> handler.handle(new ReadByPatternQuery(null, 10)));
    verifyNoInteractions(repo);
  }

  @Test
  void readByPatternLimitTooSmallFailsAndSkipsRepo() {
    assertThrows(
        ConstraintViolationException.class, () -> handler.handle(new ReadByPatternQuery("eng", 0)));
    verifyNoInteractions(repo);
  }

  @Test
  void readByPatternLimitTooLargeFailsAndSkipsRepo() {
    assertThrows(
        ConstraintViolationException.class,
        () -> handler.handle(new ReadByPatternQuery("eng", 201)));
    verifyNoInteractions(repo);
  }

  @Test
  void readByPatternNullLimitCallsListAllSorted() {
    var list = List.of(FieldOfStudy.builder().name("engineering").build());
    when(repo.listAllSorted()).thenReturn(list);

    var out = handler.handle(new ReadByPatternQuery("eng", null));

    assertEquals(list, out);
    verify(repo).listAllSorted();
    verifyNoMoreInteractions(repo);
  }

  @Test
  void readByPatternWithLimitCallsListByPattern() {
    var list = List.of(FieldOfStudy.builder().name("engineering").build());
    when(repo.listByPattern("eng", 5)).thenReturn(list);

    var out = handler.handle(new ReadByPatternQuery("eng", 5));

    assertEquals(list, out);
    verify(repo).listByPattern("eng", 5);
    verifyNoMoreInteractions(repo);
  }
}
