package com.pug.partner.usecase.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.pug.geo.domain.City;
import com.pug.partner.domain.PartnerEntity;
import com.pug.partner.domain.exceptions.DuplicateCnpjException;
import com.pug.partner.infra.persistence.PartnerEntityRepository;
import com.pug.partner.usecase.entity.create.RegisterPartnerEntityCommand;
import com.pug.partner.usecase.entity.create.RegisterPartnerEntityHandler;
import jakarta.persistence.EntityManager;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class RegisterPartnerEntityHandlerTest {

  @Mock PartnerEntityRepository repo;
  @Mock EntityManager em;

  @Spy Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

  @InjectMocks RegisterPartnerEntityHandler handler;

  private static final UUID CITY_ID = UUID.randomUUID();
  private static final UUID FIXED = UUID.fromString("00000000-0000-7000-8000-000000000001");

  private void stubCityRef(UUID id) {
    when(em.getReference(eq(City.class), eq(id)))
        .thenReturn(City.builder().id(id).name("X").ibgeCode("4200000").build());
  }

  @Test
  void createEntitySuccessPersistsFlushesAndReturnsId() {
    stubCityRef(CITY_ID); // keep stub; do not verify
    when(repo.existsByCnpj("11222333000181")).thenReturn(false);
    doAnswer(
            inv -> {
              PartnerEntity e = inv.getArgument(0);
              e.setId(FIXED);
              return null;
            })
        .when(repo)
        .persist(any(PartnerEntity.class));

    var id =
        handler.handle(
            new RegisterPartnerEntityCommand("11.222.333/0001-81", " Org ", CITY_ID, null));

    assertNotNull(id);

    InOrder io = inOrder(repo);
    io.verify(repo).existsByCnpj("11222333000181");
    io.verify(repo).persist(any(PartnerEntity.class));
    io.verify(repo).flush();
    verifyNoMoreInteractions(repo);

    ArgumentCaptor<PartnerEntity> cap = ArgumentCaptor.forClass(PartnerEntity.class);
    verify(repo).persist(cap.capture());
    var saved = cap.getValue();
    assertEquals("11222333000181", saved.getCnpj());
    assertEquals("Org", saved.getName());
    assertEquals(CITY_ID, saved.getCity().getId());
  }

  @Test
  void createEntityFailsOnValidation() {
    // bad CNPJ
    assertThrows(
        ConstraintViolationException.class,
        () -> handler.handle(new RegisterPartnerEntityCommand("bad", "Org", CITY_ID, null)));
    verifyNoInteractions(repo, em);
  }

  @Test
  void createEntityFailsOnDuplicateCnpjFastPath() {
    when(repo.existsByCnpj("11222333000181")).thenReturn(true);

    assertThrows(
        DuplicateCnpjException.class,
        () ->
            handler.handle(
                new RegisterPartnerEntityCommand("11.222.333/0001-81", "Org", CITY_ID, null)));

    verify(repo).existsByCnpj("11222333000181");
    verifyNoMoreInteractions(repo);
    verifyNoInteractions(em);
  }

  @Test
  void normalizesCnpjDigitsAndTrimsNameBeforePersist() {
    stubCityRef(CITY_ID);
    when(repo.existsByCnpj("11222333000181")).thenReturn(false);
    doAnswer(
            inv -> {
              PartnerEntity e = inv.getArgument(0);
              e.setId(UUID.randomUUID());
              return null;
            })
        .when(repo)
        .persist(any(PartnerEntity.class));

    handler.handle(
        new RegisterPartnerEntityCommand(" 11.222.333/0001-81 ", "  Org  ", CITY_ID, "Addr"));

    ArgumentCaptor<PartnerEntity> cap = ArgumentCaptor.forClass(PartnerEntity.class);
    verify(repo).persist(cap.capture());
    var saved = cap.getValue();
    assertEquals("11222333000181", saved.getCnpj());
    assertEquals("Org", saved.getName());
    assertEquals("Addr", saved.getAddress());
    assertEquals(CITY_ID, saved.getCity().getId());
  }

  @Test
  void nullCityIdTriggersConstraintViolationAndSkipsRepo() {
    assertThrows(
        ConstraintViolationException.class,
        () ->
            handler.handle(new RegisterPartnerEntityCommand("11222333000181", "Org", null, null)));
    verifyNoInteractions(repo, em);
  }

  @Test
  void tooLongNameTriggersConstraintViolationAndSkipsRepo() {
    assertThrows(
        ConstraintViolationException.class,
        () ->
            handler.handle(
                new RegisterPartnerEntityCommand(
                    "11222333000181", "x".repeat(151), CITY_ID, null)));
    verifyNoInteractions(repo, em);
  }
}
