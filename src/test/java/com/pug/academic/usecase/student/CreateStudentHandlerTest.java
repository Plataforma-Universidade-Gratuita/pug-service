package com.pug.academic.usecase.student;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.pug.academic.domain.Course;
import com.pug.academic.domain.Student;
import com.pug.academic.domain.exceptions.CourseNotFoundException;
import com.pug.academic.domain.exceptions.DuplicateAcademicRegistrationException;
import com.pug.academic.infra.persistence.CourseRepository;
import com.pug.academic.infra.persistence.StudentRepository;
import com.pug.academic.usecase.student.create.CreateStudentCommand;
import com.pug.academic.usecase.student.create.CreateStudentHandler;
import com.pug.identity.domain.Role;
import com.pug.identity.domain.exceptions.RoleNotFoundException;
import com.pug.identity.infra.persistence.RoleRepository;
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
class CreateStudentHandlerTest {

  @Mock StudentRepository studentRepo;
  @Mock RoleRepository roleRepo;
  @Mock CourseRepository courseRepo;

  @SuppressWarnings("unused")
  @Spy
  Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

  @InjectMocks CreateStudentHandler handler;

  @Test
  void nullUserRoleIdFailsAndSkipsRepos() {
    var cmd = new CreateStudentCommand(null, "AR1", UUID.randomUUID());
    assertThrows(ConstraintViolationException.class, () -> handler.handle(cmd));
    verifyNoInteractions(studentRepo, roleRepo, courseRepo);
  }

  @Test
  void nullCourseIdFailsAndSkipsRepos() {
    var cmd = new CreateStudentCommand(UUID.randomUUID(), "AR1", null);
    assertThrows(ConstraintViolationException.class, () -> handler.handle(cmd));
    verifyNoInteractions(studentRepo, roleRepo, courseRepo);
  }

  @Test
  void blankRegFailsAndSkipsRepos() {
    var cmd = new CreateStudentCommand(UUID.randomUUID(), "  ", UUID.randomUUID());
    assertThrows(ConstraintViolationException.class, () -> handler.handle(cmd));
    verifyNoInteractions(studentRepo, roleRepo, courseRepo);
  }

  @Test
  void overlongRegFailsAndSkipsRepos() {
    var cmd = new CreateStudentCommand(UUID.randomUUID(), "x".repeat(16), UUID.randomUUID());
    assertThrows(ConstraintViolationException.class, () -> handler.handle(cmd));
    verifyNoInteractions(studentRepo, roleRepo, courseRepo);
  }

  @Test
  void trimsRegBeforeUniquenessCheck() {
    when(studentRepo.existsByAcademicRegistration("AR123")).thenReturn(false);
    when(roleRepo.findByIdOptional(any())).thenReturn(Optional.of(new Role()));
    when(courseRepo.findByIdOptional(any()))
        .thenReturn(Optional.of(Course.builder().id(UUID.randomUUID()).build()));

    doAnswer(
            inv -> {
              Student s = inv.getArgument(0);
              s.setId(UUID.randomUUID());
              return null;
            })
        .when(studentRepo)
        .persist(any(Student.class));

    handler.handle(new CreateStudentCommand(UUID.randomUUID(), "  AR123  ", UUID.randomUUID()));

    verify(studentRepo).existsByAcademicRegistration("AR123");
  }

  @Test
  void duplicateRegistrationThrowsAndSkipsLookups() {
    when(studentRepo.existsByAcademicRegistration("AR123")).thenReturn(true);

    var cmd = new CreateStudentCommand(UUID.randomUUID(), "AR123", UUID.randomUUID());
    assertThrows(DuplicateAcademicRegistrationException.class, () -> handler.handle(cmd));

    verify(studentRepo).existsByAcademicRegistration("AR123");
    verifyNoInteractions(roleRepo, courseRepo);
  }

  @Test
  void roleNotFoundThrows() {
    UUID roleId = UUID.randomUUID();
    when(studentRepo.existsByAcademicRegistration("AR1")).thenReturn(false);
    when(roleRepo.findByIdOptional(roleId)).thenReturn(Optional.empty());

    var cmd = new CreateStudentCommand(roleId, "AR1", UUID.randomUUID());
    assertThrows(RoleNotFoundException.class, () -> handler.handle(cmd));

    verify(studentRepo).existsByAcademicRegistration("AR1");
    verify(roleRepo).findByIdOptional(roleId);
    verifyNoMoreInteractions(studentRepo, roleRepo);
    verifyNoInteractions(courseRepo);
  }

  @Test
  void courseNotFoundThrows() {
    UUID roleId = UUID.randomUUID();
    UUID courseId = UUID.randomUUID();
    when(studentRepo.existsByAcademicRegistration("AR1")).thenReturn(false);
    when(roleRepo.findByIdOptional(roleId)).thenReturn(Optional.of(new Role()));
    when(courseRepo.findByIdOptional(courseId)).thenReturn(Optional.empty());

    var cmd = new CreateStudentCommand(roleId, "AR1", courseId);
    assertThrows(CourseNotFoundException.class, () -> handler.handle(cmd));

    verify(studentRepo).existsByAcademicRegistration("AR1");
    verify(roleRepo).findByIdOptional(roleId);
    verify(courseRepo).findByIdOptional(courseId);
    verifyNoMoreInteractions(studentRepo, roleRepo, courseRepo);
  }

  @Test
  void validFlowPersistsFlushesAndReturnsId() {
    UUID roleId = UUID.randomUUID();
    UUID courseId = UUID.randomUUID();
    var role = new Role();
    var course = Course.builder().id(courseId).build();

    when(studentRepo.existsByAcademicRegistration("AR9")).thenReturn(false);
    when(roleRepo.findByIdOptional(roleId)).thenReturn(Optional.of(role));
    when(courseRepo.findByIdOptional(courseId)).thenReturn(Optional.of(course));

    doAnswer(
            inv -> {
              Student s = inv.getArgument(0);
              s.setId(UUID.randomUUID());
              return null;
            })
        .when(studentRepo)
        .persist(any(Student.class));

    UUID id = handler.handle(new CreateStudentCommand(roleId, "AR9", courseId));

    assertNotNull(id);
    verify(studentRepo).existsByAcademicRegistration("AR9");
    verify(roleRepo).findByIdOptional(roleId);
    verify(courseRepo).findByIdOptional(courseId);
    verify(studentRepo).persist(any(Student.class));
    verify(studentRepo).flush();
    verifyNoMoreInteractions(studentRepo, roleRepo, courseRepo);
  }
}
