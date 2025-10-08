package com.pug.academic.usecase.course;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.pug.academic.domain.Course;
import com.pug.academic.domain.exceptions.CourseNotFoundException;
import com.pug.academic.infra.persistence.CourseRepository;
import com.pug.academic.usecase.course.read.ReadCourseHandler;
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
class ReadCourseHandlerTest {

  @Mock CourseRepository repo;

  @SuppressWarnings("unused")
  @Spy
  Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

  @InjectMocks ReadCourseHandler handler;

  @Test
  void readByIdNullIdFailsAndSkipsRepo() {
    assertThrows(ConstraintViolationException.class, () -> handler.handle(new ReadByIdQuery(null)));
    verifyNoInteractions(repo);
  }

  @Test
  void readByIdNotFoundThrowsCourseNotFound() {
    UUID id = UUID.randomUUID();
    when(repo.findByIdOptional(id)).thenReturn(Optional.empty());

    assertThrows(CourseNotFoundException.class, () -> handler.handle(new ReadByIdQuery(id)));
    verify(repo).findByIdOptional(id);
    verifyNoMoreInteractions(repo);
  }

  @Test
  void readByIdFoundReturnsEntity() {
    UUID id = UUID.randomUUID();
    var c = Course.builder().id(id).name("databases").build();
    when(repo.findByIdOptional(id)).thenReturn(Optional.of(c));

    var out = handler.handle(new ReadByIdQuery(id));

    assertEquals(c, out);
    verify(repo).findByIdOptional(id);
    verifyNoMoreInteractions(repo);
  }

  @Test
  void readByPatternNullQueryFailsAndSkipsRepo() {
    assertThrows(
        ConstraintViolationException.class,
        () -> handler.handle(new ReadByPatternQuery(null, 10, 0)));
    verifyNoInteractions(repo);
  }

  @Test
  void readByPattern_limitTooSmall_failsAndSkipsRepo() {
    assertThrows(
        ConstraintViolationException.class,
        () -> handler.handle(new ReadByPatternQuery("data", 0, 0)));
    verifyNoInteractions(repo);
  }

  @Test
  void readByPatternLimitTooLargeFailsAndSkipsRepo() {
    assertThrows(
        ConstraintViolationException.class,
        () -> handler.handle(new ReadByPatternQuery("data", 201, 0)));
    verifyNoInteractions(repo);
  }

  @Test
  void readByPatternNegativeOffsetFailsAndSkipsRepo() {
    assertThrows(
        ConstraintViolationException.class,
        () -> handler.handle(new ReadByPatternQuery("data", 10, -1)));
    verifyNoInteractions(repo);
  }

  @Test
  void readByPatternNullLimitCallsListAllSorted() {
    var list = List.of(Course.builder().name("databases").build());
    when(repo.listAllSorted()).thenReturn(list);

    var out = handler.handle(new ReadByPatternQuery("data", null));

    assertEquals(list, out);
    verify(repo).listAllSorted();
    verifyNoMoreInteractions(repo);
  }

  @Test
  void readByPatternWithLimitNullOffsetCallsThreeArgWithZero() {
    var list = List.of(Course.builder().name("databases").build());
    when(repo.listByPattern("data", 5, 0)).thenReturn(list);

    var out = handler.handle(new ReadByPatternQuery("data", 5));

    assertEquals(list, out);
    verify(repo).listByPattern("data", 5, 0);
    verifyNoMoreInteractions(repo);
  }

  @Test
  void readByPatternWithLimitZeroOffsetCallsThreeArgWithZero() {
    var list = List.of(Course.builder().name("algorithms").build());
    when(repo.listByPattern("algo", 10, 0)).thenReturn(list);

    var out = handler.handle(new ReadByPatternQuery("algo", 10, 0));

    assertEquals(list, out);
    verify(repo).listByPattern("algo", 10, 0);
    verifyNoMoreInteractions(repo);
  }

  @Test
  void readByPatternWithOffsetCallsThreeArg() {
    var list = List.of(Course.builder().name("os").build());
    when(repo.listByPattern("sys", 10, 20)).thenReturn(list);

    var out = handler.handle(new ReadByPatternQuery("sys", 10, 20));

    assertEquals(list, out);
    verify(repo).listByPattern("sys", 10, 20);
    verifyNoMoreInteractions(repo);
  }
}
