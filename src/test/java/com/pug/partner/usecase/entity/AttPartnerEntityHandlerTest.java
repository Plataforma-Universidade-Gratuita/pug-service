package com.pug.partner.usecase.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.pug.geo.domain.City;
import com.pug.partner.domain.PartnerEntity;
import com.pug.partner.domain.exceptions.DuplicateCnpjException;
import com.pug.partner.domain.exceptions.PartnerEntityNotFoundException;
import com.pug.partner.infra.persistence.PartnerEntityRepository;
import com.pug.partner.usecase.entity.update.AttIsActiveCommand;
import com.pug.partner.usecase.entity.update.AttPartnerEntityCommand;
import com.pug.partner.usecase.entity.update.AttPartnerEntityHandler;
import jakarta.persistence.EntityManager;
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
class AttPartnerEntityHandlerTest {

  @Mock PartnerEntityRepository repo;
  @Mock EntityManager em;

  @SuppressWarnings("unused")
  @Spy
  Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

  @InjectMocks AttPartnerEntityHandler handler;

  private static PartnerEntity entity(UUID id, String cnpj, String name, UUID cityId) {
    return PartnerEntity.builder()
        .id(id)
        .cnpj(cnpj)
        .name(name)
        .city(City.builder().id(cityId).name("City").ibgeCode("4200000").build())
        .active(true)
        .build();
  }

  @Test
  void updateSuccess_changesNameCityAddress_flushesAndReturnsEntity() {
    var id = UUID.randomUUID();
    var oldCity = UUID.randomUUID();
    var newCity = UUID.randomUUID();
    var existing = entity(id, "11222333000181", "Old Org", oldCity);

    when(repo.findByIdOptional(id)).thenReturn(Optional.of(existing));
    when(em.getReference(City.class, newCity))
        .thenReturn(City.builder().id(newCity).name("New City").ibgeCode("4201000").build());

    var cmd = new AttPartnerEntityCommand(id, null, "  New Org  ", newCity, "Addr");

    var out = handler.handle(cmd);

    assertNotNull(out);
    assertEquals(id, out.getId());
    assertEquals("11222333000181", out.getCnpj());
    assertEquals("New Org", out.getName());
    assertEquals("Addr", out.getAddress());
    assertEquals(newCity, out.getCity().getId());

    verify(repo).findByIdOptional(id);
    verify(em).getReference(City.class, newCity);
    verify(repo).flush();
    verifyNoMoreInteractions(repo, em);
  }

  @Test
  void updateSuccess_changesCnpj_normalizesDigits_checksDup_flushes() {
    var id = UUID.randomUUID();
    var city = UUID.randomUUID();
    var existing = entity(id, "11222333000181", "Org", city);

    when(repo.findByIdOptional(id)).thenReturn(Optional.of(existing));
    when(repo.existsByCnpjForAnother("19131243000197", id)).thenReturn(false);

    var cmd = new AttPartnerEntityCommand(id, "19.131.243/0001-97", null, null, null);

    var out = handler.handle(cmd);

    assertEquals("19131243000197", out.getCnpj());
    assertEquals("Org", out.getName()); // unchanged
    verify(repo).findByIdOptional(id);
    verify(repo).existsByCnpjForAnother("19131243000197", id);
    verify(repo).flush();
    verifyNoMoreInteractions(repo);
    verifyNoInteractions(em);
  }

  @Test
  void duplicateCnpjFastPathThrows() {
    var id = UUID.randomUUID();
    var city = UUID.randomUUID();
    var existing = entity(id, "11222333000181", "Org", city);

    when(repo.findByIdOptional(id)).thenReturn(Optional.of(existing));
    when(repo.existsByCnpjForAnother("19131243000197", id)).thenReturn(true);

    var cmd = new AttPartnerEntityCommand(id, "19.131.243/0001-97", null, null, null);

    assertThrows(DuplicateCnpjException.class, () -> handler.handle(cmd));

    verify(repo).findByIdOptional(id);
    verify(repo).existsByCnpjForAnother("19131243000197", id);
    verify(repo, never()).flush();
    verifyNoMoreInteractions(repo);
    verifyNoInteractions(em);
  }

  @Test
  void entityNotFoundThrows() {
    var id = UUID.randomUUID();
    when(repo.findByIdOptional(id)).thenReturn(Optional.empty());

    var cmd = new AttPartnerEntityCommand(id, null, "Org", null, null);

    assertThrows(PartnerEntityNotFoundException.class, () -> handler.handle(cmd));

    verify(repo).findByIdOptional(id);
    verifyNoMoreInteractions(repo);
    verifyNoInteractions(em);
  }

  @Test
  void commandValidationFailure_badCnpj_throwsConstraintViolation_andSkipsRepo() {
    var id = UUID.randomUUID();
    var cmd = new AttPartnerEntityCommand(id, "bad", null, null, null);

    assertThrows(ConstraintViolationException.class, () -> handler.handle(cmd));

    verifyNoInteractions(repo, em);
  }

  @Test
  void commandValidationFailure_nameTooLong_throwsConstraintViolation_andSkipsRepo() {
    var id = UUID.randomUUID();
    var cmd = new AttPartnerEntityCommand(id, null, "x".repeat(151), null, null);

    assertThrows(ConstraintViolationException.class, () -> handler.handle(cmd));

    verifyNoInteractions(repo, em);
  }

  private static PartnerEntity entity(UUID id, boolean active) {
    return PartnerEntity.builder()
        .id(id)
        .cnpj("11222333000181")
        .name("Org")
        .city(City.builder().id(UUID.randomUUID()).name("City").ibgeCode("4200000").build())
        .active(active)
        .build();
  }

  @Test
  void togglesFromActiveToInactiveAndFlushes() {
    var id = UUID.randomUUID();
    var e = entity(id, true);
    when(repo.findByIdOptional(id)).thenReturn(Optional.of(e));

    handler.handle(new AttIsActiveCommand(id));

    assertFalse(e.isActive());
    verify(repo).findByIdOptional(id);
    verify(repo).flush();
    verifyNoMoreInteractions(repo);
  }

  @Test
  void togglesFromInactiveToActiveAndFlushes() {
    var id = UUID.randomUUID();
    var e = entity(id, false);
    when(repo.findByIdOptional(id)).thenReturn(Optional.of(e));

    handler.handle(new AttIsActiveCommand(id));

    assertTrue(e.isActive());
    verify(repo).findByIdOptional(id);
    verify(repo).flush();
    verifyNoMoreInteractions(repo);
  }

  @Test
  void invalidToggleCommandIdTriggersConstraintViolation() {
    assertThrows(
        ConstraintViolationException.class, () -> handler.handle(new AttIsActiveCommand(null)));
    verifyNoInteractions(repo);
  }
}
