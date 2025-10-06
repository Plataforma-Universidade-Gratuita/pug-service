package com.pug.partner.usecase.staff;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.pug.geo.domain.City;
import com.pug.partner.domain.PartnerEntity;
import com.pug.partner.domain.Staff;
import com.pug.partner.domain.exceptions.StaffNotFoundException;
import com.pug.partner.infra.persistence.StaffRepository;
import com.pug.partner.usecase.staff.get.RetrieveStaffByUserRoleIdQuery;
import com.pug.partner.usecase.staff.get.RetrieveStaffHandler;
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
class RetrieveStaffHandlerTest {

  @Mock StaffRepository repo;

  @SuppressWarnings("unused")
  @Spy
  Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

  @InjectMocks RetrieveStaffHandler handler;

  private static Staff staffWithUserRole(UUID userRoleId) {
    var city = City.builder().id(UUID.randomUUID()).name("City").ibgeCode("4200000").build();
    var entity =
        PartnerEntity.builder()
            .id(UUID.randomUUID())
            .cnpj("11222333000181")
            .name("Org")
            .city(city)
            .build();
    return Staff.builder()
        .id(UUID.randomUUID())
        .userRole(com.pug.identity.domain.Role.builder().id(userRoleId).build())
        .entity(entity)
        .build();
  }

  @Test
  void returnsStaffWhenFoundByUserRoleId() {
    var urid = UUID.randomUUID();
    var s = staffWithUserRole(urid);
    when(repo.findByUserRoleId(urid)).thenReturn(Optional.of(s));

    var out = handler.handle(new RetrieveStaffByUserRoleIdQuery(urid));

    assertNotNull(out);
    assertEquals(urid, out.getUserRole().getId());
    verify(repo).findByUserRoleId(urid);
    verifyNoMoreInteractions(repo);
  }

  @Test
  void notFoundThrowsStaffNotFoundException() {
    var urid = UUID.randomUUID();
    when(repo.findByUserRoleId(urid)).thenReturn(Optional.empty());

    assertThrows(
        StaffNotFoundException.class,
        () -> handler.handle(new RetrieveStaffByUserRoleIdQuery(urid)));

    verify(repo).findByUserRoleId(urid);
    verifyNoMoreInteractions(repo);
  }

  @Test
  void nullUserRoleIdThrowsConstraintViolationAndSkipsRepo() {
    assertThrows(
        ConstraintViolationException.class,
        () -> handler.handle(new RetrieveStaffByUserRoleIdQuery(null)));
    verifyNoInteractions(repo);
  }
}
