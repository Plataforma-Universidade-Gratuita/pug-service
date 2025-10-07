package com.pug.academic.usecase.fieldOfStudy;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.pug.academic.domain.FieldOfStudy;
import com.pug.academic.domain.exceptions.DuplicateFieldOfStudyNameException;
import com.pug.academic.infra.persistence.FieldOfStudyRepository;
import com.pug.academic.usecase.fieldOfStudy.create.CreateFieldOfStudyCommand;
import com.pug.academic.usecase.fieldOfStudy.create.CreateFieldOfStudyHandler;
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
class CreateFieldOfStudyHandlerTest {

  @Mock FieldOfStudyRepository repo;

  @SuppressWarnings("unused")
  @Spy
  Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

  @InjectMocks CreateFieldOfStudyHandler handler;

  @Test
  void nullNameFailsAndSkipsRepo() {
    assertThrows(
        ConstraintViolationException.class,
        () -> handler.handle(new CreateFieldOfStudyCommand(null)));
    verifyNoInteractions(repo);
  }

  @Test
  void blankNameFailsAndSkipsRepo() {
    assertThrows(
        ConstraintViolationException.class,
        () -> handler.handle(new CreateFieldOfStudyCommand("  ")));
    verifyNoInteractions(repo);
  }

  @Test
  void overlongNameFailsAndSkipsRepo() {
    assertThrows(
        ConstraintViolationException.class,
        () -> handler.handle(new CreateFieldOfStudyCommand("x".repeat(101))));
    verifyNoInteractions(repo);
  }

  @Test
  void trimsAndLowercasesBeforeUniquenessCheck() {
    when(repo.existsByName("engineering")).thenReturn(false);
    handler.handle(new CreateFieldOfStudyCommand("  Engineering  "));
    verify(repo).existsByName("engineering");
  }

  @Test
  void lowercasesBeforeUniquenessCheck() {
    when(repo.existsByName("law")).thenReturn(false);
    handler.handle(new CreateFieldOfStudyCommand("LAW"));
    verify(repo).existsByName("law");
  }

  @Test
  void duplicateNameThrowsAndSkipsPersist() {
    when(repo.existsByName("medicine")).thenReturn(true);
    assertThrows(
        DuplicateFieldOfStudyNameException.class,
        () -> handler.handle(new CreateFieldOfStudyCommand("Medicine")));
    verify(repo).existsByName("medicine");
    verifyNoMoreInteractions(repo);
  }

  @Test
  void persistsWhenValid() {
    when(repo.existsByName("physics")).thenReturn(false);
    handler.handle(new CreateFieldOfStudyCommand("Physics"));
    verify(repo).existsByName("physics");
    verify(repo).persist(any(FieldOfStudy.class));
    verify(repo).flush();
  }
}
