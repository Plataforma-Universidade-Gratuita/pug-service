package com.pug.academic.usecase.course;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.pug.academic.domain.Course;
import com.pug.academic.domain.FieldOfStudy;
import com.pug.academic.domain.exceptions.CourseNotFoundException;
import com.pug.academic.domain.exceptions.DuplicateCourseNameException;
import com.pug.academic.domain.exceptions.FieldOfStudyNotFoundException;
import com.pug.academic.infra.persistence.CourseRepository;
import com.pug.academic.infra.persistence.FieldOfStudyRepository;
import com.pug.academic.usecase.course.update.UpdateCourseCommand;
import com.pug.academic.usecase.course.update.UpdateCourseHandler;
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
class UpdateCourseHandlerTest {

  @Mock CourseRepository courseRepo;
  @Mock FieldOfStudyRepository fieldRepo;

  @SuppressWarnings("unused")
  @Spy
  Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

  @InjectMocks UpdateCourseHandler handler;

  @Test
  void nullIdFailsAndSkipsRepos() {
    assertThrows(
        ConstraintViolationException.class,
        () -> handler.handle(new UpdateCourseCommand(null, "Databases", UUID.randomUUID())));
    verifyNoInteractions(courseRepo, fieldRepo);
  }

  @Test
  void nullFieldIdFailsAndSkipsRepos() {
    assertThrows(
        ConstraintViolationException.class,
        () -> handler.handle(new UpdateCourseCommand(UUID.randomUUID(), "Databases", null)));
    verifyNoInteractions(courseRepo, fieldRepo);
  }

  @Test
  void blankNameFailsAndSkipsRepos() {
    assertThrows(
        ConstraintViolationException.class,
        () -> handler.handle(new UpdateCourseCommand(UUID.randomUUID(), "  ", UUID.randomUUID())));
    verifyNoInteractions(courseRepo, fieldRepo);
  }

  @Test
  void overlongNameFailsAndSkipsRepos() {
    assertThrows(
        ConstraintViolationException.class,
        () ->
            handler.handle(
                new UpdateCourseCommand(UUID.randomUUID(), "x".repeat(121), UUID.randomUUID())));
    verifyNoInteractions(courseRepo, fieldRepo);
  }

  @Test
  void courseNotFoundThrows404() {
    UUID id = UUID.randomUUID();
    when(courseRepo.findByIdOptional(id)).thenReturn(Optional.empty());

    assertThrows(
        CourseNotFoundException.class,
        () -> handler.handle(new UpdateCourseCommand(id, "Databases", UUID.randomUUID())));

    verify(courseRepo).findByIdOptional(id);
    verifyNoMoreInteractions(courseRepo);
    verifyNoInteractions(fieldRepo);
  }

  @Test
  void duplicateNameThrowsAndDoesNotFlush() {
    UUID id = UUID.randomUUID();
    var existingField = FieldOfStudy.builder().id(UUID.randomUUID()).name("engineering").build();
    var existingCourse = Course.builder().id(id).name("old name").field(existingField).build();

    when(courseRepo.findByIdOptional(id)).thenReturn(Optional.of(existingCourse));
    when(courseRepo.existsByNameForAnother("databases", id)).thenReturn(true);

    assertThrows(
        DuplicateCourseNameException.class,
        () -> handler.handle(new UpdateCourseCommand(id, "Databases", existingField.getId())));

    assertEquals("old name", existingCourse.getName());
    assertEquals(existingField, existingCourse.getField());

    verify(courseRepo).findByIdOptional(id);
    verify(courseRepo).existsByNameForAnother("databases", id);
    verifyNoMoreInteractions(courseRepo);
    verifyNoInteractions(fieldRepo);
  }

  @Test
  void fieldNotFoundThrowsFieldOfStudyNotFound() {
    UUID id = UUID.randomUUID();
    UUID newFieldId = UUID.randomUUID();
    var existingField = FieldOfStudy.builder().id(UUID.randomUUID()).name("engineering").build();
    var existingCourse = Course.builder().id(id).name("old").field(existingField).build();

    when(courseRepo.findByIdOptional(id)).thenReturn(Optional.of(existingCourse));
    when(courseRepo.existsByNameForAnother("ai", id)).thenReturn(false);
    when(fieldRepo.findByIdOptional(newFieldId)).thenReturn(Optional.empty());

    assertThrows(
        FieldOfStudyNotFoundException.class,
        () -> handler.handle(new UpdateCourseCommand(id, "AI", newFieldId)));

    verify(courseRepo).findByIdOptional(id);
    verify(courseRepo).existsByNameForAnother("ai", id);
    verify(fieldRepo).findByIdOptional(newFieldId);
    verifyNoMoreInteractions(courseRepo, fieldRepo);
  }

  @Test
  void trimsLowercasesUpdatesFieldAndFlushes() {
    UUID id = UUID.randomUUID();
    UUID newFieldId = UUID.randomUUID();

    var oldField = FieldOfStudy.builder().id(UUID.randomUUID()).name("engineering").build();
    var newField = FieldOfStudy.builder().id(newFieldId).name("computing").build();
    var existingCourse = Course.builder().id(id).name("old").field(oldField).build();

    when(courseRepo.findByIdOptional(id)).thenReturn(Optional.of(existingCourse));
    when(courseRepo.existsByNameForAnother("databases", id)).thenReturn(false);
    when(fieldRepo.findByIdOptional(newFieldId)).thenReturn(Optional.of(newField));

    UUID out = handler.handle(new UpdateCourseCommand(id, "  Databases  ", newFieldId));

    assertEquals(id, out);
    assertEquals("databases", existingCourse.getName());
    assertEquals(newField, existingCourse.getField());

    verify(courseRepo).findByIdOptional(id);
    verify(courseRepo).existsByNameForAnother("databases", id);
    verify(fieldRepo).findByIdOptional(newFieldId);
    verify(courseRepo).flush();
    verifyNoMoreInteractions(courseRepo, fieldRepo);
  }
}
