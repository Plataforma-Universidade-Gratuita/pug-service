package com.pug.academic.usecase.course;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.pug.academic.domain.Course;
import com.pug.academic.domain.FieldOfStudy;
import com.pug.academic.domain.exceptions.DuplicateCourseNameException;
import com.pug.academic.domain.exceptions.FieldOfStudyNotFoundException;
import com.pug.academic.infra.persistence.CourseRepository;
import com.pug.academic.infra.persistence.FieldOfStudyRepository;
import com.pug.academic.usecase.course.create.CreateCourseCommand;
import com.pug.academic.usecase.course.create.CreateCourseHandler;
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
class CreateCourseHandlerTest {

  @Mock CourseRepository courseRepo;
  @Mock FieldOfStudyRepository fieldRepo;

  @SuppressWarnings("unused")
  @Spy
  Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

  @InjectMocks CreateCourseHandler handler;

  @Test
  void nullNameFailsAndSkipsRepos() {
    var fieldId = UUID.randomUUID();
    assertThrows(
        ConstraintViolationException.class,
        () -> handler.handle(new CreateCourseCommand(null, fieldId)));
    verifyNoInteractions(courseRepo, fieldRepo);
  }

  @Test
  void blankNameFailsAndSkipsRepos() {
    var fieldId = UUID.randomUUID();
    assertThrows(
        ConstraintViolationException.class,
        () -> handler.handle(new CreateCourseCommand("  ", fieldId)));
    verifyNoInteractions(courseRepo, fieldRepo);
  }

  @Test
  void overlongNameFailsAndSkipsRepos() {
    var fieldId = UUID.randomUUID();
    assertThrows(
        ConstraintViolationException.class,
        () -> handler.handle(new CreateCourseCommand("x".repeat(121), fieldId)));
    verifyNoInteractions(courseRepo, fieldRepo);
  }

  @Test
  void nullFieldIdFailsAndSkipsRepos() {
    assertThrows(
        ConstraintViolationException.class,
        () -> handler.handle(new CreateCourseCommand("Databases", null)));
    verifyNoInteractions(courseRepo, fieldRepo);
  }

  @Test
  void trimsAndLowercasesThenChecksUniqueness() {
    when(courseRepo.existsByName("software engineering")).thenReturn(false);
    when(fieldRepo.findByIdOptional(any()))
        .thenReturn(
            Optional.of(FieldOfStudy.builder().id(UUID.randomUUID()).name("engineering").build()));

    handler.handle(new CreateCourseCommand("  Software Engineering  ", UUID.randomUUID()));

    verify(courseRepo).existsByName("software engineering");
  }

  @Test
  void duplicateNameThrowsAndSkipsPersist() {
    when(courseRepo.existsByName("networks")).thenReturn(true);

    assertThrows(
        DuplicateCourseNameException.class,
        () -> handler.handle(new CreateCourseCommand("Networks", UUID.randomUUID())));

    verify(courseRepo).existsByName("networks");
    verifyNoMoreInteractions(courseRepo);
    verifyNoInteractions(fieldRepo);
  }

  @Test
  void missingFieldThrowsFieldOfStudyNotFound() {
    UUID fid = UUID.randomUUID();
    when(courseRepo.existsByName("ai")).thenReturn(false);
    when(fieldRepo.findByIdOptional(fid)).thenReturn(Optional.empty());

    assertThrows(
        FieldOfStudyNotFoundException.class,
        () -> handler.handle(new CreateCourseCommand("AI", fid)));

    verify(courseRepo).existsByName("ai");
    verify(fieldRepo).findByIdOptional(fid);
    verifyNoMoreInteractions(courseRepo, fieldRepo);
  }

  @Test
  void validFlowPersistsAndFlushesAndReturnsId() {
    UUID fid = UUID.randomUUID();
    var field = FieldOfStudy.builder().id(fid).name("engineering").build();

    when(courseRepo.existsByName("databases")).thenReturn(false);
    when(fieldRepo.findByIdOptional(fid)).thenReturn(Optional.of(field));

    doAnswer(
            inv -> {
              Course c = inv.getArgument(0);
              c.setId(UUID.randomUUID());
              return null;
            })
        .when(courseRepo)
        .persist(any(Course.class));

    UUID id = handler.handle(new CreateCourseCommand("Databases", fid));

    assertNotNull(id);
    verify(courseRepo).existsByName("databases");
    verify(fieldRepo).findByIdOptional(fid);
    verify(courseRepo).persist(any(Course.class));
    verify(courseRepo).flush();
    verifyNoMoreInteractions(courseRepo, fieldRepo);
  }
}
