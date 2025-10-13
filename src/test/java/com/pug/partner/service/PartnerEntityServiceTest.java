package com.pug.partner.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.pug.partner.domain.Cnpj;
import com.pug.partner.domain.PartnerEntity;
import com.pug.partner.domain.PartnerEntityRepository;
import com.pug.partner.service.commands.CreatePartnerEntityCommand;
import com.pug.partner.service.commands.UpdatePartnerEntityCommand;
import com.pug.shared.application.UuidQuery;
import com.pug.shared.domain.exceptions.AppValidationException;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class PartnerEntityServiceTest {

  private final PartnerEntityRepository repo = mock(PartnerEntityRepository.class);
  private final PartnerEntityService svc =
      new PartnerEntityService() {
        {
          this.repo = PartnerEntityServiceTest.this.repo;
        }
      };

  @Test
  void registerChecksDuplicateAndSaves() {
    when(repo.existsByCnpjForAnother("11222333000181", null)).thenReturn(false);
    when(repo.save(any())).thenAnswer(i -> i.getArgument(0));

    var out =
        svc.register(
            new CreatePartnerEntityCommand("11.222.333/0001-81", "Org", UUID.randomUUID(), "Addr"));
    assertEquals("Org", out.getName());
    verify(repo).existsByCnpjForAnother("11.222.333/0001-81", null);
    verify(repo).save(any());
  }

  @Test
  void registerFailsOnDuplicate() {
    when(repo.existsByCnpjForAnother("11222333000181", null)).thenReturn(true);
    assertThrows(
        AppValidationException.class,
        () ->
            svc.register(
                new CreatePartnerEntityCommand("11222333000181", "Org", UUID.randomUUID(), null)));
    verify(repo).existsByCnpjForAnother("11222333000181", null);
    verifyNoMoreInteractions(repo);
  }

  @Test
  void getAndUpdate() {
    UUID id = UUID.randomUUID();
    var existing =
        PartnerEntity.newActive()
            .id(id)
            .cnpj(Cnpj.of("11222333000181"))
            .name("A")
            .cityId(UUID.randomUUID())
            .build();
    when(repo.findOptionalById(id)).thenReturn(Optional.of(existing));
    when(repo.existsByCnpjForAnother("11222333000181", id)).thenReturn(false);
    when(repo.save(any())).thenAnswer(i -> i.getArgument(0));

    var got = svc.get(new UuidQuery(id));
    assertEquals(id, got.getId());

    var upd =
        svc.update(
            new UpdatePartnerEntityCommand(
                id, "11222333000181", "B", existing.getCityId(), null, null));
    assertEquals("B", upd.getName());
  }

  @Test
  void updateFailsOnDuplicate() {
    UUID id = UUID.randomUUID();
    var existing =
        PartnerEntity.newActive()
            .id(id)
            .cnpj(Cnpj.of("11222333000181"))
            .name("A")
            .cityId(UUID.randomUUID())
            .build();
    when(repo.findOptionalById(id)).thenReturn(Optional.of(existing));
    when(repo.existsByCnpjForAnother("11222333000181", id)).thenReturn(true);

    assertThrows(
        AppValidationException.class,
        () ->
            svc.update(
                new UpdatePartnerEntityCommand(
                    id, "11222333000181", "B", existing.getCityId(), null, null)));
  }
}
