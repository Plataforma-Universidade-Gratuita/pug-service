package com.pug.academic.usecase.student;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.pug.academic.domain.Course;
import com.pug.academic.domain.Student;
import com.pug.academic.domain.exceptions.CourseNotFoundException;
import com.pug.academic.domain.exceptions.DuplicateAcademicRegistrationException;
import com.pug.academic.domain.exceptions.StudentNotFoundException;
import com.pug.academic.infra.persistence.CourseRepository;
import com.pug.academic.infra.persistence.StudentRepository;
import com.pug.academic.usecase.student.update.UpdateStudentCommand;
import com.pug.academic.usecase.student.update.UpdateStudentHandler;
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
class UpdateStudentHandlerTest {

  @Mock StudentRepository studentRepo;
  @Mock CourseRepository courseRepo;

  @SuppressWarnings("unused")
  @Spy
  Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

  @InjectMocks UpdateStudentHandler handler;

  @Test
  void nullIdFailsAndSkipsRepos() {
    assertThrows(
        ConstraintViolationException.class,
        () -> handler.handle(new UpdateStudentCommand(null, "AR1", UUID.randomUUID())));
    verifyNoInteractions(studentRepo, courseRepo);
  }

  @Test
  void blankRegFailsAndSkipsRepos() {
    assertThrows(
        ConstraintViolationException.class,
        () -> handler.handle(new UpdateStudentCommand(UUID.randomUUID(), "  ", UUID.randomUUID())));
    verifyNoInteractions(studentRepo, courseRepo);
  }

  @Test
  void overlongRegFailsAndSkipsRepos() {
    assertThrows(
        ConstraintViolationException.class,
        () ->
            handler.handle(
                new UpdateStudentCommand(UUID.randomUUID(), "x".repeat(16), UUID.randomUUID())));
    verifyNoInteractions(studentRepo, courseRepo);
  }

  @Test
  void nullCourseIdFailsAndSkipsRepos() {
    assertThrows(
        ConstraintViolationException.class,
        () -> handler.handle(new UpdateStudentCommand(UUID.randomUUID(), "AR1", null)));
    verifyNoInteractions(studentRepo, courseRepo);
  }

  @Test
  void studentNotFoundThrows() {
    UUID id = UUID.randomUUID();
    when(studentRepo.findByIdOptional(id)).thenReturn(Optional.empty());

    assertThrows(
        StudentNotFoundException.class,
        () -> handler.handle(new UpdateStudentCommand(id, "AR1", UUID.randomUUID())));

    verify(studentRepo).findByIdOptional(id);
    verifyNoMoreInteractions(studentRepo);
    verifyNoInteractions(courseRepo);
  }

  @Test
  void duplicateRegThrowsAndDoesNotFlush() {
    UUID id = UUID.randomUUID();
    var existing =
        Student.builder()
            .id(id)
            .academicRegistration("OLD")
            .course(Course.builder().id(UUID.randomUUID()).build())
            .build();

    when(studentRepo.findByIdOptional(id)).thenReturn(Optional.of(existing));
    when(studentRepo.existsByAcademicRegistrationForAnother("AR1", id)).thenReturn(true);

    assertThrows(
        DuplicateAcademicRegistrationException.class,
        () -> handler.handle(new UpdateStudentCommand(id, "AR1", UUID.randomUUID())));

    assertEquals("OLD", existing.getAcademicRegistration());
    verify(studentRepo).findByIdOptional(id);
    verify(studentRepo).existsByAcademicRegistrationForAnother("AR1", id);
    verifyNoMoreInteractions(studentRepo);
    verifyNoInteractions(courseRepo);
  }

  @Test
  void courseNotFoundThrows() {
    UUID id = UUID.randomUUID();
    UUID courseId = UUID.randomUUID();
    var existing =
        Student.builder()
            .id(id)
            .academicRegistration("OLD")
            .course(Course.builder().id(UUID.randomUUID()).build())
            .build();

    when(studentRepo.findByIdOptional(id)).thenReturn(Optional.of(existing));
    when(studentRepo.existsByAcademicRegistrationForAnother("AR2", id)).thenReturn(false);
    when(courseRepo.findByIdOptional(courseId)).thenReturn(Optional.empty());

    assertThrows(
        CourseNotFoundException.class,
        () -> handler.handle(new UpdateStudentCommand(id, "AR2", courseId)));

    verify(studentRepo).findByIdOptional(id);
    verify(studentRepo).existsByAcademicRegistrationForAnother("AR2", id);
    verify(courseRepo).findByIdOptional(courseId);
    verifyNoMoreInteractions(studentRepo, courseRepo);
  }

  @Test
  void trimsRegUpdatesCourseAndFlushes() {
    UUID id = UUID.randomUUID();
    UUID courseId = UUID.randomUUID();

    var oldCourse = Course.builder().id(UUID.randomUUID()).build();
    var newCourse = Course.builder().id(courseId).build();
    var existing = Student.builder().id(id).academicRegistration("OLD").course(oldCourse).build();

    when(studentRepo.findByIdOptional(id)).thenReturn(Optional.of(existing));
    when(studentRepo.existsByAcademicRegistrationForAnother("AR9", id)).thenReturn(false);
    when(courseRepo.findByIdOptional(courseId)).thenReturn(Optional.of(newCourse));

    UUID out = handler.handle(new UpdateStudentCommand(id, "  AR9  ", courseId));

    assertEquals(id, out);
    assertEquals("AR9", existing.getAcademicRegistration());
    assertEquals(newCourse, existing.getCourse());
    verify(studentRepo).findByIdOptional(id);
    verify(studentRepo).existsByAcademicRegistrationForAnother("AR9", id);
    verify(courseRepo).findByIdOptional(courseId);
    verify(studentRepo).flush();
    verifyNoMoreInteractions(studentRepo, courseRepo);
  }
}
