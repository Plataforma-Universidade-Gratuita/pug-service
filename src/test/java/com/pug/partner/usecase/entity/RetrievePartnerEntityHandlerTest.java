package com.pug.partner.usecase.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.pug.geo.domain.City;
import com.pug.partner.domain.PartnerEntity;
import com.pug.partner.domain.exceptions.PartnerEntityNotFoundException;
import com.pug.partner.infra.persistence.PartnerEntityRepository;
import com.pug.partner.usecase.entity.get.RetrievePartnerEntityByCnpjQuery;
import com.pug.partner.usecase.entity.get.RetrievePartnerEntityByIdQuery;
import com.pug.partner.usecase.entity.get.RetrievePartnerEntityHandler;
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

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class RetrievePartnerEntityHandlerTest {

  @Mock PartnerEntityRepository repo;

  @Spy Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

  @InjectMocks RetrievePartnerEntityHandler handler;

  private static PartnerEntity entityWithCnpj(String cnpj) {
    return PartnerEntity.builder()
        .id(UUID.randomUUID())
        .cnpj(cnpj)
        .name("Org")
        .city(City.builder().id(UUID.randomUUID()).name("City").ibgeCode("4200000").build())
        .build();
  }

  @Test
  void returnsEntityWhenFoundWithMaskNormalization() {
    var masked = "11.222.333/0001-81";
    var digits = "11222333000181";
    when(repo.findByCnpj(digits)).thenReturn(Optional.of(entityWithCnpj(digits)));

    var out = handler.handle(new RetrievePartnerEntityByCnpjQuery(masked));

    assertNotNull(out);
    assertEquals(digits, out.getCnpj());
    verify(repo).findByCnpj(digits);
    verifyNoMoreInteractions(repo);
  }

  @Test
  void nullCnpjThrowsConstraintViolationAndSkipsRepo() {
    assertThrows(
        ConstraintViolationException.class,
        () -> handler.handle(new RetrievePartnerEntityByCnpjQuery(null)));
    verifyNoInteractions(repo);
  }

  @Test
  void blankCnpjThrowsConstraintViolationAndSkipsRepo() {
    assertThrows(
        ConstraintViolationException.class,
        () -> handler.handle(new RetrievePartnerEntityByCnpjQuery("  ")));
    verifyNoInteractions(repo);
  }

  @Test
  void invalidCnpjFormatThrowsConstraintViolationAndSkipsRepo() {
    assertThrows(
        ConstraintViolationException.class,
        () -> handler.handle(new RetrievePartnerEntityByCnpjQuery("123")));
    verifyNoInteractions(repo);
  }

  @Test
  void notFoundByCnpjThrows() {
    var digits = "11222333000181";
    when(repo.findByCnpj(digits)).thenReturn(Optional.empty());

    assertThrows(
        PartnerEntityNotFoundException.class,
        () -> handler.handle(new RetrievePartnerEntityByCnpjQuery(digits)));

    verify(repo).findByCnpj(digits);
    verifyNoMoreInteractions(repo);
  }

  private static PartnerEntity entityWithId(UUID id) {
    return PartnerEntity.builder()
        .id(id)
        .cnpj("11222333000181")
        .name("Org")
        .city(City.builder().id(UUID.randomUUID()).name("City").ibgeCode("4200000").build())
        .build();
  }

  @Test
  void returnsEntityWhenFoundById() {
    var id = UUID.randomUUID();
    var e = entityWithId(id);
    when(repo.findByIdOptional(id)).thenReturn(Optional.of(e));

    var out = handler.handle(new RetrievePartnerEntityByIdQuery(id));

    assertNotNull(out);
    assertEquals(id, out.getId());
    verify(repo).findByIdOptional(id);
    verifyNoMoreInteractions(repo);
  }

  @Test
  void throwsWhenNotFoundById() {
    var id = UUID.randomUUID();
    when(repo.findByIdOptional(id)).thenReturn(Optional.empty());

    assertThrows(
        PartnerEntityNotFoundException.class,
        () -> handler.handle(new RetrievePartnerEntityByIdQuery(id)));

    verify(repo).findByIdOptional(id);
    verifyNoMoreInteractions(repo);
  }

  @Test
  void throwsWhenIdIsNull() {
    assertThrows(
        ConstraintViolationException.class,
        () -> handler.handle(new RetrievePartnerEntityByIdQuery(null)));
    verifyNoInteractions(repo);
  }
}
