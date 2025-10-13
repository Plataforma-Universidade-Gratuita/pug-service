package com.pug.partner.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.pug.partner.domain.Staff;
import com.pug.partner.domain.StaffRepository;
import com.pug.partner.service.commands.CreateStaffCommand;
import com.pug.shared.application.EmailQuery;
import com.pug.shared.application.UuidCommand;
import com.pug.shared.domain.exceptions.AppValidationException;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class StaffServiceTest {

  private final StaffRepository repo = mock(StaffRepository.class);
  private final StaffService svc =
      new StaffService() {
        {
          this.repo = StaffServiceTest.this.repo;
        }
      };

  @Test
  void addChecksDuplicateAndSaves() {
    when(repo.existsByEmailForAnother("a@b.com", null)).thenReturn(false);
    when(repo.save(any())).thenAnswer(i -> i.getArgument(0));

    var out = svc.add(new CreateStaffCommand(UUID.randomUUID(), UUID.randomUUID(), "a@b.com"));
    assertEquals("a@b.com", out.getEmail());
    verify(repo).existsByEmailForAnother("a@b.com", null);
    verify(repo).save(any());
  }

  @Test
  void addFailsOnDuplicate() {
    when(repo.existsByEmailForAnother("a@b.com", null)).thenReturn(true);
    assertThrows(
        AppValidationException.class,
        () -> svc.add(new CreateStaffCommand(UUID.randomUUID(), UUID.randomUUID(), "a@b.com")));
    verify(repo).existsByEmailForAnother("a@b.com", null);
    verifyNoMoreInteractions(repo);
  }

  @Test
  void activateDeactivateAndFind() {
    UUID id = UUID.randomUUID();
    var s =
        Staff.newActive()
            .id(id)
            .userId(UUID.randomUUID())
            .email("x@y.com")
            .entityId(UUID.randomUUID())
            .build();
    when(repo.findOptionalById(id)).thenReturn(Optional.of(s));
    when(repo.save(any())).thenAnswer(i -> i.getArgument(0));
    when(repo.findByEmail("x@y.com")).thenReturn(Optional.of(s));

    assertTrue(svc.activate(new UuidCommand(id)).isActive());
    assertFalse(svc.deactivate(new UuidCommand(id)).isActive());
    assertTrue(svc.findByEmail(new EmailQuery("x@y.com")).isPresent());
  }
}
