package com.pug.academic.usecase.fieldOfStudy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.pug.academic.domain.FieldOfStudy;
import com.pug.academic.domain.exceptions.DuplicateFieldOfStudyNameException;
import com.pug.academic.domain.exceptions.FieldOfStudyNotFoundException;
import com.pug.academic.infra.persistence.FieldOfStudyRepository;
import com.pug.academic.usecase.fieldOfStudy.update.UpdateFieldOfStudyCommand;
import com.pug.academic.usecase.fieldOfStudy.update.UpdateFieldOfStudyHandler;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UpdateFieldOfStudyHandlerTest {

  @Mock FieldOfStudyRepository repo;

  @SuppressWarnings("unused")
  @Spy
  Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

  @InjectMocks UpdateFieldOfStudyHandler handler;

  static Locale orig;

  @BeforeAll
  static void locale() {
    orig = Locale.getDefault();
    Locale.setDefault(Locale.ENGLISH);
  }

  @AfterAll
  static void reset() {
    Locale.setDefault(orig);
  }

  @Test
  void nullIdFailsAndSkipsRepo() {
    assertThrows(
        ConstraintViolationException.class,
        () -> handler.handle(new UpdateFieldOfStudyCommand(null, "Law")));
    verifyNoInteractions(repo);
  }

  @Test
  void blankNameFailsAndSkipsRepo() {
    UUID id = UUID.randomUUID();
    assertThrows(
        ConstraintViolationException.class,
        () -> handler.handle(new UpdateFieldOfStudyCommand(id, "  ")));
    verifyNoInteractions(repo);
  }

  @Test
  void overlongNameFailsAndSkipsRepo() {
    UUID id = UUID.randomUUID();
    assertThrows(
        ConstraintViolationException.class,
        () -> handler.handle(new UpdateFieldOfStudyCommand(id, "x".repeat(101))));
    verifyNoInteractions(repo);
  }

  @Test
  void notFoundThrows404() {
    UUID id = UUID.randomUUID();
    when(repo.findByIdOptional(id)).thenReturn(Optional.empty());
    assertThrows(
        FieldOfStudyNotFoundException.class,
        () -> handler.handle(new UpdateFieldOfStudyCommand(id, "Law")));
    verify(repo).findByIdOptional(id);
    verifyNoMoreInteractions(repo);
  }

  @Test
  void duplicateNameThrowsAndDoesNotFlush() {
    UUID id = UUID.randomUUID();
    var existing = FieldOfStudy.builder().id(id).name("old").build();
    when(repo.findByIdOptional(id)).thenReturn(Optional.of(existing));
    when(repo.existsByNameForAnother("law", id)).thenReturn(true);

    assertThrows(
        DuplicateFieldOfStudyNameException.class,
        () -> handler.handle(new UpdateFieldOfStudyCommand(id, "Law")));

    verify(repo).findByIdOptional(id);
    verify(repo).existsByNameForAnother("law", id);
    verifyNoMoreInteractions(repo);
    assertEquals("old", existing.getName());
  }

  @Test
  void trimsAndLowercasesThenUpdatesAndFlushes() {
    UUID id = UUID.randomUUID();
    var existing = FieldOfStudy.builder().id(id).name("old").build();
    when(repo.findByIdOptional(id)).thenReturn(Optional.of(existing));
    when(repo.existsByNameForAnother("engineering", id)).thenReturn(false);

    UUID out = handler.handle(new UpdateFieldOfStudyCommand(id, "  Engineering  "));

    assertEquals(id, out);
    assertEquals("engineering", existing.getName());
    verify(repo).findByIdOptional(id);
    verify(repo).existsByNameForAnother("engineering", id);
    verify(repo).flush();
    verifyNoMoreInteractions(repo);
  }
}
