package com.pug.academic.usecase.student;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.pug.academic.domain.Student;
import com.pug.academic.domain.exceptions.StudentNotFoundException;
import com.pug.academic.infra.persistence.StudentRepository;
import com.pug.academic.usecase.student.read.ReadStudentByAcademicRegistrationQuery;
import com.pug.academic.usecase.student.read.ReadStudentByUserRoleIdQuery;
import com.pug.academic.usecase.student.read.ReadStudentHandler;
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
class ReadStudentHandlerTest {

  @Mock StudentRepository repo;

  @SuppressWarnings("unused")
  @Spy
  Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

  @InjectMocks ReadStudentHandler handler;

  @Test
  void regNullFailsAndSkipsRepo() {
    assertThrows(
        ConstraintViolationException.class,
        () -> handler.handle(new ReadStudentByAcademicRegistrationQuery(null)));
    verifyNoInteractions(repo);
  }

  @Test
  void regBlankFailsAndSkipsRepo() {
    assertThrows(
        ConstraintViolationException.class,
        () -> handler.handle(new ReadStudentByAcademicRegistrationQuery("  ")));
    verifyNoInteractions(repo);
  }

  @Test
  void regNotFoundThrowsStudentNotFound() {
    when(repo.findByAcademicRegistration("AR1")).thenReturn(Optional.empty());
    assertThrows(
        StudentNotFoundException.class,
        () -> handler.handle(new ReadStudentByAcademicRegistrationQuery("AR1")));
    verify(repo).findByAcademicRegistration("AR1");
    verifyNoMoreInteractions(repo);
  }

  @Test
  void regFoundReturnsStudent() {
    var s = Student.builder().build();
    when(repo.findByAcademicRegistration("AR1")).thenReturn(Optional.of(s));
    var out = handler.handle(new ReadStudentByAcademicRegistrationQuery("  AR1  "));
    assertEquals(s, out);
    verify(repo).findByAcademicRegistration("AR1");
    verifyNoMoreInteractions(repo);
  }

  @Test
  void userRoleIdNullFailsAndSkipsRepo() {
    assertThrows(
        ConstraintViolationException.class,
        () -> handler.handle(new ReadStudentByUserRoleIdQuery(null)));
    verifyNoInteractions(repo);
  }

  @Test
  void userRoleIdNotFoundThrowsStudentNotFound() {
    UUID id = UUID.randomUUID();
    when(repo.findByUserRoleId(id)).thenReturn(Optional.empty());
    assertThrows(
        StudentNotFoundException.class, () -> handler.handle(new ReadStudentByUserRoleIdQuery(id)));
    verify(repo).findByUserRoleId(id);
    verifyNoMoreInteractions(repo);
  }

  @Test
  void userRoleIdFoundReturnsStudent() {
    UUID id = UUID.randomUUID();
    var s = Student.builder().build();
    when(repo.findByUserRoleId(id)).thenReturn(Optional.of(s));
    var out = handler.handle(new ReadStudentByUserRoleIdQuery(id));
    assertEquals(s, out);
    verify(repo).findByUserRoleId(id);
    verifyNoMoreInteractions(repo);
  }
}
